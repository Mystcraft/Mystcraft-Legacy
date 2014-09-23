package com.xcompwiz.mystcraft.data;

import java.util.ArrayList;
import java.util.Collections;

import net.minecraft.item.ItemStack;

import com.xcompwiz.mystcraft.core.InternalAPI;
import com.xcompwiz.mystcraft.inventory.CreativeTabMyst;
import com.xcompwiz.mystcraft.inventory.InventoryNotebook;
import com.xcompwiz.mystcraft.page.Page;
import com.xcompwiz.mystcraft.page.SortingUtils.ComparatorSymbolAlphabetical;
import com.xcompwiz.mystcraft.symbol.IAgeSymbol;
import com.xcompwiz.mystcraft.symbol.SymbolManager;

public class ModNotebooks {

	public static ItemStack createCreativeNotebook() {
		ItemStack notebook = new ItemStack(ModItems.notebook, 1, 0);
		InventoryNotebook.setName(notebook, "Creative Spawned (All Symbols)");
		ArrayList<IAgeSymbol> symbols = new ArrayList<IAgeSymbol>();
		symbols.addAll(SymbolManager.getAgeSymbols());
		Collections.sort(symbols, ComparatorSymbolAlphabetical.instance);
		for (IAgeSymbol symbol : symbols) {
			InventoryNotebook.addItem(notebook, Page.createSymbolPage(symbol.identifier()));
		}
		return notebook;
	}

	public static void addNotebooks(CreativeTabMyst mainTab) {
		//TODO: Use notebook builder system
	}

	public static ItemStack buildNotebook(String name, String... args) {
		return InternalAPI.itemFact.buildNotebook("Spawned (" + name + ")", args);
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
