package com.xcompwiz.mystcraft.instability.decay;

import java.util.Random;

import net.minecraft.world.World;

import com.xcompwiz.mystcraft.data.ModBlocks;

public abstract class DecayHandlerSpreading extends DecayHandler {

	@Override
	protected void pulse(World world, int i, int j, int k, Random rand) {
		spread(world, i, j - 1, k, rand);
		spread(world, i, j + 1, k, rand);
		spread(world, i - 1, j, k, rand);
		spread(world, i + 1, j, k, rand);
		spread(world, i, j, k - 1, rand);
		spread(world, i, j, k + 1, rand);
	}

	protected void spread(World world, int i, int j, int k, Random rand) {
		if (world.getBlock(i, j, k) == ModBlocks.decay && world.getBlockMetadata(i, j, k) == this.getMetadata()) return;
		if (rand.nextInt(getConversionDifficulty(world, i, j, k)) == 0) {
			world.setBlock(i, j, k, ModBlocks.decay, this.getMetadata(), 3);
			addInstability(world, 1);
		}
	}

	protected abstract int getConversionDifficulty(World world, int i, int j, int k);
}
