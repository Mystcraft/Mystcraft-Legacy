package com.xcompwiz.mystcraft.world.gen;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.NoiseGeneratorOctaves;

public class MapGenFloatingIslands extends MapGenAdvanced {
	public interface IModifiedHandler {
		void passModified(int chunkX, int chunkZ, boolean[] modified, Biome biome);
	}

	private int						rate	= 192;

	private NoiseGeneratorOctaves	noiseGen4;
	private double					stoneNoise[];

	private Biome			biome;

	private IModifiedHandler		callback;

	public MapGenFloatingIslands(long seed, Biome biome, IModifiedHandler callback, IBlockState state) {
		super(seed, state);
		this.range = 5;
		noiseGen4 = new NoiseGeneratorOctaves(rand, 4);

		this.biome = biome;
		this.callback = callback;
	}

	/**
	 * Generates a node in the current cave system recursion tree.
	 * @param modified
	 */
	protected void generateCaveNode(long seed, int chunkX, int chunkZ, IBlockState[] blocks, boolean[] modified, double baseX, double baseY, double baseZ, float scalar, float angleB, float angleC, int loopc, int maxLoops, double squash) {
		double chunkXmid = chunkX * 16 + 8;
		double chunkZmid = chunkZ * 16 + 8;
		int layers = blocks.length / 256;
		float f = 0.0F;
		float f1 = 0.0F;
		Random random = new Random(seed);

		if (maxLoops <= 0) {
			int i = range * 16 - 16;
			maxLoops = i - random.nextInt(i / 4);
		}

		boolean flag = false;

		if (loopc == -1) {
			loopc = maxLoops / 2;
			flag = true;
		}

		int j = random.nextInt(maxLoops / 2) + maxLoops / 4;
		boolean flag1 = random.nextInt(6) == 0;

		for (; loopc < maxLoops; ++loopc) {
			double d2 = 1.5D + (MathHelper.sin((loopc * (float) Math.PI) / maxLoops) * scalar * 1.0F);
			double d3 = d2 * squash;
			float f2 = MathHelper.cos(angleC);
			float f3 = MathHelper.sin(angleC);
			baseX += MathHelper.cos(angleB) * f2;
			baseY += f3;
			baseZ += MathHelper.sin(angleB) * f2;

			if (flag1) {
				angleC *= 0.92F;
			} else {
				angleC *= 0.7F;
			}

			angleC += f1 * 0.1F;
			angleB += f * 0.1F;
			f1 *= 0.9F;
			f *= 0.75F;
			f1 += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 1.0F;
			f += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 4F;

			if (!flag && loopc == j && scalar > 1.0F && maxLoops > 0) {
				generateCaveNode(random.nextLong(), chunkX, chunkZ, blocks, modified, baseX, baseY, baseZ, random.nextFloat() * 0.5F + 0.5F, angleB - ((float) Math.PI / 2F), angleC / 3F, loopc, maxLoops, squash);
				generateCaveNode(random.nextLong(), chunkX, chunkZ, blocks, modified, baseX, baseY, baseZ, random.nextFloat() * 0.5F + 0.5F, angleB + ((float) Math.PI / 2F), angleC / 3F, loopc, maxLoops, squash);
				return;
			}

			if (!flag && random.nextInt(4) == 0) {
				continue;
			}

			double xoffset = baseX - chunkXmid;
			double yoffset = baseZ - chunkZmid;
			double zoffset = maxLoops - loopc;
			double d7 = scalar + 2.0F + 16F;

			if ((xoffset * xoffset + yoffset * yoffset) - zoffset * zoffset > d7 * d7) { return; }

			if (baseX < chunkXmid - 16D - d2 * 2D || baseZ < chunkZmid - 16D - d2 * 2D || baseX > chunkXmid + 16D + d2 * 2D || baseZ > chunkZmid + 16D + d2 * 2D) {
				continue;
			}

			int minX = MathHelper.floor(baseX - d2) - chunkX * 16 - 1;
			int maxX = (MathHelper.floor(baseX + d2) - chunkX * 16) + 1;
			int minY = MathHelper.floor(baseY - d3) - 1;
			int maxY = MathHelper.floor(baseY + d3) + 1;
			int minZ = MathHelper.floor(baseZ - d2) - chunkZ * 16 - 1;
			int maxZ = (MathHelper.floor(baseZ + d2) - chunkZ * 16) + 1;

			if (minX < 0) {
				minX = 0;
			}

			if (maxX > 16) {
				maxX = 16;
			}

			if (minY < 1) {
				minY = 1;
			}

			if (maxY > layers) {
				maxY = layers;
			}

			if (minZ < 0) {
				minZ = 0;
			}

			if (maxZ > 16) {
				maxZ = 16;
			}

			for (int localY = minY; localY < maxY; ++localY) {
				double yfactor = ((localY + 0.5D) - baseY) / d3;
				double yfactorSq = yfactor * yfactor;
				for (int localZ = minZ; localZ < maxZ; ++localZ) {
					double zfactor = (((localZ + chunkZ * 16) + 0.5D) - baseZ) / d2;
					double zfactorSq = zfactor * zfactor;
					for (int localX = minX; localX < maxX; ++localX) {
						double xfactor = (((localX + chunkX * 16) + 0.5D) - baseX) / d2;
						double xfactorSq = xfactor * xfactor;

						int coords = localY << 8 | localZ << 4 | localX;

						double total = xfactorSq + yfactorSq + zfactorSq;
						if (total < 1.0D) {
							placeBlock(blocks, modified, coords);
						}
					}
				}
			}

			if (flag) {
				break;
			}
		}
	}

