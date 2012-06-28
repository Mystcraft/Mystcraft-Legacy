package com.xcompwiz.mystcraft.villager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;

import com.xcompwiz.mystcraft.page.Page;

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

		recipeList.add(new MerchantRecipeMyst(new ItemStack(Items.emerald, 1), Page.createPage()));
		EntityVillager.func_146089_b(recipeList, Items.paper, random, 0.8F);
		EntityVillager.func_146089_b(recipeList, Items.book, random, 0.8F);
		EntityVillager.func_146091_a(recipeList, Items.paper, random, 0.8F);
		EntityVillager.func_146091_a(recipeList, Items.book, random, 0.8F);

		for (int i = 0; i < 4; ++i) {
			Enchantment enchant = Enchantment.enchantmentsBookList[random.nextInt(Enchantment.enchantmentsBookList.length)];
			int level = MathHelper.getRandomIntegerInRange(random, enchant.getMinLevel(), enchant.getMaxLevel());
			ItemStack bookitem = Items.enchanted_book.getEnchantedItemStack(new EnchantmentData(enchant, level));
			int emeralds = Math.min(64, 2 + random.nextInt(5 + level * 10) + 3 * level);
			recipeList.add(new MerchantRecipeMyst(new ItemStack(Items.book), new ItemStack(Items.emerald, emeralds), bookitem));
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
