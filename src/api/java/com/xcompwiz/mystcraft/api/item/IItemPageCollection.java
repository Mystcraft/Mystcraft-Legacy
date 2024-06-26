package com.xcompwiz.mystcraft.api.item;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This interface allows you to create your own items which the Mystcraft writing desk can use as page sources (the tabs on the left). Have your items implement
 * it to allow the desk to get pages from them for writing.
 * @author xcompwiz
 */
public interface IItemPageCollection extends IItemRenameable, IItemPageAcceptor {

	/**
	 * Called when the player tries to remove a page from the itemstack. If you do not wish this to be possible, simply return null.
	 * @param player The player getting the page
	 * @param itemstack The itemstack instance of the item the page is being removed from
	 * @param page The (page) itemstack to attempt to remove
	 * @return The page retrieved as an itemstack
	 */
	@Nonnull
	ItemStack remove(EntityPlayer player, @Nonnull ItemStack itemstack, @Nonnull ItemStack page);

	/**
	 * Returns the collection of pages in this collection
	 * @param itemstack The itemstack instance of this item
	 * @return The collection of pages in the item
	 */
	@Nullable
	List<ItemStack> getItems(EntityPlayer player, @Nonnull ItemStack itemstack);
}
