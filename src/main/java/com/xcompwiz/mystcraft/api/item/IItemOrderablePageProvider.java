package com.xcompwiz.mystcraft.api.item;


import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * This interface allows you to create your own items which the Mystcraft writing desk can arrange on the writing desk.
 * @author xcompwiz
 */
public interface IItemOrderablePageProvider extends IItemPageProvider, IItemPageAcceptor {

	/**
	 * @param player
	 * @param folder
	 * @param page
	 * @param index
	 * @return Returns the itemstack of the item which occupied the index, or null
	 */
	ItemStack setPage(EntityPlayer player, ItemStack folder, ItemStack page, int index);

	/**
	 * @param player
	 * @param folder
	 * @param page
	 * @param index
	 * @return Returns the itemstack of the item which occupies the index, or null
	 */
	ItemStack removePage(EntityPlayer player, ItemStack folder, int index);

}
