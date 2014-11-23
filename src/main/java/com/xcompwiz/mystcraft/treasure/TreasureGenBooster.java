package com.xcompwiz.mystcraft.treasure;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandomChestContent;

import com.xcompwiz.mystcraft.item.ItemNotebook;

public class TreasureGenBooster extends WeightedRandomChestContent {

	private static ItemStack	dummy	= new ItemStack(Blocks.stone);
	private int					verycommon;
	private int					common;
	private int					uncommon;
	private int					rare;

	public TreasureGenBooster(int verycommon, int common, int uncommon, int rare, int weight) {
		super(dummy, 0, 0, weight);
		this.verycommon = verycommon;
		this.common = common;
		this.uncommon = uncommon;
		this.rare = rare;
	}

	@Override
	protected ItemStack[] generateChestContent(Random random, IInventory newInventory) {
		ItemStack[] stacks = new ItemStack[1];
		stacks[0] = ItemNotebook.generateBooster(random, verycommon, common, uncommon, rare);
		return stacks;
	}
}
