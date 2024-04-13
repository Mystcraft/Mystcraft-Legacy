package com.xcompwiz.mystcraft.instability.decay;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class DecayHandlerRed extends DecayHandlerSpreading {

	@Override
	public String getIdentifier() {
		return "red";
	}

	@Override
	protected int getConversionDifficulty(World world, int i, int j, int k) {
		Block block = world.getBlock(i, j, k);
		if (block == Blocks.air) return 20;
		float f = block.getExplosionResistance(null, world, i, j, k, i, j, k);
		if (f < 0.0F) {
			f = 1000.0F;
		}
		if (f > 1000.0F) {
			f = 1000.0F;
		}
		return Math.max(1, (int) f);
	}

	@Override
	public float getExplosionResistance(Entity entity, World worldObj, int x, int y, int z, double explosionX, double explosionY, double explosionZ) {
		return 10.0F;
	}

	@Override
	public float getBlockHardness(World worldObj, int x, int y, int z) {
		return 1.0F;
	}
}
