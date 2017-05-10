package com.xcompwiz.mystcraft.villager;

import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;

import javax.annotation.Nonnull;

public class MerchantRecipeMyst extends MerchantRecipe {

	public MerchantRecipeMyst(@Nonnull ItemStack buyItem, @Nonnull ItemStack sellItem) {
		super(buyItem, sellItem);
	}

	public MerchantRecipeMyst(@Nonnull ItemStack buyItem1, @Nonnull ItemStack buyItem2, @Nonnull ItemStack sellItem) {
		super(buyItem1, buyItem2, sellItem);
	}

	public MerchantRecipeMyst(MerchantRecipe recipe) {
		super(recipe.getItemToBuy(), recipe.getSecondItemToBuy(), recipe.getItemToSell());
		int usesDelta = recipe.writeToTags().getInteger("maxUses") - 7;
		increaseMaxTradeUses(usesDelta);
	}

}
