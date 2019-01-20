package com.xcompwiz.mystcraft.inventory;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.xcompwiz.mystcraft.item.ItemAgebook;
import com.xcompwiz.mystcraft.network.IGuiMessageHandler;
import com.xcompwiz.mystcraft.network.MystcraftPacketHandler;
import com.xcompwiz.mystcraft.network.packet.MPacketGuiMessage;
import com.xcompwiz.mystcraft.tileentity.TileEntityLinkModifier;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

public class ContainerLinkModifier extends ContainerBase implements IGuiMessageHandler {

	public static class Messages {

		public static final String SetTitle = "SetTitle";
		public static final String SetFlag = "SetFlag";
		public static final String SetSeed = "SetSeed";
		public static final String RecycleDim = "RecycleDim";
		public static final String LinkDead = "LinkDead";

	}

	private TileEntityLinkModifier tileentity = null;
	private InventoryPlayer inventoryplayer;

	@Nonnull
	private String cached_title = "";
	private Boolean cached_deadlink = null;

	public ContainerLinkModifier(InventoryPlayer inventoryplayer, TileEntityLinkModifier tileentity) {
		this.tileentity = tileentity;
		this.inventoryplayer = inventoryplayer;
		updateSlots();
	}

	public void updateSlots() {
		this.inventorySlots.clear();
		this.inventoryItemStacks.clear();
		if (tileentity != null) {
			addSlotToContainer(new SlotFiltered((IItemHandlerModifiable) tileentity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.DOWN), tileentity, 0, 80, 35));
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
		SlotCollection internal = new SlotCollection(this, 0, 1);
		SlotCollection maininv = new SlotCollection(this, 1, 1 + 27);
		SlotCollection hotbar = new SlotCollection(this, 1 + 27, 1 + 27 + 9);

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
	public boolean canInteractWith(@Nonnull EntityPlayer entityplayer) {
		return true;
	}
	
	@Override
	public void detectAndSendChanges() {
		for (int slotId = 0; slotId < this.inventorySlots.size(); ++slotId) {
			ItemStack actual = this.inventorySlots.get(slotId).getStack();
			ItemStack stored = this.inventoryItemStacks.get(slotId);

			if (!ItemStack.areItemStacksEqual(stored, actual)) {
				if (slotId == 0) {
					cached_deadlink = null;
				}
			}
		}

		super.detectAndSendChanges();

		List<IMessage> packets = new ArrayList<>();
		if (cached_deadlink == null) {
			cached_deadlink = checkLinkDead();
			if (cached_deadlink != null) {
				NBTTagCompound nbttagcompound = new NBTTagCompound();
				nbttagcompound.setBoolean(Messages.LinkDead, cached_deadlink);
				packets.add(new MPacketGuiMessage(this.windowId, nbttagcompound));
			}
		}
		if (packets.size() > 0) {
			for (IContainerListener listener : this.listeners) {
				if (listener instanceof EntityPlayerMP) {
					for (IMessage message : packets) {
						MystcraftPacketHandler.CHANNEL.sendTo(message, (EntityPlayerMP) listener);
					}
				}
			}
		}
	}

	@Override
	public void processMessage(@Nonnull EntityPlayer player, @Nonnull NBTTagCompound data) {
		if (data.hasKey(Messages.LinkDead)) {
			cached_deadlink = data.getBoolean(Messages.LinkDead);
		}
		if (data.hasKey(Messages.RecycleDim)) {
			tileentity.recycleDimension();
			cached_deadlink = null;
		}
		if (data.hasKey(Messages.SetFlag)) {
			tileentity.setLinkOption(data.getString(Messages.SetFlag), data.getBoolean("Value"));
		}
		if (data.hasKey(Messages.SetSeed)) {
			String seedstr = data.getString(Messages.SetSeed);
			Long seed = null;
			if (seedstr.isEmpty()) {
				//XXX: Hardcoded string reference Agebook "Seed"
				tileentity.setLinkProperty("Seed", null);
			} else {
				try {
					seed = Long.parseLong(seedstr);
				} catch (Exception ignored) {
				}
			}
			if (seed != null) {
				//XXX: This isn't particularly generalized, but only this kind of item supports a seed atm.
				ItemStack book = tileentity.getBook();
				if (!book.isEmpty() && book.getItem() instanceof ItemAgebook) {
					ItemAgebook item = (ItemAgebook) tileentity.getBook().getItem();
					item.setSeed(player, tileentity.getBook(), seed);
				}
			}
		}
		if (data.hasKey(Messages.SetTitle)) {
			cached_title = data.getString(Messages.SetTitle);
			this.tileentity.setBookTitle(player, cached_title);
		}
	}

	public String getBookTitle() {
		String title = tileentity.getBookTitle();
		if (title == null) {
			title = "";
		}
		return title;
	}

	public boolean getLinkFlag(String id) {
		return tileentity.getLinkOption(id);
	}

	public String getLinkDimensionUID() {
		return tileentity.getLinkDimensionUID();
	}

	public String getItemSeed() {
		//XXX: Hardcoded string reference Agebook "Seed"
		return tileentity.getLinkProperty("Seed");
	}
	
	public boolean isLinkDead() {
		ItemStack book = tileentity.getBook();
		if (book == null) {
			cached_deadlink = null;
		}
		if (cached_deadlink == null)
			return false;
		return cached_deadlink;
	}

	private boolean checkLinkDead() {
		return tileentity.isLinkDimensionDead();
	}

	public boolean hasItemSeed() {
		//XXX: This isn't particularly generalized, but only this kind of item supports a seed atm.
		ItemStack book = tileentity.getBook();
		return !book.isEmpty() && book.getItem() instanceof ItemAgebook;
	}
}
