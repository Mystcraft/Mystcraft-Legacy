package com.xcompwiz.mystcraft.villager;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.annotation.Nonnull;

import net.minecraft.entity.IMerchant;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;

public class MerchantRecipeProviderItem implements IMerchantRecipeProvider {

	@Nonnull
	private ItemStack buy1;
	@Nonnull
	private ItemStack buy2;
	@Nonnull
	private ItemStack sell;

	public MerchantRecipeProviderItem(@Nonnull ItemStack buy1, @Nonnull ItemStack buy2, @Nonnull ItemStack sell) {
		this.buy1 = buy1;
		this.buy2 = buy2;
		this.sell = sell;
	}

	@Nonnull
	@Override
	public List<MerchantRecipe> createNewMerchantRecipes(@Nonnull IMerchant villagerMerchant, @Nonnull Random random) {
		return Collections.singletonList(new MerchantRecipeMyst(buy1, buy2, sell));
	}

}
