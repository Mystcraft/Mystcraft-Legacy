package com.xcompwiz.mystcraft.api.impl.page;

import java.util.Collection;

import net.minecraft.item.ItemStack;

import com.xcompwiz.mystcraft.oldapi.internal.IPageAPI;
import com.xcompwiz.mystcraft.page.Page;

public class PageAPIDelegate implements IPageAPI {

	@Override
	public String getPageSymbol(ItemStack page) {
		return Page.getSymbol(page);
	}

	@Override
	public boolean isPageWritable(ItemStack page) {
		return Page.isBlank(page);
	}

	@Override
	public void setPageSymbol(ItemStack page, String symbol) {
		Page.setSymbol(page, symbol);
	}

	@Override
	public Collection<String> getPageLinkProperties(ItemStack page) {
		return Page.getLinkProperties(page);
	}

	@Override
	public boolean hasLinkPanel(ItemStack page) {
		return Page.isLinkPanel(page);
	}

}
