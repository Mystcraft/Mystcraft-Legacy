package com.xcompwiz.mystcraft.api.impl.page;

import java.util.Collection;

import com.xcompwiz.mystcraft.page.Page;

import net.minecraft.item.ItemStack;

public class PageAPIDelegate {

	public String getPageSymbol(ItemStack page) {
		return Page.getSymbol(page);
	}

	public boolean isPageWritable(ItemStack page) {
		return Page.isBlank(page);
	}

	public void setPageSymbol(ItemStack page, String symbol) {
		Page.setSymbol(page, symbol);
	}

	public Collection<String> getPageLinkProperties(ItemStack page) {
		return Page.getLinkProperties(page);
	}

	public boolean hasLinkPanel(ItemStack page) {
		return Page.isLinkPanel(page);
	}

}
