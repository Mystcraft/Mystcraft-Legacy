package com.xcompwiz.mystcraft.world.gen;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class MapGenCavesMyst extends MapGenAdvanced {
	private int	rate	= 15;
	private int	size	= 40;

	public MapGenCavesMyst(long seed, int rate, int size, Block block) {
		this(seed, rate, size, block, (byte) 0);
	}

	public MapGenCavesMyst(long seed, int rate, int size, Block block, byte blockMeta) {
		super(seed, block, blockMeta);
		this.rate = rate;
		this.size = size;
	}

	/**
	 * Generates a larger initial cave node than usual. Called 25% of the time.
	 */
	protected void generateLargeCaveNode(long seed, int chunkX, int chunkZ, Block[] blocks, byte[] metadata, double baseX, double baseY, double baseZ) {
		generateCaveNode(seed, chunkX, chunkZ, blocks, metadata, baseX, baseY, baseZ, 1.0F + rand.nextFloat() * 6F, 0.0F, 0.0F, -1, -1, 0.5D);
	}

	/**
	 * Generates a node in the current cave system recursion tree.
	 */
	protected void generateCaveNode(long seed, int chunkX, int chunkZ, Block[] blocks, byte[] metadata, double baseX, double baseY, double baseZ, float par12, float par13, float par14, int par15, int par16, double par17) {
		double chunkXmid = chunkX * 16 + 8;
		double chunkZmid = chunkZ * 16 + 8;
		int layers = blocks.length / 256;
		float f = 0.0F;
		float f1 = 0.0F;
		Random random = new Random(seed);

		if (par16 <= 0) {
			int i = range * 16 - 16;
			par16 = i - random.nextInt(i / 4);
		}

		boolean flag = false;

		if (par15 == -1) {
			par15 = par16 / 2;
			flag = true;
		}

		int j = random.nextInt(par16 / 2) + par16 / 4;
		boolean flag1 = random.nextInt(6) == 0;

		for (; par15 < par16; par15++) {
			double d2 = 1.5D + (MathHelper.sin((par15 * (float) Math.PI) / par16) * par12 * 1.0F);
			double d3 = d2 * par17;
			float f2 = MathHelper.cos(par14);
			float f3 = MathHelper.sin(par14);
			baseX += MathHelper.cos(par13) * f2;
			baseY += f3;
			baseZ += MathHelper.sin(par13) * f2;

			if (flag1) {
				par14 *= 0.92F;
			} else {
				par14 *= 0.7F;
			}

			par14 += f1 * 0.1F;
			par13 += f * 0.1F;
			f1 *= 0.9F;
			f *= 0.75F;
			f1 += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 2.0F;
			f += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 4F;

			if (!flag && par15 == j && par12 > 1.0F && par16 > 0) {
				generateCaveNode(random.nextLong(), chunkX, chunkZ, blocks, metadata, baseX, baseY, baseZ, random.nextFloat() * 0.5F + 0.5F, par13 - ((float) Math.PI / 2F), par14 / 3F, par15, par16, 1.0D);
				generateCaveNode(random.nextLong(), chunkX, chunkZ, blocks, metadata, baseX, baseY, baseZ, random.nextFloat() * 0.5F + 0.5F, par13 + ((float) Math.PI / 2F), par14 / 3F, par15, par16, 1.0D);
				return;
			}

			if (!flag && random.nextInt(4) == 0) {
				continue;
			}

			double xoffset = baseX - chunkXmid;
			double yoffset = baseZ - chunkZmid;
			double zoffset = par16 - par15;
			double d7 = par12 + 2.0F + 16F;

			if ((xoffset * xoffset + yoffset * yoffset) - zoffset * zoffset > d7 * d7) { return; }

			if (baseX < chunkXmid - 16D - d2 * 2D || baseZ < chunkZmid - 16D - d2 * 2D || baseX > chunkXmid + 16D + d2 * 2D || baseZ > chunkZmid + 16D + d2 * 2D) {
				continue;
			}

			int minX = MathHelper.floor_double(baseX - d2) - chunkX * 16 - 1;
			int maxX = (MathHelper.floor_double(baseX + d2) - chunkX * 16) + 1;
			int minY = MathHelper.floor_double(baseY - d3) - 1;
			int maxY = MathHelper.floor_double(baseY + d3) + 1;
			int minZ = MathHelper.floor_double(baseZ - d2) - chunkZ * 16 - 1;
			int maxZ = (MathHelper.floor_double(baseZ + d2) - chunkZ * 16) + 1;

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

						if (xfactorSq + zfactorSq < 1.0D) {
							int coords = localY << 8 | localZ << 4 | localX;

							if (yfactor > -0.69999999999999996D && xfactorSq + yfactorSq + zfactorSq < 1.0D) {
								placeBlock(blocks, metadata, coords);
							}
						}
					}
				}
			}

			if (flag) {
				break;
			}
		}
	}

	/**
	 * Recursively called by generate() (generate) and optionally by itself.
	 */
	@Override
	protected void recursiveGenerate(World worldObj, int x, int y, int chunkX, int chunkZ, Block[] blocks, byte[] metadata) {
		int maxNodes = rand.nextInt(rand.nextInt(rand.nextInt(size) + 1) + 1);

		if (rand.nextInt(rate) != 0) {
			maxNodes = 0;
		}

		for (int j = 0; j < maxNodes; ++j) {
			double d = x * 16 + rand.nextInt(16);
			double d1 = rand.nextInt(rand.nextInt(120) + 8);
			double d2 = y * 16 + rand.nextInt(16);
			int k = 1;

			if (rand.nextInt(4) == 0) {
				generateLargeCaveNode(rand.nextLong(), chunkX, chunkZ, blocks, metadata, d, d1, d2);
				k += rand.nextInt(4);
			}

			for (int l = 0; l < k; ++l) {
				float f = rand.nextFloat() * (float) Math.PI * 2.0F;
				float f1 = ((rand.nextFloat() - 0.5F) * 2.0F) / 8F;
				float f2 = rand.nextFloat() * 2.0F + rand.nextFloat();

				if (rand.nextInt(10) == 0) {
					f2 *= rand.nextFloat() * rand.nextFloat() * 3F + 1.0F;
				}

				generateCaveNode(rand.nextLong(), chunkX, chunkZ, blocks, metadata, d, d1, d2, f2, f, f1, 0, 0, 1.0D);
			}
		}
	}
}
