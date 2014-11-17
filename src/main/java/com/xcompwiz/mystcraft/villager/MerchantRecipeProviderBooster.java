package com.xcompwiz.mystcraft.villager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;

import com.xcompwiz.mystcraft.treasure.TreasureGenBooster;

public class MerchantRecipeProviderBooster implements IMerchantRecipeProvider {
	private int	verycommon;
	private int	common;
	private int	uncommon;
	private int	rare;

	public MerchantRecipeProviderBooster(int verycommon, int common, int uncommon, int rare) {
		this.verycommon = verycommon;
		this.common = common;
		this.uncommon = uncommon;
		this.rare = rare;
	}

	@Override
	public List<MerchantRecipe> createNewMerchantRecipes(EntityVillager villager, Random random) {
		List<MerchantRecipe> merchantrecipes = new ArrayList<MerchantRecipe>();
		for (int i = 0; i < 100; ++i) {
			ItemStack notebook = TreasureGenBooster.generateBooster(random, verycommon, common, uncommon, rare);
			MerchantRecipe merchantrecipe = new MerchantRecipeMyst(new ItemStack(Items.emerald, 20), notebook);
			merchantrecipe.func_82783_a(-6);
			merchantrecipes.add(merchantrecipe);
		}
		return merchantrecipes;
	}

}
