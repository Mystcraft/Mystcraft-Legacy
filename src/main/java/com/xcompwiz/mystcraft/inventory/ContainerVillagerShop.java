package com.xcompwiz.mystcraft.inventory;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;

import com.xcompwiz.mystcraft.network.IGuiMessageHandler;
import com.xcompwiz.mystcraft.network.packet.MPacketGuiMessage;
import com.xcompwiz.mystcraft.villager.VillagerTradeSystem;

public class ContainerVillagerShop extends ContainerBase implements IGuiMessageHandler {
	public static class Messages {
		public static final String	UpdateVillagerCollection	= "UVC";
		public static final String	PurchaseBooster				= "PB";
	}

	private InventoryPlayer		inventoryplayer;
	private EntityVillager		villager;
	private InventoryVillager	villagerinv;

	private long				villagerinv_timestamp;

	public ContainerVillagerShop(InventoryPlayer inventoryplayer, EntityVillager villager) {
		this.inventoryplayer = inventoryplayer;
		this.villager = villager;
		this.villagerinv = VillagerTradeSystem.getVillagerInventory(villager);
		updateSlots();
	}

	public void updateSlots() {
		this.inventorySlots.clear();
		this.inventoryItemStacks.clear();
		int i;
		for (i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				this.addSlotToContainer(new Slot(inventoryplayer, j + i * 9 + 9, 8 + j * 18, 99 + i * 18));
			}
		}

		for (i = 0; i < 9; ++i) {
			this.addSlotToContainer(new Slot(inventoryplayer, i, 8 + i * 18, 157));
		}

		collections.clear();
		SlotCollection maininv = null;
		SlotCollection hotbar = null;
		maininv = new SlotCollection(this, 0, 0 + 27);
		hotbar = new SlotCollection(this, 0 + 27, 0 + 27 + 9);

		maininv.pushTargetFront(hotbar);
		hotbar.pushTargetFront(maininv);

		collections.add(maininv);
		collections.add(hotbar);
	}

	@Override
	public void detectAndSendChanges() {
		List<Packet> packets = new ArrayList<Packet>();
		for (int slotId = 0; slotId < this.inventorySlots.size(); ++slotId) {
			ItemStack actual = ((Slot) this.inventorySlots.get(slotId)).getStack();
			ItemStack stored = (ItemStack) this.inventoryItemStacks.get(slotId);

			if (!ItemStack.areItemStacksEqual(stored, actual)) {
				stored = actual == null ? null : actual.copy();
				this.inventoryItemStacks.set(slotId, stored);

				for (int var4 = 0; var4 < this.crafters.size(); ++var4) {
					((ICrafting) this.crafters.get(var4)).sendSlotContents(this, slotId, stored);
				}
			}
		}
		if (this.villagerinv_timestamp != villagerinv.getLastChanged()) {
			NBTTagCompound nbttagcompound = new NBTTagCompound();
			NBTTagCompound inventorynbt = new NBTTagCompound();
			//TODO: if (villagerinv != null) villagerinv.writeToNBT(inventorynbt);
			nbttagcompound.setTag(Messages.UpdateVillagerCollection, inventorynbt);
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
		if (villager.isDead) return false;
		return entityplayer.getDistanceSq(villager.posX, villager.posY, villager.posZ) <= 64D;
	}

	@Override
	public void processMessage(EntityPlayer player, NBTTagCompound data) {
		if (data.hasKey(Messages.UpdateVillagerCollection)) {
			if (this.villager.worldObj.isRemote) { //Only allow the updates to the client this way
				//TODO: this.villagerinv.readFromNBT(data.getCompoundTag(Messages.UpdateVillagerCollection));
			}
		}
	}

	/**
	 * Called when the container is closed.
	 */
	@Override
	public void onContainerClosed(EntityPlayer player) {
		super.onContainerClosed(player);

		if (this.inventoryplayer.player.worldObj.isRemote) return;

		ItemStack itemstack = this.villagerinv.getStackInSlotOnClosing(InventoryVillager.emerald_slot);

		if (itemstack != null) {
			player.dropPlayerItemWithRandomChoice(itemstack, false);
		}

		itemstack = this.villagerinv.getStackInSlotOnClosing(1);

		if (itemstack != null) {
			player.dropPlayerItemWithRandomChoice(itemstack, false);
		}
		VillagerTradeSystem.release(villagerinv);
	}
}
