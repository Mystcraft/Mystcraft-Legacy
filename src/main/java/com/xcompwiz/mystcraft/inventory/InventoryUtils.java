package com.xcompwiz.mystcraft.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;

public abstract class InventoryUtils {

	/**
	 * Returns the index of the itemstack using object-EXACT matching (must be same instance)
	 * @param inventory to search in
	 * @param stack to search for
	 * @return index if found, -1 if not found or empty itemstack
	 */
	public static int findInInventory(IItemHandler inventory, @Nonnull ItemStack stack) {
		if (inventory == null) return -1;
		if (stack.isEmpty()) return -1;
		for (int i = 0; i < inventory.getSlots(); ++i) {
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
	public static int countInInventory(IItemHandler inventory, @Nonnull ItemStack match) {
		int count = 0;
		if (inventory == null) return count;
		for (int i = 0; i < inventory.getSlots(); ++i) {
			ItemStack itemstack = inventory.getStackInSlot(i);
			if (itemstack.isEmpty()) continue;
			if (match.isEmpty() || (itemstack.isItemEqual(match) && ItemStack.areItemStackTagsEqual(itemstack, match))) {
				count += Math.max(0, itemstack.getCount());
			}
		}
		return count;
	}

	public static int removeFromInventory(IItemHandlerModifiable inventory, @Nonnull ItemStack match, int amount) {
		if (inventory == null) return amount;
		for (int i = 0; i < inventory.getSlots() && amount > 0; ++i) {
			ItemStack itemstack = inventory.getStackInSlot(i);
			if (itemstack.isEmpty()) continue;
			if (itemstack.isItemEqual(match) && ItemStack.areItemStackTagsEqual(itemstack, match)) {
				int temp = Math.min(amount, itemstack.getCount());
				amount -= temp;
				itemstack.shrink(temp);
				if(itemstack.getCount() <= 0) {
					inventory.setStackInSlot(i, ItemStack.EMPTY);
				} else {
					inventory.setStackInSlot(i, itemstack); //update
				}
			}
		}
		return amount;
	}
}
