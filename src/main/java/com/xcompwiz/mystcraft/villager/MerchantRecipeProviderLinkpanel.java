package com.xcompwiz.mystcraft.villager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.xcompwiz.mystcraft.data.InkEffects;
import com.xcompwiz.mystcraft.page.Page;

import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;

public class MerchantRecipeProviderLinkpanel implements IMerchantRecipeProvider {

	public MerchantRecipeProviderLinkpanel() {}

	@Override
	public List<MerchantRecipe> createNewMerchantRecipes(EntityVillager villager, Random random) {
		List<MerchantRecipe> merchantrecipes = new ArrayList<MerchantRecipe>();
		for (String effect : InkEffects.getProperties()) {
			ItemStack page = Page.createLinkPage(effect);
			MerchantRecipe merchantrecipe = new MerchantRecipeMyst(getPropertyTrade(effect), page);
			merchantrecipe.func_82783_a(-6);
			merchantrecipes.add(merchantrecipe);
		}

		ItemStack page = Page.createLinkPage();
		MerchantRecipe merchantrecipe = new MerchantRecipeMyst(new ItemStack(Items.emerald, 4), page);
		merchantrecipe.func_82783_a(-6);
		merchantrecipes.add(merchantrecipe);

		return merchantrecipes;
	}

	public static ItemStack getPropertyTrade(String effect) {
		return new ItemStack(Items.emerald, 16); // TODO: (Trading) Linkpanel values
	}
}
