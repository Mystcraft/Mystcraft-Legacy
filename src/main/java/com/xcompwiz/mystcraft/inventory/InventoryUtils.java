package com.xcompwiz.mystcraft.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public abstract class InventoryUtils {

	/**
	 * Returns the index of the itemstack using EXACT matching (must be same instance) 
	 * @param inventory
	 * @param stack
	 * @return index
	 */
	public static int findInInventory(IInventory inventory, ItemStack stack) {
		if (inventory == null) return -1;
		if (stack == null) return -1;
		for (int i = 0; i < inventory.getSizeInventory(); ++i) {
			if (inventory.getStackInSlot(i) == stack) return i;
		}
		return -1;
	}

	/**
	 * Returns the count of how many items the inventory has (considers stack size).
	 * @param inventory The inventory to scan
	 * @param match The item to look for. If null, counts all items.
	 * @return The number of the found item
	 */
	public static int countInInventory(IInventory inventory, ItemStack match) {
		int count = 0;
		if (inventory == null) return count;
		for (int i = 0; i < inventory.getSizeInventory(); ++i) {
			ItemStack itemstack = inventory.getStackInSlot(i);
			if (itemstack == null) continue;
			if (match == null || (itemstack.isItemEqual(match) && ItemStack.areItemStackTagsEqual(itemstack, match))) count += Math.max(0, itemstack.stackSize);
		}
		return count;
	}

	public static int removeFromInventory(IInventory inventory, ItemStack match, int amount) {
		if (inventory == null) return amount;
		for (int i = 0; i < inventory.getSizeInventory() && amount > 0; ++i) {
			ItemStack itemstack = inventory.getStackInSlot(i);
			if (itemstack == null) continue;
			if (itemstack.isItemEqual(match) && ItemStack.areItemStackTagsEqual(itemstack, match)) {
				int temp = Math.min(amount, itemstack.stackSize);
				amount -= temp;
				itemstack.stackSize -= temp;
				if (itemstack.stackSize <= 0) inventory.setInventorySlotContents(i, null);
			}
		}
		return amount;
	}
}
