package com.xcompwiz.mystcraft.data;

import java.util.ArrayList;
import java.util.Collections;

import net.minecraft.item.ItemStack;

import com.xcompwiz.mystcraft.api.internal.IGrammarAPI;
import com.xcompwiz.mystcraft.core.InternalAPI;
import com.xcompwiz.mystcraft.inventory.CreativeTabMyst;
import com.xcompwiz.mystcraft.inventory.InventoryNotebook;
import com.xcompwiz.mystcraft.item.ItemNotebook;
import com.xcompwiz.mystcraft.page.Page;
import com.xcompwiz.mystcraft.page.SortingUtils.ComparatorSymbolAlphabetical;
import com.xcompwiz.mystcraft.symbol.IAgeSymbol;
import com.xcompwiz.mystcraft.symbol.SymbolManager;

public class LoaderNotebook {

	private static ItemStack createCreativeNotebook() {
		ItemStack notebook = new ItemStack(ItemNotebook.instance, 1, 0);
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
		ArrayList<ItemStack> creative_notebooks = new ArrayList<ItemStack>();
		creative_notebooks.add(createCreativeNotebook());

		creative_notebooks.add(buildNotebook("Biome Controllers", IGrammarAPI.BIOMECONTROLLER));
		creative_notebooks.add(buildNotebook("Celestials", IGrammarAPI.SUN, IGrammarAPI.MOON, IGrammarAPI.STARFIELD, IGrammarAPI.DOODAD));
		creative_notebooks.add(buildNotebook("Effects", IGrammarAPI.EFFECT));
		creative_notebooks.add(buildNotebook("Lighting", IGrammarAPI.LIGHTING));
		creative_notebooks.add(buildNotebook("Modifiers, Basic", IGrammarAPI.ANGLE_BASIC, IGrammarAPI.PERIOD_BASIC, IGrammarAPI.PHASE_BASIC));
		creative_notebooks.add(buildNotebook("Modifiers, Biomes", IGrammarAPI.BIOME));
		creative_notebooks.add(buildNotebook("Modifiers, Block", IGrammarAPI.BLOCK_ANY, IGrammarAPI.BLOCK_CRYSTAL, IGrammarAPI.BLOCK_FLUID, IGrammarAPI.BLOCK_GAS, IGrammarAPI.BLOCK_SEA, IGrammarAPI.BLOCK_SOLID, IGrammarAPI.BLOCK_STRUCTURE, IGrammarAPI.BLOCK_TERRAIN));
		creative_notebooks.add(buildNotebook("Modifiers, Colors", IGrammarAPI.COLOR_BASIC, IGrammarAPI.COLOR_SEQ, IGrammarAPI.GRADIENT_BASIC, IGrammarAPI.GRADIENT_SEQ, IGrammarAPI.SUNSET));
		creative_notebooks.add(buildNotebook("Populators", IGrammarAPI.POPULATOR));
		creative_notebooks.add(buildNotebook("Terrain Alterations", IGrammarAPI.TERRAINALT));
		creative_notebooks.add(buildNotebook("Terrains", IGrammarAPI.TERRAIN));
		creative_notebooks.add(buildNotebook("Visuals", IGrammarAPI.VISUAL_EFFECT));
		creative_notebooks.add(buildNotebook("Weather", IGrammarAPI.WEATHER));

		for (ItemStack notebook : creative_notebooks) {
			mainTab.registerItemStack(notebook);
		}
	}

	private static ItemStack buildNotebook(String name, String... args) {
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
