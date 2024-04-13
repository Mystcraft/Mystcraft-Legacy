package com.xcompwiz.mystcraft.explosion.effects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;

import com.xcompwiz.mystcraft.explosion.ExplosionAdvanced;
import com.xcompwiz.mystcraft.utility.WeightedItemSelector;
import com.xcompwiz.mystcraft.utility.WeightedItemSelector.IWeightedItem;

public class ExplosionEffectPlaceOres extends ExplosionEffect {

	public static class WeightedBlockSelection implements IWeightedItem {

		public final Block	block;
		public final int	metadata;
		public final float	weight;

		public WeightedBlockSelection(Block block, int metadata, float weight) {
			this.block = block;
			this.metadata = metadata;
			this.weight = weight;
		}

		@Override
		public float getWeight() {
			return this.weight;
		}

	}

	public static ExplosionEffect						instance	= new ExplosionEffectPlaceOres();

	private static Collection<WeightedBlockSelection>	blocks		= new ArrayList<WeightedBlockSelection>();

	static {
		registerMeteorPlaceableBlock(Blocks.coal_ore, 0, 0.50F);
		registerMeteorPlaceableBlock(Blocks.iron_ore, 0, 0.30F);
		registerMeteorPlaceableBlock(Blocks.gold_ore, 0, 0.20F);
	}

	private ExplosionEffectPlaceOres() {}

	public static void registerMeteorPlaceableBlock(Block block, int meta, float weight) {
		blocks.add(new WeightedBlockSelection(block, meta, weight));
	}

	@Override
	public void apply(World worldObj, ExplosionAdvanced explosion, ChunkPosition pos, Random rand, boolean isClient) {
		if (isClient) return;
		int x, y, z;
		x = pos.chunkPosX;
		y = pos.chunkPosY;
		z = pos.chunkPosZ;
		Block supporting = worldObj.getBlock(x, y - 1, z);

		if (rand.nextInt(20) == 0 && worldObj.isAirBlock(x, y, z) && supporting.isOpaqueCube()) {
			WeightedBlockSelection desc = getRandomBlock(rand);
			if (desc != null) {
				worldObj.setBlock(x, y, z, desc.block, desc.metadata, 3);
			}
		}
	}

	private WeightedBlockSelection getRandomBlock(Random rand) {
		return WeightedItemSelector.getRandomItem(rand, blocks);
	}

}
