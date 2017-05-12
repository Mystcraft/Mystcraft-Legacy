package com.xcompwiz.mystcraft.api.impl.page;

import java.util.Collection;

import com.xcompwiz.mystcraft.page.Page;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class PageAPIDelegate {

	public String getPageSymbol(@Nonnull ItemStack page) {
		return Page.getSymbol(page);
	}

	public boolean isPageWritable(@Nonnull ItemStack page) {
		return Page.isBlank(page);
	}

	public void setPageSymbol(@Nonnull ItemStack page, String symbol) {
		Page.setSymbol(page, symbol);
	}

	public Collection<String> getPageLinkProperties(@Nonnull ItemStack page) {
		return Page.getLinkProperties(page);
	}

	public boolean hasLinkPanel(@Nonnull ItemStack page) {
		return Page.isLinkPanel(page);
	}

}
