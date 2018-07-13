package com.xcompwiz.mystcraft.api.impl.page;

import java.util.Collection;

import javax.annotation.Nonnull;

import com.xcompwiz.mystcraft.page.Page;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class PageAPIDelegate {

	public ResourceLocation getPageSymbol(@Nonnull ItemStack page) {
		return Page.getSymbol(page);
	}

	public boolean isPageWritable(@Nonnull ItemStack page) {
		return Page.isBlank(page);
	}

	public void setPageSymbol(@Nonnull ItemStack page, ResourceLocation symbol) {
		Page.setSymbol(page, symbol);
	}

	public Collection<String> getPageLinkProperties(@Nonnull ItemStack page) {
		return Page.getLinkProperties(page);
	}

	public boolean hasLinkPanel(@Nonnull ItemStack page) {
		return Page.isLinkPanel(page);
	}

}
