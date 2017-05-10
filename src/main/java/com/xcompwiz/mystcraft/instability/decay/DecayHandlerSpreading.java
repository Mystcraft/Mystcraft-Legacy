package com.xcompwiz.mystcraft.instability.decay;

import java.util.Random;

import com.xcompwiz.mystcraft.block.BlockDecay;
import com.xcompwiz.mystcraft.data.ModBlocks;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class DecayHandlerSpreading extends DecayHandler {

	@Override
	protected void pulse(World world, BlockPos pos, Random rand) {
		for (EnumFacing face : EnumFacing.VALUES) {
			spread(world, pos.offset(face), rand);
		}
	}

	protected void spread(World world, BlockPos pos, Random rand) {
		IBlockState state = world.getBlockState(pos);
		if (state.getBlock().equals(ModBlocks.decay) && ModBlocks.decay.getMetaFromState(state) == this.getMetadata()) return;
		if (rand.nextInt(getConversionDifficulty(world, pos)) == 0) {
			world.setBlockState(pos, ModBlocks.decay.getDefaultState().withProperty(BlockDecay.DECAY_META, getMetadata()));
			addInstability(world, 1);
		}
	}

	protected abstract int getConversionDifficulty(World world, BlockPos pos);
}
