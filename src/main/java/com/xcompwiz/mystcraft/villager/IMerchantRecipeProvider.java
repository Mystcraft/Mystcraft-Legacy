package com.xcompwiz.mystcraft.villager;

import java.util.List;
import java.util.Random;

import net.minecraft.entity.IMerchant;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.village.MerchantRecipe;

import javax.annotation.Nonnull;

public interface IMerchantRecipeProvider {

	@Nonnull
	public List<MerchantRecipe> createNewMerchantRecipes(@Nonnull IMerchant villagerMerchant, @Nonnull Random random);

}
