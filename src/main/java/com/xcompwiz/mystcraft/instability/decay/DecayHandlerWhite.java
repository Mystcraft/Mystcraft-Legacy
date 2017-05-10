package com.xcompwiz.mystcraft.instability.decay;

import com.xcompwiz.mystcraft.data.ModBlocks;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class DecayHandlerWhite extends DecayHandlerSpreading {

	@Override
	public String getIdentifier() {
		return "white";
	}

	@Override
	public void onEntityContact(World worldObj, BlockPos pos, Entity entity) {
		entity.attackEntityFrom(DamageSource.MAGIC, 1);
	}

	@Override
	protected int getConversionDifficulty(World world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		if (state.getBlock().isAir(state, world, pos)) return 50;
		if (state.getBlock().equals(ModBlocks.decay)) return 1;
		// if (block.blockMaterial.isLiquid()) return 3;
		return 1;
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
