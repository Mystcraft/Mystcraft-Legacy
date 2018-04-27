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

import com.xcompwiz.mystcraft.api.item.IItemPageCollection;
import com.xcompwiz.mystcraft.api.item.IItemPageProvider;
import com.xcompwiz.mystcraft.api.symbol.IAgeSymbol;
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
import com.xcompwiz.mystcraft.symbol.SymbolManager;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public abstract class GuiElementSurfaceControlsBase implements IGuiPositionedPagesProvider, IGuiOnClickHandler, IGuiStateProvider, IGuiTextProvider, IGuiOnTextChange {

	private Minecraft				mc;
	private float					xSize;

	@Nonnull
	private ItemStack				cached_tabitem = ItemStack.EMPTY;
	private List<PositionableItem>	arranged_pages;
	private Comparator<ItemStack>	sorttype		= SortingUtils.ComparatorItemSymbolAlphabetical.instance;
	private boolean					showall			= false;
	private Collection<GuiElement>	surfaceelements	= new HashSet<>();

	public GuiElementSurfaceControlsBase(Minecraft mc, int guiLeft, int guiTop, int width, int height) {
		this.mc = mc;
		this.xSize = width;
	}

	@Nonnull
	public abstract ItemStack getItemStack();

	@Override
	public List<PositionableItem> getPositionedPages() {
		ItemStack itemstack = getItemStack();
		if (!ItemStack.areItemStacksEqual(itemstack, cached_tabitem)) {
			if (itemstack.isEmpty()) {
				cached_tabitem = ItemStack.EMPTY;
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
		if (cached_tabitem.isEmpty()) {
			return;
		}
		boolean iscollection = cached_tabitem.getItem() instanceof IItemPageCollection;
		for (GuiElement element : surfaceelements) {
			element.setEnabled(iscollection);
		}
		if (iscollection) {
			IItemPageCollection item = (IItemPageCollection) cached_tabitem.getItem();
			Map<NBTTagCompound, PositionableItem> collection = new HashMap<NBTTagCompound, PositionableItem>();
			List<ItemStack> pages = item.getItems(mc.player, cached_tabitem);
			if (pages != null) {
				for (ItemStack page : pages) {
					if (page.getTagCompound() != null) {
						//XXX: Filters
						String displayname = null;
						if (Page.getSymbol(page) != null) {
							IAgeSymbol symbol = SymbolManager.getAgeSymbol(Page.getSymbol(page));
							if (symbol != null) displayname = symbol.getLocalizedName();
							if (displayname == null) {
								ResourceLocation id = Page.getSymbol(page);
								if(id != null) {
									displayname = id.getResourcePath();
								}
							}
						}
						if (searchtext != null && searchtext.length() > 0) {
							if (displayname == null || !displayname.toLowerCase().contains(searchtext.toLowerCase())) {
								continue;
							}
						}
					}
					ItemStack copy = page.copy();
					copy.setCount(1);
					NBTTagCompound key = new NBTTagCompound();
					copy.writeToNBT(key);
					PositionableItem newpos = collection.get(key);
					if (newpos == null) {
						newpos = new PositionableItem();
						newpos.itemstack = page;
						newpos.count = page.getCount();
						collection.put(key, newpos);
					} else {
						newpos.count += page.getCount();
					}
				}
			}
			if (showall) {
				Collection<IAgeSymbol> symbols = SymbolManager.getAgeSymbols();
				for (IAgeSymbol symbol : symbols) {
					ResourceLocation symbolname = symbol.getRegistryName();
					//XXX: Filters
					String displayname = symbol.getLocalizedName();
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
			arranged_pages = new LinkedList<>();
			arranged_pages.addAll(collection.values());
			sort(sorttype);
		} else if (cached_tabitem.getItem() instanceof IItemPageProvider) {
			int i = 0;

			IItemPageProvider item = (IItemPageProvider) cached_tabitem.getItem();
			List<ItemStack> pages = item.getPageList(mc.player, cached_tabitem);
			arranged_pages = new LinkedList<>();
			for (ItemStack page : pages) {
				PositionableItem newpos = new PositionableItem();
				newpos.itemstack = page;
				newpos.slotId = i++;
				newpos.count = 1;
				arranged_pages.add(newpos);
			}
		}
		if (arranged_pages != null) {
			arrange();
		}
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
		arranged_pages.sort((arg0, arg1) -> comparator.compare(arg0.itemstack, arg1.itemstack));
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

	private List<IGuiOnTextChange>	listeners	= new ArrayList<>();
	private String					searchtext	= null;

	@Override
	public String getText(GuiElementTextField caller) {
		return searchtext;
	}

	@Override
	public void onTextChange(GuiElementTextField caller, String newtext) {
		searchtext = newtext;
		if (!cached_tabitem.isEmpty() && cached_tabitem.getItem() instanceof IItemPageCollection) {
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
