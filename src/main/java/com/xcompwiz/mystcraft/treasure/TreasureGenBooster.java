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
	private int					verycommon;
	private int					common;
	private int					uncommon;
	private int					rare;

	public TreasureGenBooster(int verycommon, int common, int uncommon, int rare, int weight) {
		super(dummy, 0, 0, weight);
		this.verycommon = verycommon;
		this.common = common;
		this.uncommon = uncommon;
		this.rare = rare;
	}

	//XXX: Generalize to allow for alternate rank sets (any rank, >=2, etc) 
	public static ItemStack generateBooster(Random rand, int verycommon, int common, int uncommon, int rare) {
		ItemStack notebook = new ItemStack(ModItems.notebook, 1, 0);

		Collection<IAgeSymbol> symbols_vc = SymbolManager.getSymbolsByRank(0);
		Collection<IAgeSymbol> symbols_c = SymbolManager.getSymbolsByRank(1);
		Collection<IAgeSymbol> symbols_uc = SymbolManager.getSymbolsByRank(2);
		Collection<IAgeSymbol> symbols_r = SymbolManager.getSymbolsByRank(3, null);

		for (int i = 0; i < verycommon; ++i) {
			IAgeSymbol symbol = WeightedItemSelector.getRandomItem(rand, symbols_vc, WeightProviderSymbolItem.instance);
			InventoryNotebook.addItem(notebook, Page.createSymbolPage(symbol.identifier()));
		}
		for (int i = 0; i < common; ++i) {
			IAgeSymbol symbol = WeightedItemSelector.getRandomItem(rand, symbols_c, WeightProviderSymbolItem.instance);
			InventoryNotebook.addItem(notebook, Page.createSymbolPage(symbol.identifier()));
		}
		for (int i = 0; i < uncommon; ++i) {
			IAgeSymbol symbol = WeightedItemSelector.getRandomItem(rand, symbols_uc, WeightProviderSymbolItem.instance);
			InventoryNotebook.addItem(notebook, Page.createSymbolPage(symbol.identifier()));
		}
		for (int i = 0; i < rare; ++i) {
			IAgeSymbol symbol = WeightedItemSelector.getRandomItem(rand, symbols_r, WeightProviderSymbolItem.instance);
			InventoryNotebook.addItem(notebook, Page.createSymbolPage(symbol.identifier()));
		}
		return notebook;
	}

	@Override
	protected ItemStack[] generateChestContent(Random random, IInventory newInventory) {
		ItemStack[] stacks = new ItemStack[1];
		stacks[0] = generateBooster(random, verycommon, common, uncommon, rare);
		return stacks;
	}
}
