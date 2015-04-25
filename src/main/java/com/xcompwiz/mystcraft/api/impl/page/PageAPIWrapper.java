package com.xcompwiz.mystcraft.api.impl.page;

import java.util.Collection;

import net.minecraft.item.ItemStack;

import com.xcompwiz.mystcraft.api.hook.PageAPI;
import com.xcompwiz.mystcraft.api.impl.APIWrapper;
import com.xcompwiz.mystcraft.api.impl.InternalAPI;

public class PageAPIWrapper extends APIWrapper implements PageAPI {

	public PageAPIWrapper(String modname) {
		super(modname);
	}

	@Override
	public boolean hasLinkPanel(ItemStack page) {
		return InternalAPI.page.hasLinkPanel(page);
	}

	@Override
	public Collection<String> getPageLinkProperties(ItemStack page) {
		return InternalAPI.page.getPageLinkProperties(page);
	}

	@Override
	public boolean isPageWritable(ItemStack page) {
		return InternalAPI.page.isPageWritable(page);
	}

	@Override
	public String getPageSymbol(ItemStack page) {
		return InternalAPI.page.getPageSymbol(page);
	}

	@Override
	public void setPageSymbol(ItemStack page, String symbol) {
		InternalAPI.page.setPageSymbol(page, symbol);		
	}

}
