package com.xcompwiz.mystcraft.symbol.symbols;

import java.util.Random;

import com.xcompwiz.mystcraft.api.symbol.BlockCategory;
import com.xcompwiz.mystcraft.api.symbol.BlockDescriptor;
import com.xcompwiz.mystcraft.api.symbol.ModifierUtils;
import com.xcompwiz.mystcraft.api.world.AgeDirector;
import com.xcompwiz.mystcraft.symbol.SymbolBase;
import com.xcompwiz.mystcraft.symbol.TerrainGeneratorBase;

import net.minecraft.world.gen.NoiseGeneratorOctaves;

public class SymbolTerrainGenNether extends SymbolBase {

	public SymbolTerrainGenNether(String identifier) {
		super(identifier);
	}

	@Override
	public void registerLogic(AgeDirector controller, long seed) {
		TerrainGenerator gen = new TerrainGenerator(controller);
		BlockDescriptor block;
		block = ModifierUtils.popBlockMatching(controller, BlockCategory.SEA);
		if (block != null) {
			gen.setSeaBlock(block.block, block.metadata);
		}
		block = ModifierUtils.popBlockMatching(controller, BlockCategory.TERRAIN);
		if (block != null) {
			gen.setTerrainBlock(block.block, block.metadata);
		}
		controller.registerInterface(gen);
		controller.setCloudHeight(200);
		controller.setHorizon(128);
		controller.setSeaLevel(32);
	}

	private static class TerrainGenerator extends TerrainGeneratorBase {
		private Random					rand;
		private NoiseGeneratorOctaves	noiseGen1;
		private NoiseGeneratorOctaves	noiseGen2;
		private NoiseGeneratorOctaves	noiseGen3;
		private NoiseGeneratorOctaves	noiseGen4;
		private NoiseGeneratorOctaves	noiseGen5;

		private double					noiseData1[];
		private double					noiseData2[];
		private double					noiseData3[];
		private double					noiseData4[];
		private double					noiseData5[];

		public TerrainGenerator(AgeDirector controller) {
			super(controller);
			rand = new Random(controller.getSeed());
			bedrockGen = new Random(controller.getSeed());
			// netherCaveGenerator = new MapGenCavesHell();
			noiseGen1 = new NoiseGeneratorOctaves(rand, 16);
			noiseGen2 = new NoiseGeneratorOctaves(rand, 16);
			noiseGen3 = new NoiseGeneratorOctaves(rand, 8);
			noiseGen4 = new NoiseGeneratorOctaves(rand, 10);
			noiseGen5 = new NoiseGeneratorOctaves(rand, 16);
		}

		@Override
		protected double[] initializeNoiseField(double noise_field[], int subchunkX, int subchunkY, int subchunkZ, int sizeX, int sizeY, int sizeZ) {
			if (noise_field == null) {
				noise_field = new double[sizeX * sizeY * sizeZ];
			}

			double cfactor1 = 684.41200000000003D;
			double cfactor2 = 2053.2359999999999D;
			noiseData4 = noiseGen4.generateNoiseOctaves(noiseData4, subchunkX, subchunkY, subchunkZ, sizeX, 1, sizeZ, 1.0D, 0.0D, 1.0D); // Not really used
			noiseData5 = noiseGen5.generateNoiseOctaves(noiseData5, subchunkX, subchunkY, subchunkZ, sizeX, 1, sizeZ, 100D, 0.0D, 100D);
			noiseData3 = noiseGen3.generateNoiseOctaves(noiseData3, subchunkX, subchunkY, subchunkZ, sizeX, sizeY, sizeZ, cfactor1 / 80D, cfactor2 / 60D, cfactor1 / 80D);
			noiseData1 = noiseGen1.generateNoiseOctaves(noiseData1, subchunkX, subchunkY, subchunkZ, sizeX, sizeY, sizeZ, cfactor1, cfactor2, cfactor1);
			noiseData2 = noiseGen2.generateNoiseOctaves(noiseData2, subchunkX, subchunkY, subchunkZ, sizeX, sizeY, sizeZ, cfactor1, cfactor2, cfactor1);
			int i = 0;
			int j = 0;
			double ad[] = new double[sizeY];

			for (int y = 0; y < sizeY; y++) {
				ad[y] = Math.cos((y * Math.PI * 6D) / sizeY) * 2D;
				double d2 = y;

				if (y > sizeY / 2) {
					d2 = sizeY - 1 - y;
				}

				if (d2 < 4D) {
					d2 = 4D - d2;
					ad[y] -= d2 * d2 * d2 * 10D;
				}
			}

			for (int x = 0; x < sizeX; x++) {
				for (int z = 0; z < sizeZ; z++) {
					double d3 = (noiseData4[j] + 256D) / 512D; // This variable is never read, only assigned

					if (d3 > 1.0D) {
						d3 = 1.0D;
					}

					double d4 = 0.0D;
					double d5 = noiseData5[j] / 8000D;

					if (d5 < 0.0D) {
						d5 = -d5;
					}

					d5 = d5 * 3D - 3D;

					if (d5 < 0.0D) {
						d5 /= 2D;

						if (d5 < -1D) {
							d5 = -1D;
						}

						d5 /= 1.3999999999999999D;
						d5 /= 2D;
						d3 = 0.0D;
					} else {
						if (d5 > 1.0D) {
							d5 = 1.0D;
						}

						d5 /= 6D;
					}

					d3 += 0.5D;
					d5 = (d5 * sizeY) / 16D;
					j++;

					for (int j1 = 0; j1 < sizeY; j1++) {
						double d6 = 0.0D;
						double d7 = ad[j1];
						double d8 = noiseData1[i] / 512D;
						double d9 = noiseData2[i] / 512D;
						double d10 = (noiseData3[i] / 10D + 1.0D) / 2D;

						if (d10 < 0.0D) {
							d6 = d8;
						} else if (d10 > 1.0D) {
							d6 = d9;
						} else {
							d6 = d8 + (d9 - d8) * d10;
						}

						d6 -= d7;

						if (j1 > sizeY - 4) {
							double d11 = (j1 - (sizeY - 4)) / 3F;
							d6 = d6 * (1.0D - d11) + -10D * d11;
						}

						if (j1 < d4) {
							double d12 = (d4 - j1) / 4D;

							if (d12 < 0.0D) {
								d12 = 0.0D;
							}

							if (d12 > 1.0D) {
								d12 = 1.0D;
							}

							d6 = d6 * (1.0D - d12) + -10D * d12;
						}

						noise_field[i] = d6;
						i++;
					}
				}
			}

			return noise_field;
		}
	}
}
