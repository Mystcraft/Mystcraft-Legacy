package com.xcompwiz.mystcraft.villager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import cpw.mods.fml.common.registry.VillagerRegistry.IVillageTradeHandler;

public class VillagerArchivist implements IVillageTradeHandler {

	private List<IMerchantRecipeProvider>	items	= new ArrayList<IMerchantRecipeProvider>();

	public VillagerArchivist() {
		items.add(new MerchantRecipeProviderLinkpanel());
	}

	@Override
	public void manipulateTradesForVillager(EntityVillager villager, MerchantRecipeList recipeList, Random random) {
		for (IMerchantRecipeProvider item : items) {
			recipeList.addAll(item.createNewMerchantRecipes(villager, random));
		}

		for (int i = 0; i < recipeList.size(); ++i) {
			MerchantRecipe recipe = (MerchantRecipe) recipeList.get(i);
			if (!(recipe instanceof MerchantRecipeMyst)) {
				recipeList.remove(i--);
				recipeList.add(new MerchantRecipeMyst(recipe));
			}
		}
	}

	public void registerRecipe(IMerchantRecipeProvider recipe) {
		items.add(recipe);
	}

}
