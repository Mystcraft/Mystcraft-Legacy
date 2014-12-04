package com.xcompwiz.mystcraft.treasure;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;

public class TreasureGenWrapper extends WeightedRandomChestContent {

	private static ItemStack	dummy	= new ItemStack(Blocks.stone);
	private String				gen_info;

	public TreasureGenWrapper(String gen_info, int weight) {
		super(dummy, 0, 0, weight);
		this.gen_info = gen_info;
	}

	/**
	 * Allows a mod to submit a custom implementation that can delegate item stack generation beyond simple stack lookup
	 * @param random The current random for generation
	 * @param newInventory The inventory being generated (do not populate it, but you can refer to it)
	 * @return An array of {@link ItemStack} to put into the chest
	 */
	@Override
	protected ItemStack[] generateChestContent(Random random, IInventory newInventory) {
		ChestGenHooks info = ChestGenHooks.getInfo(gen_info);
		return generateChestContents(random, info.getItems(random));
	}

	public static ItemStack[] generateChestContents(Random rand, WeightedRandomChestContent[] contentlist) {
		WeightedRandomChestContent content = (WeightedRandomChestContent) WeightedRandom.getRandomItem(rand, contentlist);

		return ChestGenHooks.generateStacks(rand, content.theItemId, content.theMinimumChanceToGenerateItem, content.theMaximumChanceToGenerateItem);
	}

}
