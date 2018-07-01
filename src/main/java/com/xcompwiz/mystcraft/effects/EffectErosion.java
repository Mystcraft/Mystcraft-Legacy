package com.xcompwiz.mystcraft.effects;

import java.util.Random;

import com.xcompwiz.mystcraft.api.world.logic.IEnvironmentalEffect;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.feature.WorldGenLakes;
import net.minecraftforge.fluids.BlockFluidBase;

public class EffectErosion implements IEnvironmentalEffect {

	private int updateLCG = (new Random()).nextInt();

	public void onChunkPopulate(World worldObj, Random rand, int x, int z) {
		if (rand.nextInt(4) == 0) {
			int i1 = x + rand.nextInt(16) + 8;
			int j2 = rand.nextInt(256);
			int k3 = z + rand.nextInt(16) + 8;
			(new WorldGenLakes(Blocks.WATER)).generate(worldObj, rand, new BlockPos(i1, j2, k3));
		}
		if (rand.nextInt(8) == 0) {
			int j1 = x + rand.nextInt(16) + 8;
			int k2 = rand.nextInt(rand.nextInt(248) + 8);
			int l3 = z + rand.nextInt(16) + 8;
			(new WorldGenLakes(Blocks.LAVA)).generate(worldObj, rand, new BlockPos(j1, k2, l3));
		}
	}

	@Override
	public void tick(World worldObj, Chunk chunk) {
		int var5 = chunk.x * 16;
		int var6 = chunk.z * 16;
		int var8;

		if (worldObj.rand.nextInt(100) == 0) {
			updateLCG = updateLCG * 3 + 1013904223;
			var8 = updateLCG >> 2;
			int x = var5 + (var8 & 15);
			int y = var6 + (var8 >> 8 & 15);
			int z = (var8 >> 16 & 127);
			BlockPos pos = new BlockPos(x, y, z);
			IBlockState state = worldObj.getBlockState(pos);
			if (state.getBlock() instanceof BlockContainer)
				return;
			if (state.getMaterial().isLiquid())
				return;
			Block ajacent = adjacentFluid(worldObj, pos);
			if (ajacent != null) {
				worldObj.setBlockState(pos, ajacent.getDefaultState());
			}
		}
	}

	private Block adjacentFluid(World par1World, BlockPos pos) {
		for (EnumFacing face : EnumFacing.VALUES) {
			if (face != EnumFacing.DOWN) {
				IBlockState adj = par1World.getBlockState(pos.offset(face));
				if (adj.getBlock() instanceof BlockLiquid || adj.getBlock() instanceof BlockFluidBase) {
					return adj.getBlock();
				}
			}
		}
		return null;
	}
}
