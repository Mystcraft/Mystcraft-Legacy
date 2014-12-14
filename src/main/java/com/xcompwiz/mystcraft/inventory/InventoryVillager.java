package com.xcompwiz.mystcraft.inventory;

import com.xcompwiz.mystcraft.data.ModItems;

import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class InventoryVillager implements IInventory {
	public static final int	emerald_slot	= 0;
	public static final int	booster_slot	= 1;
	public static final int	boostercost		= 10;

	private ItemStack		inventory[]		= new ItemStack[1];

	private EntityVillager	villager;

	public InventoryVillager(EntityVillager villager) {
		this.villager = villager;
	}

	@Override
	public int getSizeInventory() {
		return inventory.length + 1;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		if (slot < inventory.length) { return inventory[slot]; }
		if (slot == booster_slot) { return getBoosterPacks(); }
		return null;
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		if (amount == 0) return null;
		if (slot < inventory.length) {
			if (inventory[slot] == null) return inventory[slot];
			amount = Math.min(amount, inventory[slot].stackSize);
			ItemStack copy = inventory[slot].copy();
			copy.stackSize = amount;
			inventory[slot].stackSize -= amount;
			if (inventory[slot].stackSize <= 0) inventory[slot] = null;
			return copy;
		}
		if (slot == booster_slot) { return removeBoosterPacks(amount); }
		return null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		if (slot < inventory.length) { return inventory[slot]; }
		return null;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack itemstack) {
		if (slot < inventory.length) {
			inventory[slot] = itemstack;
		}
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public void markDirty() {
		//TODO: Write back to villager
	}

	@Override
	public void openInventory() {}

	@Override
	public void closeInventory() {}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack itemstack) {
		if (slot == emerald_slot) return itemstack.getItem() == Items.emerald;
		return false;
	}

	private ItemStack getBoosterPacks() {
		//TODO: Read from villager
		if (inventory[emerald_slot] == null || inventory[emerald_slot].stackSize < boostercost) return null;
		return new ItemStack(ModItems.booster);
	}

	private ItemStack removeBoosterPacks(int amount) {
		//TODO: Read from villager
		if (inventory[emerald_slot] == null || inventory[emerald_slot].stackSize < boostercost) return null;
		inventory[emerald_slot].stackSize -= boostercost;
		if (inventory[emerald_slot].stackSize <= 0) {
			inventory[emerald_slot] = null;
		}
		return new ItemStack(ModItems.booster);
	}

	@Override
	public String getInventoryName() {
		return null;
	}

	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer p_70300_1_) {
		return false;
	}
}
