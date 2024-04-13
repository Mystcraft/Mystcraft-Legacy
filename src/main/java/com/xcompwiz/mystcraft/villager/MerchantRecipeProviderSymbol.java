package com.xcompwiz.mystcraft.villager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;

import com.xcompwiz.mystcraft.api.symbol.IAgeSymbol;
import com.xcompwiz.mystcraft.page.Page;
import com.xcompwiz.mystcraft.symbol.SymbolManager;

public class MerchantRecipeProviderSymbol implements IMerchantRecipeProvider {

	private IAgeSymbol	symbol;

	public MerchantRecipeProviderSymbol(IAgeSymbol symbol) {
		this.symbol = symbol;
	}

	@Override
	public List<MerchantRecipe> createNewMerchantRecipes(EntityVillager villager, Random random) {
		List<MerchantRecipe> merchantrecipes = new ArrayList<MerchantRecipe>();
		MerchantRecipe merchantrecipe = null;
		List<ItemStack> buyitems = SymbolManager.getSymbolTradeItems(symbol.identifier());
		if (buyitems.size() > 0 && buyitems.get(0) != null) {
			if (buyitems.size() > 1 && buyitems.get(1) != null) {
				merchantrecipe = new MerchantRecipeMyst(buyitems.get(0), buyitems.get(1), Page.createSymbolPage(symbol.identifier()));
			} else {
				merchantrecipe = new MerchantRecipeMyst(buyitems.get(0), Page.createSymbolPage(symbol.identifier()));
			}
		}
		if (merchantrecipe != null) {
			merchantrecipe.func_82783_a(-6);
			merchantrecipes.add(merchantrecipe);
		}
		return merchantrecipes;
	}

}
