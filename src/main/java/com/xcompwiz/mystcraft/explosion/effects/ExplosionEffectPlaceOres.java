package com.xcompwiz.mystcraft.explosion.effects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import com.xcompwiz.mystcraft.explosion.ExplosionAdvanced;
import com.xcompwiz.mystcraft.utility.WeightedItemSelector;
import com.xcompwiz.mystcraft.utility.WeightedItemSelector.IWeightedItem;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ExplosionEffectPlaceOres extends ExplosionEffect {

	public static class WeightedBlockSelection implements IWeightedItem {

		public final IBlockState blockstate;
		public final float weight;

		public WeightedBlockSelection(IBlockState blockstate, float weight) {
			this.blockstate = blockstate;
			this.weight = weight;
		}

		@Override
		public float getWeight() {
			return this.weight;
		}

	}

	public static ExplosionEffect instance = new ExplosionEffectPlaceOres();

	private static Collection<WeightedBlockSelection> blocks = new ArrayList<WeightedBlockSelection>();

	static {
		registerMeteorPlaceableBlock(Blocks.COAL_ORE.getDefaultState(), 0.50F);
		registerMeteorPlaceableBlock(Blocks.IRON_ORE.getDefaultState(), 0.30F);
		registerMeteorPlaceableBlock(Blocks.GOLD_ORE.getDefaultState(), 0.20F);
	}

	private ExplosionEffectPlaceOres() {}

	public static void registerMeteorPlaceableBlock(IBlockState blockstate, float weight) {
		blocks.add(new WeightedBlockSelection(blockstate, weight));
	}

	@Override
	public void apply(World worldObj, ExplosionAdvanced explosion, BlockPos pos, Random rand, boolean isClient) {
		if (isClient) return;
		IBlockState supporting = worldObj.getBlockState(pos.down());

		if (rand.nextInt(20) == 0 && worldObj.isAirBlock(pos) && supporting.isOpaqueCube()) {
			WeightedBlockSelection desc = getRandomBlock(rand);
			if (desc != null) {
				worldObj.setBlockState(pos, desc.blockstate, 3);
			}
		}
	}

	private WeightedBlockSelection getRandomBlock(Random rand) {
		return WeightedItemSelector.getRandomItem(rand, blocks);
	}

}
