package com.xcompwiz.mystcraft.villager;

import java.util.List;
import java.util.Random;

import com.xcompwiz.util.CollectionUtils;

import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;

public class MerchantRecipeProviderItem implements IMerchantRecipeProvider {

	private ItemStack	buy1;
	private ItemStack	buy2;
	private ItemStack	sell;

	public MerchantRecipeProviderItem(ItemStack buy1, ItemStack buy2, ItemStack sell) {
		this.buy1 = buy1;
		this.buy2 = buy2;
		this.sell = sell;
	}

	@Override
	public List<MerchantRecipe> createNewMerchantRecipes(EntityVillager villager, Random random) {
		return CollectionUtils.buildList((MerchantRecipe) new MerchantRecipeMyst(buy1, buy2, sell));
	}

}
