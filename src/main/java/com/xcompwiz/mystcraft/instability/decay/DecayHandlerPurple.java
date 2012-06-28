package com.xcompwiz.mystcraft.instability.decay;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

import com.xcompwiz.mystcraft.block.BlockDecay;

public class DecayHandlerPurple extends DecayHandlerSpreading {

	@Override
	public String getIdentifier() {
		return "purple";
	}

	@Override
	protected int getConversionDifficulty(World world, int i, int j, int k) {
		Block block = world.getBlock(i, j, k);
		if (block == Blocks.air) return 8;
		if (block == BlockDecay.instance) return 5;
		if (block.getMaterial().isLiquid()) return 3;
		float resist = block.getExplosionResistance(null, world, i, j, k, i, j, k);
		if (resist < 0.0F) {
			resist = 1000.0F;
		}
		if (resist > 1000.0F) {
			resist = 1000.0F;
		}
		float hardness = block.getBlockHardness(world, i, j, k);
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
	public float getExplosionResistance(Entity entity, World worldObj, int x, int y, int z, double explosionX, double explosionY, double explosionZ) {
		return 100.0F;
	}

	@Override
	public float getBlockHardness(World worldObj, int x, int y, int z) {
		return 50.0F;
	}
}
