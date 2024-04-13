package com.xcompwiz.mystcraft.instability.decay;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import com.xcompwiz.mystcraft.data.ModBlocks;

public class DecayHandlerWhite extends DecayHandlerSpreading {

	@Override
	public String getIdentifier() {
		return "white";
	}

	@Override
	public void onEntityContact(World worldObj, int x, int y, int z, Entity entity) {
		entity.attackEntityFrom(DamageSource.magic, 1);
	}

	@Override
	protected int getConversionDifficulty(World world, int i, int j, int k) {
		Block block = world.getBlock(i, j, k);
		if (block == Blocks.air) return 50;
		if (block == ModBlocks.decay) return 1;
		// if (block.blockMaterial.isLiquid()) return 3;
		return 1;
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
