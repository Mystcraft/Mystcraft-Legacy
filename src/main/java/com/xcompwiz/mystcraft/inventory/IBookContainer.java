package com.xcompwiz.mystcraft.inventory;

import java.util.Collection;

import net.minecraft.item.ItemStack;

import com.xcompwiz.mystcraft.api.linking.ILinkInfo;

// XXX: Refactor this into helpers and multiple interfaces
// Keep getBook
// isLinkPermitted is a server to client thing, so isn't part of book (natively)
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
