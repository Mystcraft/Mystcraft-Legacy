package com.xcompwiz.mystcraft.inventory;

import java.util.Collection;

import net.minecraft.item.ItemStack;

import com.xcompwiz.mystcraft.api.linking.ILinkInfo;

//XXX: This probably needs to be made into a gui interface
public interface IBookContainer {

	public abstract ItemStack getCurrentPage();
	public abstract int getCurrentPageIndex();
	public abstract void setCurrentPageIndex(int currentpage);

	public abstract int getPageCount();

	public abstract boolean hasBookSlot();

	public abstract boolean isTargetWorldVisited();

	public abstract ItemStack getBook();

	public abstract Collection<String> getBookAuthors();

	public abstract boolean isLinkPermitted();

	public abstract ILinkInfo getLinkInfo();

	public abstract String getBookTitle();
}
