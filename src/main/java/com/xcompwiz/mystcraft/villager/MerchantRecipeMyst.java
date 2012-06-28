package com.xcompwiz.mystcraft.villager;

import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;

public class MerchantRecipeMyst extends MerchantRecipe {

	public MerchantRecipeMyst(ItemStack buyItem, ItemStack sellItem) {
		super(buyItem, sellItem);
	}

	public MerchantRecipeMyst(ItemStack buyItem1, ItemStack buyItem2, ItemStack sellItem) {
		super(buyItem1, buyItem2, sellItem);
	}

	public MerchantRecipeMyst(MerchantRecipe recipe) {
		super(recipe.getItemToBuy(), recipe.getSecondItemToBuy(), recipe.getItemToSell());
		int usesDelta = recipe.writeToTags().getInteger("maxUses") - 7;
		func_82783_a(usesDelta);
	}

	@Override
	public boolean hasSameIDsAs(MerchantRecipe other) {
		return true;
	}

	/**
	 * checks first and second ItemToBuy ID's and count. Calls hasSameIDs
	 */
	@Override
	public boolean hasSameItemsAs(MerchantRecipe other) {
		return this.hasSameIDsAs(other);
	}
}
