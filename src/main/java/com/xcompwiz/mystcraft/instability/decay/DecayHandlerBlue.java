package com.xcompwiz.mystcraft.instability.decay;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class DecayHandlerBlue extends DecayHandlerSpreading {

	@Override
	public String getIdentifier() {
		return "blue";
	}

	@Override
	protected int getConversionDifficulty(World world, int i, int j, int k) {
		Block block = world.getBlock(i, j, k);
		if (block == Blocks.air) return 20;
		float f = block.getBlockHardness(world, i, j, k);
		if (f < 0.0F) {
			f = 1000.0F;
		}
		if (f > 1000.0F) {
			f = 1000.0F;
		}
		return Math.max(1, (int) f * 2);
	}

	@Override
	public float getExplosionResistance(Entity entity, World worldObj, int x, int y, int z, double explosionX, double explosionY, double explosionZ) {
		return 2.0F;
	}

	@Override
	public float getBlockHardness(World worldObj, int x, int y, int z) {
		return 5.0F;
	}
}
