package com.xcompwiz.mystcraft.villager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.xcompwiz.mystcraft.api.symbol.IAgeSymbol;
import com.xcompwiz.mystcraft.page.Page;
import com.xcompwiz.mystcraft.symbol.SymbolManager;

import net.minecraft.entity.IMerchant;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;

import javax.annotation.Nonnull;

public class MerchantRecipeProviderSymbol implements IMerchantRecipeProvider {

	private IAgeSymbol	symbol;

	public MerchantRecipeProviderSymbol(IAgeSymbol symbol) {
		this.symbol = symbol;
	}

	@Nonnull
	@Override
	public List<MerchantRecipe> createNewMerchantRecipes(@Nonnull IMerchant villagerMerchant, @Nonnull Random random) {
		List<MerchantRecipe> merchantrecipes = new ArrayList<>();
		MerchantRecipe merchantrecipe = null;
		List<ItemStack> buyitems = SymbolManager.getSymbolTradeItems(symbol.getRegistryName());
		if (buyitems.size() > 0 && buyitems.get(0) != null) {
			if (buyitems.size() > 1 && buyitems.get(1) != null) {
				merchantrecipe = new MerchantRecipeMyst(buyitems.get(0), buyitems.get(1), Page.createSymbolPage(symbol.getRegistryName()));
			} else {
				merchantrecipe = new MerchantRecipeMyst(buyitems.get(0), Page.createSymbolPage(symbol.getRegistryName()));
			}
		}
		if (merchantrecipe != null) {
			merchantrecipe.increaseMaxTradeUses(-6);
			merchantrecipes.add(merchantrecipe);
		}
		return merchantrecipes;
	}

}
