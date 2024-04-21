package com.xcompwiz.mystcraft.api.item;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IItemPageProvider extends IItemRenameable {

	/**
	 * Called to retrieve the list of pages in the itemstack. This can safely return null.
	 * @param player The current player
	 * @param itemstack The itemstack instance of this item
	 * @return A list of pages in the item
	 */
	List<ItemStack> getPageList(EntityPlayer player, ItemStack itemstack);

}
