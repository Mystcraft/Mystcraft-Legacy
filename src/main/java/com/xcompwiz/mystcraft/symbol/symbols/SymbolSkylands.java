package com.xcompwiz.mystcraft.symbol.symbols;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.NoiseGeneratorOctaves;

import com.xcompwiz.mystcraft.api.world.AgeDirector;
import com.xcompwiz.mystcraft.api.world.logic.ITerrainAlteration;
import com.xcompwiz.mystcraft.symbol.SymbolBase;

public class SymbolSkylands extends SymbolBase {

	@Override
	public void registerLogic(AgeDirector controller, long seed) {
		NoiseGeneratorOctaves noise = new NoiseGeneratorOctaves(new Random(controller.getSeed()), 7);
		controller.registerInterface(new TerrainAlteration(noise));
		controller.setCloudHeight(42.5F);
		controller.setHorizon(0);
	}

	@Override
	public String identifier() {
		return "Skylands";
	}

	private static class TerrainAlteration implements ITerrainAlteration {
		private static final double		factor		= 0.03125D;

		private double					skyNoise[]	= null;
		private NoiseGeneratorOctaves	noiseGen;

		public TerrainAlteration(NoiseGeneratorOctaves noise) {
			noiseGen = noise;
		}

		@Override
		public void alterTerrain(World worldObj, int chunkX, int chunkZ, Block[] blocks, byte[] metadata) {
			skyNoise = noiseGen.generateNoiseOctaves(skyNoise, chunkX * 16, chunkZ * 16, 16, 16, 16, 1, factor * 64D, factor * 32D, factor * 64D);
			int layers = blocks.length / 256;
			for (int y = 0; y < layers; ++y) {
				for (int z = 0; z < 16; ++z) {
					for (int x = 0; x < 16; ++x) {
						int height = (76) + (int) (skyNoise[z | x << 4]);
						int coords = y << 8 | z << 4 | x;

						if (blocks[coords] == Blocks.water && !isSupported(x, y, z, height, blocks)) {
							blocks[coords] = Blocks.air;
						}
						if (y <= height) {
							blocks[coords] = Blocks.air;
						}
					}
				}
			}
		}

		private boolean isSupported(int x, int y, int z, int sky, Block[] blocks) {
			if (y < 1) return false;
			Block block = blocks[(y - 1) << 8 | z << 4 | x];
			if (block == Blocks.air) return false;
			if (block == Blocks.water) return false;
			return false;
		}
	}
}
