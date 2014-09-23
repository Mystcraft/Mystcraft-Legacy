package com.xcompwiz.mystcraft.treasure;

import java.util.Collection;
import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandomChestContent;

import com.xcompwiz.mystcraft.data.ModItems;
import com.xcompwiz.mystcraft.inventory.InventoryNotebook;
import com.xcompwiz.mystcraft.page.Page;
import com.xcompwiz.mystcraft.symbol.IAgeSymbol;
import com.xcompwiz.mystcraft.symbol.SymbolManager;
import com.xcompwiz.mystcraft.utility.WeightedItemSelector;

public class TreasureGenBooster extends WeightedRandomChestContent {

	private static ItemStack	dummy	= new ItemStack(Blocks.stone);
	private int					common;
	private int					uncommon;
	private int					rare;

	public TreasureGenBooster(int common, int uncommon, int rare, int weight) {
		super(dummy, 0, 0, weight);
		this.common = common;
		this.uncommon = uncommon;
		this.rare = rare;
	}

	public static ItemStack generateBooster(Random rand, int common, int uncommon, int rare) {
		ItemStack notebook = new ItemStack(ModItems.notebook, 1, 0);

		Collection<IAgeSymbol> symbols_common = SymbolManager.getSymbolByRarity(0.6F, null);
		Collection<IAgeSymbol> symbols_uncommon = SymbolManager.getSymbolByRarity(0.2F, 0.6F);
		Collection<IAgeSymbol> symbols_rare = SymbolManager.getSymbolByRarity(0F, 0.2F);

		for (int i = 0; i < common; ++i) {
			IAgeSymbol symbol = WeightedItemSelector.getRandomItem(rand, symbols_common, WeightProviderSymbolItem.instance);
			InventoryNotebook.addItem(notebook, Page.createSymbolPage(symbol.identifier()));
		}
		for (int i = 0; i < uncommon; ++i) {
			IAgeSymbol symbol = WeightedItemSelector.getRandomItem(rand, symbols_uncommon, WeightProviderSymbolItem.instance);
			InventoryNotebook.addItem(notebook, Page.createSymbolPage(symbol.identifier()));
		}
		for (int i = 0; i < rare; ++i) {
			IAgeSymbol symbol = WeightedItemSelector.getRandomItem(rand, symbols_rare, WeightProviderSymbolItem.instance);
			InventoryNotebook.addItem(notebook, Page.createSymbolPage(symbol.identifier()));
		}
		return notebook;
	}

	@Override
	protected ItemStack[] generateChestContent(Random random, IInventory newInventory) {
		ItemStack[] stacks = new ItemStack[1];
		stacks[0] = generateBooster(random, common, uncommon, rare);
		return stacks;
	}
}
