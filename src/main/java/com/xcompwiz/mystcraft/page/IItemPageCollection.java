package com.xcompwiz.mystcraft.page;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import com.xcompwiz.mystcraft.item.IItemRenameable;

/**
 * This interface allows you to create your own items which the Mystcraft writing desk can use as page sources (the tabs on the left). Have your items implement
 * it to allow the desk to get pages from them for writing.
 * @author xcompwiz
 */
public interface IItemPageCollection extends IItemRenameable {

	/**
	 * Called when the player tries to remove a page from the itemstack. If you do not wish this to be possible, simply return null.
	 * @param player The player getting the page
	 * @param itemstack The itemstack instance of the item the page is being removed from
	 * @param page The (page) itemstack to attempt to remove
	 * @return The page retrieved as an itemstack
	 */
	ItemStack remove(EntityPlayer player, ItemStack itemstack, ItemStack page);

	/**
	 * Called when the player tries to add a page to the itemstack container. If you do not wish to accept the page, simply return the page.
	 * @param player The player adding the page
	 * @param itemstack The itemstack instance of this item
	 * @param page The page being added. Not guaranteed to be a page item. May have stacksize > 1.
	 * @return The result of the operation. The player's cursor item will be set to this.
	 */
	ItemStack add(EntityPlayer player, ItemStack itemstack, ItemStack page);

	//FIXME: !!(PageSorting) This isn't very future proof.  Collections won't be itemstacks in the future. 
	/**
	 * Returns the collection of pages in this collection
	 * @param itemstack The itemstack instance of this item
	 * @return The collection of pages in the item
	 */
	List<ItemStack> getItems(EntityPlayer player, ItemStack itemstack);
}
