package com.xcompwiz.mystcraft.symbol.symbols;

import java.util.Random;

import com.xcompwiz.mystcraft.api.world.AgeDirector;
import com.xcompwiz.mystcraft.api.world.logic.ITerrainAlteration;
import com.xcompwiz.mystcraft.symbol.SymbolBase;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.NoiseGeneratorOctaves;

public class SymbolSkylands extends SymbolBase {

	public SymbolSkylands(String identifier) {
		super(identifier);
	}

	@Override
	public void registerLogic(AgeDirector controller, long seed) {
		NoiseGeneratorOctaves noise = new NoiseGeneratorOctaves(new Random(controller.getSeed()), 7);
		controller.registerInterface(new TerrainAlteration(noise));
		controller.setCloudHeight(42.5F);
		controller.setHorizon(0);
	}

	private static class TerrainAlteration implements ITerrainAlteration {

		private static final double		factor		= 0.03125D;

		private double					skyNoise[]	= null;
		private NoiseGeneratorOctaves	noiseGen;

		public TerrainAlteration(NoiseGeneratorOctaves noise) {
			noiseGen = noise;
		}

		@Override
		public void alterTerrain(World worldObj, int chunkX, int chunkZ, ChunkPrimer primer) {
			skyNoise = noiseGen.generateNoiseOctaves(skyNoise, chunkX * 16, chunkZ * 16, 16, 16, 16, 1, factor * 64D, factor * 32D, factor * 64D);
			int layers = 256;
			for (int y = 0; y < layers; ++y) {
				for (int z = 0; z < 16; ++z) {
					for (int x = 0; x < 16; ++x) {
						int height = (76) + (int) (skyNoise[z | x << 4]);
						int coords = y << 8 | z << 4 | x;

						if (primer.getBlockState(x, y, z).getBlock().equals(Blocks.WATER) && !isSupported(x, y, z, height, primer)) {
							primer.setBlockState(x, y, z, Blocks.AIR.getDefaultState());
						}
						if (y <= height) {
							primer.setBlockState(x, y, z, Blocks.AIR.getDefaultState());
						}
					}
				}
			}
		}

		private boolean isSupported(int x, int y, int z, int sky, ChunkPrimer primer) {
			if (y < 1) return false;
			IBlockState block = primer.getBlockState(x, y - 1, z);
			if (block.getBlock().equals(Blocks.AIR)) return false;
			if (block.getBlock().equals(Blocks.WATER)) return false;
			return false;
		}
	}
}
