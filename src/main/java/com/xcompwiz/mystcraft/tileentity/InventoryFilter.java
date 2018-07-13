package com.xcompwiz.mystcraft.tileentity;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;

/**
 * Implement this to filter if an item is applicable to be set in a specific slot in a {@link IOInventory} Apply the filter to specific slots via
 * {@link IOInventory#applyFilter(InventoryFilter, int...)}
 */
public interface InventoryFilter {

	public boolean canAcceptItem(int slot, @Nonnull ItemStack stack);

}
