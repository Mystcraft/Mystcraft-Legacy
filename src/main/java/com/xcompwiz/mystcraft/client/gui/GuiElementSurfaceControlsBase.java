package com.xcompwiz.mystcraft.client.gui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.xcompwiz.mystcraft.api.item.IItemPageCollection;
import com.xcompwiz.mystcraft.api.item.IItemPageProvider;
import com.xcompwiz.mystcraft.client.gui.element.GuiElement;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementButton;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementButton.IGuiOnClickHandler;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementButtonToggle.IGuiStateProvider;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementPageSurface;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementPageSurface.IGuiPositionedPagesProvider;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementPageSurface.PositionableItem;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementTextField;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementTextField.IGuiOnTextChange;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementTextField.IGuiTextProvider;
import com.xcompwiz.mystcraft.page.Page;
import com.xcompwiz.mystcraft.page.SortingUtils;
import com.xcompwiz.mystcraft.symbol.IAgeSymbol;
import com.xcompwiz.mystcraft.symbol.SymbolManager;

public abstract class GuiElementSurfaceControlsBase implements IGuiPositionedPagesProvider, IGuiOnClickHandler, IGuiStateProvider, IGuiTextProvider, IGuiOnTextChange {
	private Minecraft				mc;
	private float					xSize;

	private ItemStack				cached_tabitem;
	private List<PositionableItem>	arranged_pages;
	private Comparator<ItemStack>	sorttype		= SortingUtils.ComparatorItemSymbolAlphabetical.instance;
	private boolean					showall			= false;
	private Collection<GuiElement>	surfaceelements	= new HashSet<GuiElement>();

	public GuiElementSurfaceControlsBase(Minecraft mc, int guiLeft, int guiTop, int width, int height) {
		this.mc = mc;
		this.xSize = width;
	}

	public abstract ItemStack getItemStack();

	@Override
	public List<PositionableItem> getPositionedPages() {
		ItemStack itemstack = getItemStack();
		if (!ItemStack.areItemStacksEqual(itemstack, cached_tabitem)) {
			if (itemstack == null) {
				cached_tabitem = null;
				arranged_pages = null;
				return null;
			}
			cached_tabitem = itemstack.copy();
			updateCollection();
		}
		return arranged_pages;
	}

	public void addSurfaceElement(GuiElement elem) {
		this.surfaceelements.add(elem);
	}

	private void updateCollection() {
		arranged_pages = null;
		if (cached_tabitem == null) {
			return;
		}
		boolean iscollection = cached_tabitem.getItem() instanceof IItemPageCollection;
		for (GuiElement element : surfaceelements) {
			element.setEnabled(iscollection);
		}
		if (iscollection) {
			IItemPageCollection item = (IItemPageCollection) cached_tabitem.getItem();
			Map<NBTTagCompound, PositionableItem> collection = new HashMap<NBTTagCompound, PositionableItem>();
			List<ItemStack> pages = item.getItems(mc.thePlayer, cached_tabitem);
			if (pages != null) {
				for (ItemStack page : pages) {
					//XXX: Filters
					String displayname = null;
					if (Page.getSymbol(page) != null) {
						IAgeSymbol symbol = SymbolManager.getAgeSymbol(Page.getSymbol(page));
						if (symbol != null) displayname = symbol.displayName();
						if (displayname == null) displayname = Page.getSymbol(page);
					}
					if (searchtext != null && searchtext.length() > 0) {
						if (displayname == null || !displayname.toLowerCase().contains(searchtext.toLowerCase())) {
							continue;
						}
					}
					ItemStack copy = page.copy();
					copy.stackSize = 1;
					NBTTagCompound key = new NBTTagCompound();
					copy.writeToNBT(key);
					PositionableItem newpos = collection.get(key);
					if (newpos == null) {
						newpos = new PositionableItem();
						newpos.itemstack = page;
						newpos.count = page.stackSize;
						collection.put(key, newpos);
					} else {
						newpos.count += page.stackSize;
					}
				}
			}
			if (showall) {
				Collection<IAgeSymbol> symbols = SymbolManager.getAgeSymbols();
				for (IAgeSymbol symbol : symbols) {
					String symbolname = symbol.identifier();
					//XXX: Filters
					String displayname = symbol.displayName();
					if (searchtext != null && searchtext.length() > 0) {
						if (displayname == null || !displayname.toLowerCase().contains(searchtext.toLowerCase())) {
							continue;
						}
					}
					ItemStack page = Page.createSymbolPage(symbolname);
					NBTTagCompound key = new NBTTagCompound();
					page.writeToNBT(key);
					PositionableItem newpos = collection.get(key);
					if (newpos == null) {
						newpos = new PositionableItem();
						newpos.itemstack = page;
						newpos.count = 0;
						collection.put(key, newpos);
					}
				}
			}
			arranged_pages = new LinkedList<PositionableItem>();
			arranged_pages.addAll(collection.values());
			sort(sorttype);
		} else if (cached_tabitem.getItem() instanceof IItemPageProvider) {
			int i = 0;

			IItemPageProvider item = (IItemPageProvider) cached_tabitem.getItem();
			List<ItemStack> pages = item.getPageList(mc.thePlayer, cached_tabitem);
			arranged_pages = new LinkedList<PositionableItem>();
			for (ItemStack page : pages) {
				PositionableItem newpos = new PositionableItem();
				newpos.itemstack = page;
				newpos.slotId = i++;
				newpos.count = 1;
				arranged_pages.add(newpos);
			}
		}
		if (arranged_pages != null) arrange();
	}

	public void arrange() {
		float xStep = GuiElementPageSurface.pagewidth + 1;
		float yStep = GuiElementPageSurface.pageheight + 1;
		float x = 0;
		float y = 0;
		for (PositionableItem page : arranged_pages) {
			page.x = x;
			page.y = y;
			x += xStep;
			if (x + xStep > this.xSize) {
				x = 0;
				y += yStep;
			}
		}
	}

	public void sort(final Comparator<ItemStack> comparator) {
		Collections.sort(arranged_pages, new Comparator<PositionableItem>() {
			@Override
			public int compare(PositionableItem arg0, PositionableItem arg1) {
				return comparator.compare(arg0.itemstack, arg1.itemstack);
			}
		});
	}

	@Override
	public void onClick(GuiElementButton caller) {
		if (caller.getId().equals("AZ")) {
			sorttype = SortingUtils.ComparatorItemSymbolAlphabetical.instance;
			updateCollection();
		} else if (caller.getId().equals("ALL")) {
			showall = !showall;
			updateCollection();
		}
	}

	@Override
	public boolean getState(String id) {
		if (id.equals("AZ")) return sorttype == SortingUtils.ComparatorItemSymbolAlphabetical.instance;
		if (id.equals("ALL")) return showall;
		return false;
	}

	private List<IGuiOnTextChange>	listeners	= new ArrayList<IGuiOnTextChange>();
	private String					searchtext	= null;

	@Override
	public String getText(GuiElementTextField caller) {
		return searchtext;
	}

	@Override
	public void onTextChange(GuiElementTextField caller, String newtext) {
		searchtext = newtext;
		if (cached_tabitem != null && cached_tabitem.getItem() instanceof IItemPageCollection) {
			this.updateCollection();
		}
		for (IGuiOnTextChange listener : listeners) {
			listener.onTextChange(caller, searchtext);
		}
	}

	public void addListener(IGuiOnTextChange listener) {
		listeners.add(listener);
	}
}
