package com.xcompwiz.mystcraft.symbol.symbols;

import java.util.Random;

import com.xcompwiz.mystcraft.api.event.DenseOresEvent;
import com.xcompwiz.mystcraft.api.world.AgeDirector;
import com.xcompwiz.mystcraft.api.world.logic.IPopulate;
import com.xcompwiz.mystcraft.symbol.SymbolBase;

import net.minecraft.init.Blocks;
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
		WorldGenerator	coalGen			= new WorldGenMinable(Blocks.coal_ore, 16);
		WorldGenerator	ironGen			= new WorldGenMinable(Blocks.iron_ore, 8);
		WorldGenerator	goldGen			= new WorldGenMinable(Blocks.gold_ore, 8);
		WorldGenerator	redstoneGen		= new WorldGenMinable(Blocks.redstone_ore, 7);
		WorldGenerator	diamondGen		= new WorldGenMinable(Blocks.diamond_ore, 7);
		WorldGenerator	lapisGen		= new WorldGenMinable(Blocks.lapis_ore, 6);
		WorldGenerator	emeraldGen		= new WorldGenMinable(Blocks.emerald_ore, 1);
		WorldGenerator	netherQuartzGen	= new WorldGenMinable(Blocks.quartz_ore, 13, Blocks.netherrack);

		public Populator() {}

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
				worldgenerator.generate(worldObj, rand, i1, j1, k1);
			}
		}
	}
}
