package com.xcompwiz.mystcraft.explosion.effects;

import java.util.Random;

import com.xcompwiz.mystcraft.explosion.ExplosionAdvanced;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;

public class ExplosionEffectBreakBlocks extends ExplosionEffect {
	public static ExplosionEffect	noDrop		= new ExplosionEffectBreakBlocks(false);
	public static ExplosionEffect	dropItems	= new ExplosionEffectBreakBlocks(true);

	private boolean					dropitems;

	private ExplosionEffectBreakBlocks(boolean b) {
		dropitems = b;
	}

	@Override
	public void apply(World worldObj, ExplosionAdvanced explosion, ChunkPosition pos, Random rand, boolean isClient) {
		if (worldObj.isRemote) return;
		int x = pos.chunkPosX;
		int y = pos.chunkPosY;
		int z = pos.chunkPosZ;
		Block block = worldObj.getBlock(x, y, z);

		if (dropitems) {
			block.dropBlockAsItemWithChance(worldObj, x, y, z, worldObj.getBlockMetadata(x, y, z), 0.3F, 0);
		}

		if (worldObj.setBlock(x, y, z, Blocks.AIR, 0, 3)) {
			worldObj.notifyBlocksOfNeighborChange(x, y, z, Blocks.AIR);
		}

		block.onBlockDestroyedByExplosion(worldObj, x, y, z, explosion.toExplosion());
	}

}
