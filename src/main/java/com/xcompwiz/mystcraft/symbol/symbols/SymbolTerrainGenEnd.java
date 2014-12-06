package com.xcompwiz.mystcraft.symbol.symbols;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.gen.NoiseGeneratorOctaves;

import com.xcompwiz.mystcraft.api.symbol.BlockCategory;
import com.xcompwiz.mystcraft.api.symbol.ModifierUtils;
import com.xcompwiz.mystcraft.api.world.IAgeController;
import com.xcompwiz.mystcraft.symbol.BlockDescriptor;
import com.xcompwiz.mystcraft.symbol.SymbolBase;
import com.xcompwiz.mystcraft.symbol.TerrainGeneratorBase;

public class SymbolTerrainGenEnd extends SymbolBase {

	@Override
	public void registerLogic(IAgeController controller, long seed) {
		TerrainGenerator gen = new TerrainGenerator(controller);
		BlockDescriptor block;
		block = ModifierUtils.popBlockMatching(controller, BlockCategory.TERRAIN);
		if (block != null) {
			gen.setTerrainBlock(block.block, block.metadata);
		}
		block = ModifierUtils.popBlockMatching(controller, BlockCategory.SEA);
		if (block != null) {
			gen.setSeaBlock(block.block, block.metadata);
		} else {
			gen.setSeaBlock(Blocks.air, (byte) 0);
		}
		controller.registerInterface(gen);
		controller.setHorizon(0);
		controller.setSeaLevel(49);
		controller.setDrawHorizon(false);
		controller.setDrawVoid(false);
	}

	@Override
	public String identifier() {
		return "TerrainEnd";
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

		public TerrainGenerator(IAgeController controller) {
			super(controller);
			rand = new Random(controller.getSeed());
			bedrockGen = new Random(controller.getSeed());
			// netherCaveGenerator = new MapGenCavesHell();
			noiseGen1 = new NoiseGeneratorOctaves(rand, 16);
			noiseGen2 = new NoiseGeneratorOctaves(rand, 16);
			noiseGen3 = new NoiseGeneratorOctaves(rand, 8);
			noiseGen4 = new NoiseGeneratorOctaves(rand, 10);
			noiseGen5 = new NoiseGeneratorOctaves(rand, 16);
			this.genBedrock = false;
		}

		/**
		 * generates a subset of the level's terrain data. Takes 7 arguments: the [empty] noise array, the position, and the size.
		 */
		@Override
		protected double[] initializeNoiseField(double[] noise_field, int subchunkX, int subchunkY, int subchunkZ, int sizeX, int sizeY, int sizeZ) {
			if (noise_field == null) {
				noise_field = new double[sizeX * sizeY * sizeZ];
			}

			double var8 = 684.412D;
			double var10 = 684.412D;
			this.noiseData4 = this.noiseGen4.generateNoiseOctaves(this.noiseData4, subchunkX, subchunkZ, sizeX, sizeZ, 1.121D, 1.121D, 0.5D);
			this.noiseData5 = this.noiseGen5.generateNoiseOctaves(this.noiseData5, subchunkX, subchunkZ, sizeX, sizeZ, 200.0D, 200.0D, 0.5D);
			var8 *= 2.0D;
			this.noiseData1 = this.noiseGen3.generateNoiseOctaves(this.noiseData1, subchunkX, subchunkY, subchunkZ, sizeX, sizeY, sizeZ, var8 / 80.0D, var10 / 160.0D, var8 / 80.0D);
			this.noiseData2 = this.noiseGen1.generateNoiseOctaves(this.noiseData2, subchunkX, subchunkY, subchunkZ, sizeX, sizeY, sizeZ, var8, var10, var8);
			this.noiseData3 = this.noiseGen2.generateNoiseOctaves(this.noiseData3, subchunkX, subchunkY, subchunkZ, sizeX, sizeY, sizeZ, var8, var10, var8);
			int noise_index = 0;
			int noise5_index = 0;

			for (int x = 0; x < sizeX; ++x) {
				for (int z = 0; z < sizeZ; ++z) {
					double noise4 = (this.noiseData4[noise5_index] + 256.0D) / 512.0D;

					if (noise4 > 1.0D) {
						noise4 = 1.0D;
					}

					double noise5 = this.noiseData5[noise5_index] / 8000.0D;

					if (noise5 < 0.0D) {
						noise5 = -noise5 * 0.3D;
					}

					noise5 = noise5 * 3.0D - 2.0D;
					float xCoord = (x + subchunkX - 0) / 1.0F;
					float yCoord = (z + subchunkZ - 0) / 1.0F;
					float distFactor = 100.0F - MathHelper.sqrt_float(xCoord * xCoord + yCoord * yCoord) * 8.0F; // Play with THIS

					if (distFactor > 80.0F) {
						distFactor = 80.0F;
					}

					if (distFactor < -100.0F) {
						distFactor = -100.0F;
					}

					if (noise5 > 1.0D) {
						noise5 = 1.0D;
					}

					noise5 /= 8.0D;
					noise5 = 0.0D;

					if (noise4 < 0.0D) {
						noise4 = 0.0D;
					}

					noise4 += 0.5D;
					noise5 = noise5 * sizeY / 16.0D;
					++noise5_index;
					double halfSizeY = sizeY / 2.0D;

					for (int y = 0; y < sizeY; ++y) {
						double density = 0.0D;
						double terminal = (y - halfSizeY) * 8.0D / noise4; // NOT USED!

						if (terminal < 0.0D) {
							terminal *= -1.0D;
						}

						double noise2 = this.noiseData2[noise_index] / 512.0D;
						double noise3 = this.noiseData3[noise_index] / 512.0D;
						double noise1 = (this.noiseData1[noise_index] / 10.0D + 1.0D) / 2.0D;

						if (noise1 < 0.0D) {
							density = noise2;
						} else if (noise1 > 1.0D) {
							density = noise3;
						} else {
							density = noise2 + (noise3 - noise2) * noise1;
						}

						density -= 8.0D;
						density += distFactor;
						byte value = 2;
						double factor;

						if (y > sizeY / 2 - value) {
							factor = ((y - (sizeY / 2 - value)) / 64.0F);

							if (factor < 0.0D) {
								factor = 0.0D;
							}

							if (factor > 1.0D) {
								factor = 1.0D;
							}

							density = density * (1.0D - factor) + -3000.0D * factor;
						}

						value = 8;

						if (y < value) {
							factor = ((value - y) / (value - 1.0F));
							density = density * (1.0D - factor) + -30.0D * factor;
						}

						noise_field[noise_index] = density;
						++noise_index;
					}
				}
			}

			return noise_field;
		}
	}
}
