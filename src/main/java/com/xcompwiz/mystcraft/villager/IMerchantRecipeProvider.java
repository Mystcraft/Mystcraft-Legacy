package com.xcompwiz.mystcraft.villager;

import java.util.List;
import java.util.Random;

import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.village.MerchantRecipe;

public interface IMerchantRecipeProvider {

	public List<MerchantRecipe> createNewMerchantRecipes(EntityVillager villager, Random random);

}