	protected void placeBlock(IBlockState[] blocks, boolean[] modified, int coords) {
		if (super.placeBlock(blocks, coords)) {
			modified[coords & 255] = true;
		}
	}

	private void replaceBlocksForBiome(int chunkX, int chunkZ, IBlockState blocks[], boolean[] modified, Biome biome) {
		int layers = blocks.length / 256;
		double noisefactor = 0.03125D;
		stoneNoise = noiseGen4.generateNoiseOctaves(stoneNoise, chunkX * 16, chunkZ * 16, 0, 16, 16, 1, noisefactor * 2D, noisefactor * 2D, noisefactor * 2D);

		for (int z = 0; z < 16; z++) {
			for (int x = 0; x < 16; x++) {
				if (!modified[x + z * 16]) continue;

				int stone_noise_val = (int) (stoneNoise[z + x * 16] / 3D + 3D + rand.nextDouble() * 0.25D);
				int counter = -1;
				IBlockState filler = biome.fillerBlock;
				for (int y = layers - 1; y >= 0; --y) {
					int coords = y << 8 | z << 4 | x;
					IBlockState blockId = blocks[coords];
					if (blockId == null) {
						if (counter != -1 && (filler.getBlock().equals(Blocks.SAND) || filler.getBlock().equals(Blocks.SANDSTONE))) {
							blocks[coords] = Blocks.SANDSTONE.getDefaultState();
						}
						counter = -1;
						continue;
					}
					if (blockId != Blocks.STONE) {
						continue;
					}
					if (counter == -1) {
						IBlockState surface = biome.topBlock;
						filler = biome.fillerBlock;
						counter = stone_noise_val;
						blocks[coords] = surface;
						continue;
					}
					if (counter <= 0) {
						continue;
					}
					--counter;
					blocks[coords] = filler;
					if (counter == 0 && filler == Blocks.SAND) {
						counter = rand.nextInt(4);
						filler = Blocks.SANDSTONE.getDefaultState();
					}
				}

			}

		}

	}

	/**
	 * Recursively called by generate() (generate) and optionally by itself.
	 */
	@Override
	protected void recursiveGenerate(World worldObj, int x, int z, int chunkX, int chunkZ, IBlockState[] blocks) {
		if (rand.nextInt(rate) != 0) { return; }
		boolean[] modified = new boolean[256];

		double dx = x * 16 + rand.nextInt(16);
		double dy = rand.nextInt(rand.nextInt(50) + 50) + 150;
		double dz = z * 16 + rand.nextInt(16);
		// generateLargeCaveNode(rand.nextLong(), chunkX, chunkZ, blocks, metadata, dx, dy, dz);
		generateCaveNode(rand.nextLong(), chunkX, chunkZ, blocks, modified, dx, dy, dz, 12F, 0.0F, 0.0F, -1, -1, 0.2D);
		int subelements = rand.nextInt(12) + 40;
		for (int i = 0; i < subelements; ++i) {
			double subx = dx + (rand.nextDouble() - rand.nextDouble()) * 20.0D;
			double suby = dy + (rand.nextDouble() - rand.nextDouble()) * 10.0D;
			double subz = dz + (rand.nextDouble() - rand.nextDouble()) * 20.0D;
			float scale = rand.nextFloat() * 3F + 1F;
			generateCaveNode(rand.nextLong(), chunkX, chunkZ, blocks, modified, subx, suby, subz, scale, 0.0F, 0.0F, -1, -1, 0.4D);
		}
		replaceBlocksForBiome(chunkX, chunkZ, blocks, modified, biome);
		callback.passModified(chunkX, chunkZ, modified, biome);
	}
}
