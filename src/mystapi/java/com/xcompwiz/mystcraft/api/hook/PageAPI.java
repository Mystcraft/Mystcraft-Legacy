package com.xcompwiz.mystcraft.api.hook;

import java.util.Collection;

import net.minecraft.item.ItemStack;

import com.xcompwiz.mystcraft.api.APIInstanceProvider;

/**
 * Provides methods for accessing and setting information in pages. The implementation of this is provided by {@link APIInstanceProvider}. Look at the {@link ItemFactory} for
 * methods for building page items.
 * @author xcompwiz
 */
public interface PageAPI {

	/**
	 * Returns if the page has a link panel on it
	 * @param page The page to query
	 * @return True if the page has a link panel
	 */
	public boolean hasLinkPanel(ItemStack page);

	/**
	 * Returns the link properties of the page. Returns an empty list if no properties, or null if not a link panel.
	 * @param page The page to query
	 * @return Null is not a linking panel, otherwise a list of all the link properties on the panel. The list may be empty.
	 */
	public Collection<String> getPageLinkProperties(ItemStack page);

	/**
	 * Returns if a symbol can be written to the page
	 * @param page The page to query
	 * @return True if the page is writable
	 */
	public boolean isPageWritable(ItemStack page);

	/**
	 * Gets the symbol identifier on a page. Returns null if there is not symbol on the page.
	 * @param page The page to query
	 * @return The symbol identifier or null
	 */
	public String getPageSymbol(ItemStack page);

	/**
	 * Sets the symbol on the page.
	 * @param page The page to affect
	 * @param symbol The identifier of the symbol to apply
	 */
	public void setPageSymbol(ItemStack page, String symbol);
}
