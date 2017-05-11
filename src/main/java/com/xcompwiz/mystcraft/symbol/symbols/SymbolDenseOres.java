package com.xcompwiz.mystcraft.symbol.symbols;

import java.util.Random;

import com.xcompwiz.mystcraft.api.event.DenseOresEvent;
import com.xcompwiz.mystcraft.api.world.AgeDirector;
import com.xcompwiz.mystcraft.api.world.logic.IPopulate;
import com.xcompwiz.mystcraft.symbol.SymbolBase;

import net.minecraft.block.state.pattern.BlockMatcher;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.MinecraftForge;

public class SymbolDenseOres extends SymbolBase {

	public SymbolDenseOres(String identifier) {
		super(identifier);
	}

	@Override
	public void registerLogic(AgeDirector controller, long seed) {
		controller.registerInterface(new Populator());
	}

	private class Populator implements IPopulate {

		WorldGenerator	coalGen			= new WorldGenMinable(Blocks.COAL_ORE.getDefaultState(),     16);
		WorldGenerator	ironGen			= new WorldGenMinable(Blocks.IRON_ORE.getDefaultState(),     8);
		WorldGenerator	goldGen			= new WorldGenMinable(Blocks.GOLD_ORE.getDefaultState(),     8);
		WorldGenerator	redstoneGen		= new WorldGenMinable(Blocks.REDSTONE_ORE.getDefaultState(), 7);
		WorldGenerator	diamondGen		= new WorldGenMinable(Blocks.DIAMOND_ORE.getDefaultState(),  7);
		WorldGenerator	lapisGen		= new WorldGenMinable(Blocks.LAPIS_ORE.getDefaultState(),    6);
		WorldGenerator	emeraldGen		= new WorldGenMinable(Blocks.EMERALD_ORE.getDefaultState(),  1);
		WorldGenerator	netherQuartzGen	= new WorldGenMinable(Blocks.QUARTZ_ORE.getDefaultState(),   13, BlockMatcher.forBlock(Blocks.NETHERRACK));

		@Override
		public boolean populate(World worldObj, Random rand, int xPos, int zPos, boolean flag) {
			genStandardOre(worldObj, 20, rand, coalGen, xPos, zPos, 0, 128);
			genStandardOre(worldObj, 20, rand, ironGen, xPos, zPos, 0, 64);
			genStandardOre(worldObj, 2, rand, goldGen, xPos, zPos, 0, 32);
			genStandardOre(worldObj, 8, rand, redstoneGen, xPos, zPos, 0, 16);
			genStandardOre(worldObj, 1, rand, diamondGen, xPos, zPos, 0, 16);
			genStandardOre(worldObj, 1, rand, lapisGen, xPos, zPos, 0, 16);
			genStandardOre(worldObj, 6, rand, emeraldGen, xPos, zPos, 4, 32);
			genStandardOre(worldObj, 10, rand, netherQuartzGen, xPos, zPos, 10, 256);
			MinecraftForge.EVENT_BUS.post(new DenseOresEvent(worldObj, rand, xPos, zPos));
			return false;
		}

		protected void genStandardOre(World worldObj, int times, Random rand, WorldGenerator worldgenerator, int x, int z, int minY, int maxY) {
			for (int l = 0; l < times; ++l) {
				int i1 = x + rand.nextInt(16);
				int j1 = rand.nextInt(maxY - minY) + minY;
				int k1 = z + rand.nextInt(16);
				worldgenerator.generate(worldObj, rand, new BlockPos(i1, j1, k1));
			}
		}

	}
}
