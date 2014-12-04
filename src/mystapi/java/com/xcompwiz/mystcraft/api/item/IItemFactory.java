package com.xcompwiz.mystcraft.api.item;

import net.minecraft.item.ItemStack;

/**
 * Provides a set of functions for building Mystcraft items. Do NOT implement this yourself!
 * @author xcompwiz
 */
public interface IItemFactory {

	/**
	 * @return A blank Mystcraft page itemstack
	 */
	public ItemStack buildPage();

	/**
	 * Returns a page with the given symbol on it. Note: this succeeds even if the symbol is not a properly registered symbol.
	 * @param identifier The identifier of the symbol
	 * @return The page item
	 */
	public ItemStack buildSymbolPage(String identifier);

	/**
	 * Builds a link panel page with the specified properties. Note: no filtering is applied to the link properties listed.
	 * @param properties The list of properties to add
	 * @return The link panel page
	 */
	public ItemStack buildLinkPage(String... properties);

	/**
	 * Builds a collection item (portfolio) containing pages for all of the symbols produced directly from the provided grammar tokens.
	 * @param name The name of the collection
	 * @param tokens The list of grammar tokens to expand for symbols
	 * @return The collection itemstack
	 */
	public ItemStack buildCollectionItem(String name, String... tokens);

	/**
	 * Builds a collection item (portfolio) containing the provided pages. This clones the itemstacks of the pages and will only add items which can be put into
	 * page collections.
	 * @param name The name of the collection
	 * @param pages The list of page itemstacks to add
	 * @return The collection itemstack
	 */
	public ItemStack buildCollectionItem(String name, ItemStack... pages);
}
