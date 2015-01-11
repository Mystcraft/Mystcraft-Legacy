package com.xcompwiz.mystcraft.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public abstract class InventoryUtils {

	public static int findInInventory(IInventory inventory, ItemStack stack) {
		if (inventory == null) return -1;
		if (stack == null) return -1;
		for (int i = 0; i < inventory.getSizeInventory(); ++i) {
			if (inventory.getStackInSlot(i) == stack) return i;
		}
		return -1;
	}
}
