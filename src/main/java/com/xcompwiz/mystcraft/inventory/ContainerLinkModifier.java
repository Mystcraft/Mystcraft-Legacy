package com.xcompwiz.mystcraft.inventory;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;

import com.xcompwiz.mystcraft.network.IGuiMessageHandler;
import com.xcompwiz.mystcraft.network.packet.MPacketGuiMessage;
import com.xcompwiz.mystcraft.tileentity.TileEntityLinkModifier;

public class ContainerLinkModifier extends ContainerBase implements IGuiMessageHandler {
	public static class Messages {

		public static final String	SetTitle	= "SetTitle";
		public static final String	SetFlag		= "SetFlag";
	}

	private TileEntityLinkModifier	tileentity		= null;
	private InventoryPlayer			inventoryplayer;

	private String					cached_title	= "";

	public ContainerLinkModifier(InventoryPlayer inventoryplayer, TileEntityLinkModifier tileentity) {
		this.tileentity = tileentity;
		this.inventoryplayer = inventoryplayer;
		updateSlots();
	}

	public void updateSlots() {
		this.inventorySlots.clear();
		this.inventoryItemStacks.clear();
		if (tileentity != null) {
			addSlotToContainer(new SlotFiltered(tileentity, 0, 80, 35));
			int i;
			for (i = 0; i < 3; ++i) {
				for (int j = 0; j < 9; ++j) {
					this.addSlotToContainer(new Slot(inventoryplayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
				}
			}

			for (i = 0; i < 9; ++i) {
				this.addSlotToContainer(new Slot(inventoryplayer, i, 8 + i * 18, 142));
			}
		}

		collections.clear();
		SlotCollection internal = null;
		SlotCollection maininv = null;
		SlotCollection hotbar = null;
		internal = new SlotCollection(this, 0, 1);
		maininv = new SlotCollection(this, 1, 1 + 27);
		hotbar = new SlotCollection(this, 1 + 27, 1 + 27 + 9);

		internal.pushTargetFront(maininv);
		internal.pushTargetFront(hotbar);
		maininv.pushTargetFront(hotbar);
		maininv.pushTargetFront(internal);
		hotbar.pushTargetFront(maininv);
		hotbar.pushTargetFront(internal);

		collections.add(internal);
		collections.add(maininv);
		collections.add(hotbar);
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		List<Packet> packets = new ArrayList<Packet>();
		String temp = tileentity.getBookTitle();
		if (this.cached_title != temp) {
			cached_title = temp;

			NBTTagCompound nbttagcompound = new NBTTagCompound();
			nbttagcompound.setString(Messages.SetTitle, cached_title);
			packets.add(MPacketGuiMessage.createPacket(this.windowId, nbttagcompound));
		}
		if (packets.size() > 0) {
			for (int var4 = 0; var4 < this.crafters.size(); ++var4) {
				ICrafting crafter = ((ICrafting) this.crafters.get(var4));
				if (crafter instanceof EntityPlayerMP) {
					EntityPlayerMP player = (EntityPlayerMP) crafter;
					for (Packet pkt : packets) {
						player.playerNetServerHandler.sendPacket(pkt);
					}
				}
			}
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		if (tileentity == null) return true;
		return tileentity.isUseableByPlayer(entityplayer);
	}

	@Override
	public void processMessage(EntityPlayer player, NBTTagCompound data) {
		if (data.hasKey(Messages.SetFlag)) {
			tileentity.setLinkOption(data.getString(Messages.SetFlag), data.getBoolean("Value"));
		}
		if (data.hasKey(Messages.SetTitle)) {
			cached_title = data.getString(Messages.SetTitle);
			this.tileentity.setBookTitle(player, cached_title);
		}
	}

	public Object getBook() {
		return tileentity.getStackInSlot(0);
	}

	public String getBookTitle() {
		return cached_title;
	}

	public boolean getLinkFlag(String id) {
		return tileentity.getLinkOption(id);
	}

	public String getLinkDimensionUID() {
		return tileentity.getLinkDimensionUID();
	}
}
