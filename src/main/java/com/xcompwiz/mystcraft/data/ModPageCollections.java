package com.xcompwiz.mystcraft.data;

import java.util.ArrayList;
import java.util.Collections;

import net.minecraft.item.ItemStack;

import com.xcompwiz.mystcraft.api.item.IItemPageCollection;
import com.xcompwiz.mystcraft.api.symbol.IAgeSymbol;
import com.xcompwiz.mystcraft.core.InternalAPI;
import com.xcompwiz.mystcraft.inventory.CreativeTabMyst;
import com.xcompwiz.mystcraft.page.Page;
import com.xcompwiz.mystcraft.page.SortingUtils.ComparatorSymbolAlphabetical;
import com.xcompwiz.mystcraft.symbol.SymbolManager;

public class ModPageCollections {

	public static ItemStack createCreativeCollection(String name) {
		ItemStack collection = new ItemStack(ModItems.portfolio, 1, 0);
		IItemPageCollection item = (IItemPageCollection) collection.getItem();
		item.setDisplayName(null, collection, name);
		ArrayList<IAgeSymbol> symbols = new ArrayList<IAgeSymbol>();
		symbols.addAll(SymbolManager.getAgeSymbols());
		Collections.sort(symbols, ComparatorSymbolAlphabetical.instance);
		for (IAgeSymbol symbol : symbols) {
			item.addPage(null, collection, Page.createSymbolPage(symbol.identifier()));
		}
		return collection;
	}

	public static void addPageCollections(CreativeTabMyst mainTab) {
		//TODO: Use collection builder system
	}

	public static ItemStack buildPageCollection(String name, String... args) {
		return InternalAPI.itemFact.buildCollectionItem(name, args);
	}

	public static void addSymbolPages(CreativeTabMyst pageTab) {
		ArrayList<IAgeSymbol> symbols = SymbolManager.getAgeSymbols();
		Collections.sort(symbols, ComparatorSymbolAlphabetical.instance);
		for (IAgeSymbol symbol : symbols) {
			// Creative tab item
			pageTab.registerItemStack(Page.createSymbolPage(symbol.identifier()));
		}
	}
}
