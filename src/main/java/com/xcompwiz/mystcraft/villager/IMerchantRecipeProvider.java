package com.xcompwiz.mystcraft.villager;

import java.util.List;
import java.util.Random;

import javax.annotation.Nonnull;

import net.minecraft.entity.IMerchant;
import net.minecraft.village.MerchantRecipe;

public interface IMerchantRecipeProvider {

	@Nonnull
	public List<MerchantRecipe> createNewMerchantRecipes(@Nonnull IMerchant villagerMerchant, @Nonnull Random random);

}
