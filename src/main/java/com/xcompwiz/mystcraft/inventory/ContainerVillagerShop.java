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
		public static final String	PurchaseItem				= "PI";
	}

	private InventoryPlayer		inventoryplayer;
	private EntityVillager		villager;
	private InventoryVillager	villagerinv;

	private long				villagerinv_timestamp;
	private Integer				playerEmeralds;
	private long				lastEmeraldsUpdate;

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
			villagerinv_timestamp = villagerinv.getLastChanged();
			NBTTagCompound nbttagcompound = new NBTTagCompound();
			NBTTagCompound inventorynbt = new NBTTagCompound();
			villagerinv.writeToNBT(inventorynbt);
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
				this.villagerinv.readFromNBT(data.getCompoundTag(Messages.UpdateVillagerCollection));
			}
			return;
		}
		if (data.hasKey(Messages.PurchaseBooster)) {
			if (villagerinv.purchaseBooster(inventoryplayer)) getPlayerEmeralds(true);
			return;
		}
		if (data.hasKey(Messages.PurchaseItem)) {
			int index = data.getInteger(Messages.PurchaseItem);
			if (villagerinv.purchaseShopItem(inventoryplayer, index)) getPlayerEmeralds(true);
			return;
		}
	}

	/**
	 * Called when the container is closed.
	 */
	@Override
	public void onContainerClosed(EntityPlayer player) {
		super.onContainerClosed(player);
		VillagerTradeSystem.release(villagerinv);
		if (this.inventoryplayer.player.worldObj.isRemote) return;
	}

	public ItemStack getShopItem(int index) {
		return villagerinv.getShopItem(index);
	}

	public int getShopItemPrice(int index) {
		return villagerinv.getShopItemPrice(index);
	}

	public int getBoosterCount() {
		return villagerinv.getBoosterCount();
	}

	public int getBoosterCost() {
		return villagerinv.getBoosterCost();
	}

	public int getPlayerEmeralds() {
		return getPlayerEmeralds(false);
	}

	private int getPlayerEmeralds(boolean forceupdate) {
		if (playerEmeralds == null || forceupdate || lastEmeraldsUpdate + 100 < villager.worldObj.getTotalWorldTime()) {
			lastEmeraldsUpdate = villager.worldObj.getTotalWorldTime();
			playerEmeralds = villagerinv.getPlayerEmeralds(this.inventoryplayer);
		}
		return playerEmeralds;
	}
}
