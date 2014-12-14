package com.xcompwiz.mystcraft.inventory;

import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.xcompwiz.mystcraft.network.IGuiMessageHandler;

public class ContainerVillagerShop extends ContainerBase implements IGuiMessageHandler {

	private InventoryPlayer		inventoryplayer;
	private EntityVillager		villager;
	private InventoryVillager	villagerinv;

	public ContainerVillagerShop(InventoryPlayer inventoryplayer, EntityVillager villager) {
		this.inventoryplayer = inventoryplayer;
		this.villager = villager;
		this.villagerinv = new InventoryVillager(villager);
		updateSlots();
	}

	public void updateSlots() {
		this.inventorySlots.clear();
		this.inventoryItemStacks.clear();
		addSlotToContainer(new SlotFiltered(villagerinv, InventoryVillager.booster_slot, 8, 9));
		addSlotToContainer(new SlotFiltered(villagerinv, InventoryVillager.emerald_slot, 8, 28));

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
		SlotCollection internal = null;
		SlotCollection maininv = null;
		SlotCollection hotbar = null;
		internal = new SlotCollection(this, 1, 2);
		maininv = new SlotCollection(this, 2, 2 + 27);
		hotbar = new SlotCollection(this, 2 + 27, 2 + 27 + 9);

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
	public boolean canInteractWith(EntityPlayer entityplayer) {
		if (villager.isDead) return false;
		return entityplayer.getDistanceSq(villager.posX, villager.posY, villager.posZ) <= 64D;
	}

	@Override
	public void processMessage(EntityPlayer player, NBTTagCompound nbttagcompound) {}

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
	}
}
