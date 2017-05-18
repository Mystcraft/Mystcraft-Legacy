package com.xcompwiz.mystcraft.instability.decay;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class DecayHandlerRed extends DecayHandlerSpreading {

	@Override
	public String getIdentifier() {
		return "red";
	}

	@Override
	protected int getConversionDifficulty(World world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		if (state.getBlock().isAir(state, world, pos)) return 20;
		float f = state.getBlock().getExplosionResistance(world, pos, null, null);
		if (f < 0.0F) {
			f = 1000.0F;
		}
		if (f > 1000.0F) {
			f = 1000.0F;
		}
		return Math.max(1, (int) f);
	}

	@Override
	public float getExplosionResistance(World worldObj, BlockPos pos, Entity exploder, Explosion explosion) {
		return 10.0F;
	}

	@Override
	public float getBlockHardness(World worldObj, BlockPos pos) {
		return 1.0F;
	}
}
