package com.xcompwiz.mystcraft.villager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.xcompwiz.mystcraft.api.hook.LinkPropertyAPI;
import com.xcompwiz.mystcraft.api.impl.InternalAPI;
import com.xcompwiz.mystcraft.data.InkEffects;
import com.xcompwiz.mystcraft.page.Page;

import net.minecraft.entity.IMerchant;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;

import javax.annotation.Nonnull;

public class MerchantRecipeProviderLinkpanel implements IMerchantRecipeProvider {

	public MerchantRecipeProviderLinkpanel() {}

	@Nonnull
	@Override
	public List<MerchantRecipe> createNewMerchantRecipes(@Nonnull IMerchant villagerMerchant, @Nonnull Random random) {
		List<MerchantRecipe> merchantrecipes = new ArrayList<>();
		for (String effect : InkEffects.getProperties()) {
			if(effect.equals(LinkPropertyAPI.FLAG_RELATIVE)) continue;
			ItemStack page = Page.createLinkPage(effect);
			MerchantRecipe merchantrecipe = new MerchantRecipeMyst(getPropertyTrade(effect), page);
			merchantrecipe.increaseMaxTradeUses(-6);
			merchantrecipes.add(merchantrecipe);
		}

		ItemStack page = Page.createLinkPage();
		MerchantRecipe merchantrecipe = new MerchantRecipeMyst(new ItemStack(Items.EMERALD, 4), page);
		merchantrecipe.increaseMaxTradeUses(-6);
		merchantrecipes.add(merchantrecipe);

		return merchantrecipes;
	}

	public static ItemStack getPropertyTrade(String effect) {
		return new ItemStack(Items.EMERALD, 16); // TODO: (Trading) Linkpanel values
	}
}
