package com.xcompwiz.mystcraft.instability.decay;

import com.xcompwiz.mystcraft.data.ModBlocks;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class DecayHandlerPurple extends DecayHandlerSpreading {

	@Override
	public String getIdentifier() {
		return "purple";
	}

	@Override
	protected int getConversionDifficulty(World world, BlockPos pos) {
		IBlockState blockstate = world.getBlockState(pos);
		if (blockstate == Blocks.AIR) return 8;
		if (blockstate == ModBlocks.decay) return 5;
		if (blockstate.getMaterial().isLiquid()) return 3;
		float resist = blockstate.getBlock().getExplosionResistance(world, pos, null, null);
		if (resist < 0.0F) {
			resist = 1000.0F;
		}
		if (resist > 1000.0F) {
			resist = 1000.0F;
		}
		float hardness = blockstate.getBlockHardness(world, pos);
		if (hardness < 0.0F) {
			hardness = 1000.0F;
		}
		if (hardness > 1000.0F) {
			hardness = 1000.0F;
		}
		hardness *= 2;
		return Math.max(1, (int) (hardness + resist)) * 10;
	}

	@Override
	public float getExplosionResistance(Entity entity, World worldObj, BlockPos pos, Explosion explosion) {
		return 100.0F;
	}

	@Override
	public float getBlockHardness(World worldObj, BlockPos pos) {
		return 50.0F;
	}
}
