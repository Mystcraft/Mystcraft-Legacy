package com.xcompwiz.mystcraft.symbol.symbols;

import java.util.Random;

import net.minecraft.util.MathHelper;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.NoiseGeneratorOctaves;

import com.xcompwiz.mystcraft.api.symbol.BlockCategory;
import com.xcompwiz.mystcraft.api.symbol.ModifierUtils;
import com.xcompwiz.mystcraft.api.world.IAgeController;
import com.xcompwiz.mystcraft.symbol.BlockDescriptor;
import com.xcompwiz.mystcraft.symbol.SymbolBase;
import com.xcompwiz.mystcraft.symbol.TerrainGeneratorBase;

public class SymbolTerrainGenNormal extends SymbolBase {

	@Override
	public void registerLogic(IAgeController controller, long seed) {
		TerrainGeneratorNormal gen = new TerrainGeneratorNormal(controller, false);
		BlockDescriptor block;
		block = ModifierUtils.popBlockMatching(controller, BlockCategory.TERRAIN);
		if (block != null) {
			gen.setTerrainBlock(block.block, block.metadata);
		}
		block = ModifierUtils.popBlockMatching(controller, BlockCategory.SEA);
		if (block != null) {
			gen.setSeaBlock(block.block, block.metadata);
		}
		controller.registerInterface(gen);
	}

	@Override
	public String identifier() {
		return "TerrainNormal";
	}

	public static class TerrainGeneratorNormal extends TerrainGeneratorBase {
		private final boolean			amplified;

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
		private float					parabolicField[];
		protected BiomeGenBase			biomesForGeneration[];

		public TerrainGeneratorNormal(IAgeController controller, boolean amplified) {
			super(controller);
			this.amplified = amplified;
			rand = new Random(controller.getSeed());
			bedrockGen = new Random(controller.getSeed());
			noiseGen1 = new NoiseGeneratorOctaves(rand, 16);
			noiseGen2 = new NoiseGeneratorOctaves(rand, 16);
			noiseGen3 = new NoiseGeneratorOctaves(rand, 8);
			noiseGen4 = new NoiseGeneratorOctaves(rand, 10);
			noiseGen5 = new NoiseGeneratorOctaves(rand, 16);
		}

		@Override
		protected double[] initializeNoiseField(double noise_field[], int subchunkX, int subchunkY, int subchunkZ, int sizeX, int sizeY, int sizeZ) {
			biomesForGeneration = controller.getWorldChunkManager().getBiomesForGeneration(biomesForGeneration, subchunkX - 2, subchunkZ - 2, sizeX + 5, sizeZ + 5);
			if (noise_field == null) {
				noise_field = new double[sizeX * sizeY * sizeZ];
			}
			if (parabolicField == null) {
				parabolicField = new float[25];
				for (int x = -2; x <= 2; x++) {
					for (int z = -2; z <= 2; z++) {
						parabolicField[(z + 2) * 5 + x + 2] = 10F / MathHelper.sqrt_float((x * x + z * z) + 0.2F);
					}
				}
			}
			double cfactor1 = 684.412D;
			double cfactor2 = 684.412D;
			noiseData4 = noiseGen4.generateNoiseOctaves(noiseData4, subchunkX, 10, subchunkZ, sizeX, 1, sizeZ, 1.121D, 1.0D, 1.121D); // UNUSED!
			noiseData5 = noiseGen5.generateNoiseOctaves(noiseData5, subchunkX, 10, subchunkZ, sizeX, 1, sizeZ, 200D, 1.0D, 200D);
			noiseData3 = noiseGen3.generateNoiseOctaves(noiseData3, subchunkX, subchunkY, subchunkZ, sizeX, sizeY, sizeZ, cfactor1 / 80D, cfactor2 / 160D, cfactor1 / 80D);
			noiseData1 = noiseGen1.generateNoiseOctaves(noiseData1, subchunkX, subchunkY, subchunkZ, sizeX, sizeY, sizeZ, cfactor1, cfactor2, cfactor1);
			noiseData2 = noiseGen2.generateNoiseOctaves(noiseData2, subchunkX, subchunkY, subchunkZ, sizeX, sizeY, sizeZ, cfactor1, cfactor2, cfactor1);
			int noise_index = 0;
			int noise5_index = 0;
			for (int x = 0; x < sizeX; x++) {
				for (int z = 0; z < sizeZ; z++) {
					float avgMaxHeight = 0.0F;
					float avgMinHeight = 0.0F;
					float sumTotalWeight = 0.0F;
					byte sizec = 2;
					BiomeGenBase biome = biomesForGeneration[x + 2 + (z + 2) * (sizeX + 5)];
					for (int xOffset = -sizec; xOffset <= sizec; ++xOffset) {
						for (int zOffset = -sizec; zOffset <= sizec; ++zOffset) {
							BiomeGenBase secondary_biome = biomesForGeneration[x + xOffset + 2 + (z + zOffset + 2) * (sizeX + 5)];

							float height = secondary_biome.rootHeight;
							float variation = secondary_biome.heightVariation;

							if (amplified && height > 0.0F) {
								height = 1.0F + height * 2.0F;
								variation = 1.0F + variation * 4.0F;
							}

							float weight = this.parabolicField[xOffset + 2 + (zOffset + 2) * 5] / (height + 2.0F);
							if (height > biome.rootHeight) {
								weight /= 2.0F;
							}

							avgMaxHeight += variation * weight;
							avgMinHeight += height * weight;
							sumTotalWeight += weight;
						}
					}

					avgMaxHeight /= sumTotalWeight; // Weighted Average
					avgMinHeight /= sumTotalWeight; // Weighted Average
					avgMaxHeight = avgMaxHeight * 0.9F + 0.1F; // What's this do? Why?
					avgMinHeight = (avgMinHeight * 4F - 1.0F) / 8F; // What's this do? Why?
					double noise5 = noiseData5[noise5_index++] / 8000D;
					if (noise5 < 0.0D) {
						noise5 = -noise5 * 0.3D;
					}
					noise5 = noise5 * 3D - 2D;
					if (noise5 < 0.0D) {
						noise5 /= 2D;
						if (noise5 < -1D) {
							noise5 = -1D;
						}
						noise5 /= 1.4D;
						noise5 /= 2D;
					} else {
						if (noise5 > 1.0D) {
							noise5 = 1.0D;
						}
						noise5 /= 8D;
					}
					for (int y = 0; y < sizeY; y++) {
						double minHeightD = avgMinHeight;
						double maxHeightD = avgMaxHeight;
						minHeightD += noise5 * 0.2D;
						minHeightD = (minHeightD * sizeY) / 16D;
						double height = sizeY / 2D + minHeightD * 4D;
						double density = 0.0D;
						double avgDensity = ((y - height) * 12D * 128D) / 128 / maxHeightD;
						if (avgDensity < 0.0D) {
							avgDensity *= 4D;
						}
						double noise1 = noiseData1[noise_index] / 512D;
						double noise2 = noiseData2[noise_index] / 512D;
						double noise3 = (noiseData3[noise_index] / 10D + 1.0D) / 2D;
						if (noise3 < 0.0D) {
							density = noise1;
						} else if (noise3 > 1.0D) {
							density = noise2;
						} else {
							density = noise1 + (noise2 - noise1) * noise3;
						}
						density -= avgDensity;
						if (y > sizeY - 4) {
							double d11 = (y - (sizeY - 4)) / 3F;
							density = density * (1.0D - d11) + -10D * d11;
						}
						noise_field[noise_index++] = density;
					}
				}
			}

			return noise_field;
		}

	}
}
