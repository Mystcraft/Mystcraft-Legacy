package com.xcompwiz.mystcraft.world.gen;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class MapGenSpheresMyst extends MapGenAdvanced {
	public MapGenSpheresMyst(long seed, Block block) {
		this(seed, block, (byte) 0);
	}

	public MapGenSpheresMyst(long seed, Block block, byte blockMeta) {
		super(seed, block, blockMeta);
	}

	/**
	 * Generates a larger initial cave node than usual. Called 25% of the time.
	 */
	protected void generateLargeCaveNode(long seed, int chunkX, int chunkZ, Block[] blocks, byte[] metadata, double baseX, double baseY, double baseZ) {
		generateCaveNode(seed, chunkX, chunkZ, blocks, metadata, baseX, baseY, baseZ, 1.0F + rand.nextFloat() * 6F, 0.0F, 0.0F, -1, -1, 1.0D);
	}

	/**
	 * Generates a node in the current cave system recursion tree.
	 */
	protected void generateCaveNode(long seed, int chunkX, int chunkZ, Block[] blocks, byte[] metadata, double baseX, double baseY, double baseZ, float angleA, float angleB, float angleC, int loopc, int maxLoops, double squash) {
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
			double d2 = 1.5D + (MathHelper.sin((loopc * (float) Math.PI) / maxLoops) * angleA * 1.0F);
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
			f1 += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 2.0F;
			f += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 4F;

			if (!flag && loopc == j && angleA > 1.0F && maxLoops > 0) {
				generateCaveNode(random.nextLong(), chunkX, chunkZ, blocks, metadata, baseX, baseY, baseZ, random.nextFloat() * 0.5F + 0.5F, angleB - ((float) Math.PI / 2F), angleC / 3F, loopc, maxLoops, 1.0D);
				generateCaveNode(random.nextLong(), chunkX, chunkZ, blocks, metadata, baseX, baseY, baseZ, random.nextFloat() * 0.5F + 0.5F, angleB + ((float) Math.PI / 2F), angleC / 3F, loopc, maxLoops, 1.0D);
				return;
			}

			if (!flag && random.nextInt(4) == 0) {
				continue;
			}

			double xoffset = baseX - chunkXmid;
			double yoffset = baseZ - chunkZmid;
			double zoffset = maxLoops - loopc;
			double d7 = angleA + 2.0F + 16F;

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

							double total = xfactorSq + yfactorSq + zfactorSq;
							if (total < 1.0D) {
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
	protected void recursiveGenerate(World par1World, int x, int z, int chunkX, int chunkZ, Block[] blocks, byte[] metadata) {
		float roll = rand.nextFloat();
		if (roll > 0.05F) return;

		double dx = x * 16 + rand.nextInt(16);
		double dy = rand.nextInt(rand.nextInt(192) + 1) + 32;
		double dz = z * 16 + rand.nextInt(16);
		// generateLargeCaveNode(rand.nextLong(), chunkX, chunkZ, blocks, metadata, dx, dy, dz);
		generateCaveNode(rand.nextLong(), chunkX, chunkZ, blocks, metadata, dx, dy, dz, 1.0F + rand.nextFloat() * 4F, 0.0F, 0.0F, -1, -1, 1.0D);
	}
}
