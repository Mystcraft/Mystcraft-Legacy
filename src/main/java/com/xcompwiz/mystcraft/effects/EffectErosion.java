package com.xcompwiz.mystcraft.effects;

import java.util.Random;

import com.xcompwiz.mystcraft.api.world.logic.IEnvironmentalEffect;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockStaticLiquid;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.feature.WorldGenLakes;

public class EffectErosion implements IEnvironmentalEffect {

	private int	updateLCG	= (new Random()).nextInt();

	public void onChunkPopulate(World worldObj, Random rand, int i, int j) {
		if (rand.nextInt(4) == 0) {
			int i1 = i + rand.nextInt(16) + 8;
			int j2 = rand.nextInt(256);
			int k3 = j + rand.nextInt(16) + 8;
			(new WorldGenLakes(Blocks.WATER)).generate(worldObj, rand, i1, j2, k3);
		}
		if (rand.nextInt(8) == 0) {
			int j1 = i + rand.nextInt(16) + 8;
			int k2 = rand.nextInt(rand.nextInt(248) + 8);
			int l3 = j + rand.nextInt(16) + 8;
			(new WorldGenLakes(Blocks.LAVA)).generate(worldObj, rand, j1, k2, l3);
		}
	}

	@Override
	public void tick(World worldObj, Chunk chunk) {
		int var5 = chunk.xPosition * 16;
		int var6 = chunk.zPosition * 16;
		int var8;
		int var9;
		int var10;
		int var11;

		if (worldObj.rand.nextInt(100) == 0) {
			updateLCG = updateLCG * 3 + 1013904223;
			var8 = updateLCG >> 2;
			var9 = var5 + (var8 & 15);
			var10 = var6 + (var8 >> 8 & 15);
			var11 = (var8 >> 16 & 127);
			Block block = worldObj.getBlock(var9, var11, var10);
			if (block == null) return;
			if (block instanceof BlockContainer) return;
			if (block.getMaterial().isLiquid()) return;
			Block ajacent = adjacentFluid(worldObj, var9, var11, var10);
			if (ajacent != null) {
				worldObj.setBlock(var9, var11, var10, ajacent, 0, 3);
			}
		}
	}

	private Block adjacentFluid(World par1World, int par2, int par3, int par4) {
		for (int var5 = par2 - 1; var5 <= par2 + 1; ++var5) {
			for (int var6 = par3; var6 <= par3 + 1; ++var6) {
				for (int var7 = par4 - 1; var7 <= par4 + 1; ++var7) {
					Block block = par1World.getBlock(var5, var6, var7);
					if (block instanceof BlockStaticLiquid) { return block; }
				}
			}
		}

		return null;
	}
}
