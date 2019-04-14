package com.xcompwiz.mystcraft.symbol.symbols;

import java.util.Random;

import com.xcompwiz.mystcraft.api.world.AgeDirector;
import com.xcompwiz.mystcraft.api.world.logic.IPrimerFilter;
import com.xcompwiz.mystcraft.api.world.logic.ITerrainAlteration;
import com.xcompwiz.mystcraft.symbol.SymbolBase;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.NoiseGeneratorOctaves;

import javax.annotation.Nullable;

public class SymbolSkylands extends SymbolBase {

	public SymbolSkylands(ResourceLocation identifier) {
		super(identifier);
	}

	@Override
	public void registerLogic(AgeDirector controller, long seed) {
		NoiseGeneratorOctaves noise = new NoiseGeneratorOctaves(new Random(controller.getSeed()), 7);
		controller.registerInterface(new TerrainAlteration(noise));
		controller.setCloudHeight(42.5F);
		controller.setHorizon(0);
	}

	@Override
	public boolean generatesConfigOption() {
		return true;
	}

	private static class TerrainAlteration implements ITerrainAlteration {

		private double[] skyNoise = new double[16 * 16]; // x * y * z ^= 16 * 1 * 16
		private NoiseGeneratorOctaves noiseGen;

		public TerrainAlteration(NoiseGeneratorOctaves noise) {
			this.noiseGen = noise;
		}

		@Nullable
		@Override
		public IPrimerFilter getGenerationFilter(World world, ChunkPrimer primer, int chunkX, int chunkZ) {
			return new SkylandsFilter(this.noiseGen, chunkX, chunkZ);
		}

		@Override
		public void alterTerrain(World worldObj, int chunkX, int chunkZ, ChunkPrimer primer) {}

		private class SkylandsFilter implements IPrimerFilter {

			private SkylandsFilter(NoiseGeneratorOctaves noise, int chunkX, int chunkZ) {
				skyNoise = noise.generateNoiseOctaves(skyNoise, chunkX * 16, chunkZ * 16, 16, 16, 1, 1, 1);
			}

			@Override
			public IBlockState filter(int x, int y, int z, IBlockState state) {
				Material mat;
				if ((mat = state.getMaterial()) == Material.AIR) {
					return state;
				}

				int height = getCutOffHeight(x, z);
				if (y <= height) {
					return null; //If y is below the cutOff, filter out.
				} else {
					if (mat.isLiquid()) { //If adding a liquid above cutOff height, deny this.
						return null;
					}
				}
				return state;
			}

			private int getCutOffHeight(int x, int z) {
				return 76 + (int) (skyNoise[(x) << 4 | (z)]);
			}

		}
	}
}
