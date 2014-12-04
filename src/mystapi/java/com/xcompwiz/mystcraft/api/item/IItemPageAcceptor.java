package com.xcompwiz.mystcraft.api.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IItemPageAcceptor {
	/**
	 * Called when the player tries to add a page to the itemstack container. If you do not wish to accept the page, simply return the page.
	 * @param player The player adding the page
	 * @param itemstack The itemstack instance of this item
	 * @param page The page being added. Not guaranteed to be a page item. May have stacksize > 1.
	 * @return The result of the operation. The player's cursor item will be set to this.
	 */
	ItemStack addPage(EntityPlayer player, ItemStack itemstack, ItemStack page);
}
