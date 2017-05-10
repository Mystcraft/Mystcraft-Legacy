package com.xcompwiz.mystcraft.instability.decay;

import java.util.Random;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class DecayHandlerSpreading extends DecayHandler {

	@Override
	protected void pulse(World world, BlockPos pos, Random rand) {
		spread(world, pos.down(), rand);
		spread(world, pos.up(), rand);
		spread(world, pos.north(), rand);
		spread(world, pos.south(), rand);
		spread(world, pos.west(), rand);
		spread(world, pos.east(), rand);
	}

	protected void spread(World world, BlockPos pos, Random rand) {
		if (world.getBlockState(pos) == this.getBlockState()) return;
		if (rand.nextInt(getConversionDifficulty(world, pos)) == 0) {
			world.setBlockState(pos, this.getBlockState(), 3);
			addInstability(world, 1);
		}
	}

	protected abstract int getConversionDifficulty(World world, BlockPos pos);
}
