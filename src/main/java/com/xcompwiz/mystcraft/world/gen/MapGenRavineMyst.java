package com.xcompwiz.mystcraft.world.gen;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class MapGenRavineMyst extends MapGenAdvanced {
	private float[]	field_75046_d	= new float[1024];

	public MapGenRavineMyst(long seed, Block block) {
		this(seed, block, (byte) 0);
	}

	public MapGenRavineMyst(long seed, Block block, byte blockMeta) {
		super(seed, block, blockMeta);
	}

	protected void generateRavine(long seed, int chunkX, int chunkZ, Block[] blocks, byte[] metadata, double baseX, double baseY, double baseZ, float par12, float par13, float par14, int par15, int par16, double par17) {
		Random rand = new Random(seed);
		double chunkXmid = (chunkX * 16 + 8);
		double chunkZmid = (chunkZ * 16 + 8);
		int layers = blocks.length / 256;
		float var24 = 0.0F;
		float var25 = 0.0F;

		if (par16 <= 0) {
			int var26 = this.range * 16 - 16;
			par16 = var26 - rand.nextInt(var26 / 4);
		}

		boolean flag1 = false;

		if (par15 == -1) {
			par15 = par16 / 2;
			flag1 = true;
		}

		float var27 = 1.0F;

		for (int var28 = 0; var28 < 128; ++var28) {
			if (var28 == 0 || rand.nextInt(3) == 0) {
				var27 = 1.0F + rand.nextFloat() * rand.nextFloat() * 1.0F;
			}

			this.field_75046_d[var28] = var27 * var27;
		}

		for (; par15 < par16; ++par15) {
			double var53 = 1.5D + (MathHelper.sin(par15 * (float) Math.PI / par16) * par12 * 1.0F);
			double var30 = var53 * par17;
			var53 *= rand.nextFloat() * 0.25D + 0.75D;
			var30 *= rand.nextFloat() * 0.25D + 0.75D;
			float var32 = MathHelper.cos(par14);
			float var33 = MathHelper.sin(par14);
			baseX += (MathHelper.cos(par13) * var32);
			baseY += var33;
			baseZ += (MathHelper.sin(par13) * var32);
			par14 *= 0.7F;
			par14 += var25 * 0.05F;
			par13 += var24 * 0.05F;
			var25 *= 0.8F;
			var24 *= 0.5F;
			var25 += (rand.nextFloat() - rand.nextFloat()) * rand.nextFloat() * 2.0F;
			var24 += (rand.nextFloat() - rand.nextFloat()) * rand.nextFloat() * 4.0F;

			if (flag1 || rand.nextInt(4) != 0) {
				double var34 = baseX - chunkXmid;
				double var36 = baseZ - chunkZmid;
				double var38 = (par16 - par15);
				double var40 = (par12 + 2.0F + 16.0F);

				if (var34 * var34 + var36 * var36 - var38 * var38 > var40 * var40) { return; }

				if (baseX >= chunkXmid - 16.0D - var53 * 2.0D && baseZ >= chunkZmid - 16.0D - var53 * 2.0D && baseX <= chunkXmid + 16.0D + var53 * 2.0D && baseZ <= chunkZmid + 16.0D + var53 * 2.0D) {
					int minX = MathHelper.floor_double(baseX - var53) - chunkX * 16 - 1;
					int maxX = MathHelper.floor_double(baseX + var53) - chunkX * 16 + 1;
					int minY = MathHelper.floor_double(baseY - var30) - 1;
					int maxY = MathHelper.floor_double(baseY + var30) + 1;
					int minZ = MathHelper.floor_double(baseZ - var53) - chunkZ * 16 - 1;
					int maxZ = MathHelper.floor_double(baseZ + var53) - chunkZ * 16 + 1;

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

					boolean foundwater = false;
					for (int localY = maxY + 1; !foundwater && localY >= minY - 1; --localY) {
						for (int localZ = minZ; !foundwater && localZ < maxZ; ++localZ) {
							for (int localX = minX; !foundwater && localX < maxX; ++localX) {
								int coords = localY << 8 | localZ << 4 | localX;

								if (localY >= 0 && localY < layers) {
									if (blocks[coords] == Blocks.WATER || blocks[coords] == Blocks.WATER) {
										foundwater = true;
									}

									if (localY != minY - 1 && localX != minX && localX != maxX - 1 && localZ != minZ && localZ != maxZ - 1) {
										localY = minY;
									}
								}
							}
						}
					}

					if (!foundwater) {
						for (int localY = 0; localY < minY; ++localY) {
							double yfactor = (localY + 0.5D - baseY) / var30;
							double yfactorSq = yfactor * yfactor;
							for (int localZ = minZ; localZ < maxZ; ++localZ) {
								double zfactor = ((localZ + chunkZ * 16) + 0.5D - baseZ) / var53;
								double zfactorSq = zfactor * zfactor;
								for (int localX = minX; localX < maxX; ++localX) {
									double xfactor = ((localX + chunkX * 16) + 0.5D - baseX) / var53;
									double xfactorSq = xfactor * xfactor;

									int coords = localY << 8 | localZ << 4 | localX;

									if (xfactorSq + zfactorSq < 1.0D) {
										if ((xfactorSq + zfactorSq) * this.field_75046_d[localY] + yfactorSq / 6.0D < 1.0D) {
											placeBlock(blocks, metadata, coords);
										}
									}
								}
							}
						}

						if (flag1) {
							break;
						}
					}
				}
			}
		}
	}

	/**
	 * Recursively called by generate() (generate) and optionally by itself.
	 */
	@Override
	protected void recursiveGenerate(World par1World, int par2, int par3, int par4, int par5, Block[] blocks, byte[] metadata) {
		if (this.rand.nextInt(50) == 0) {
			double x = (par2 * 16 + this.rand.nextInt(16));
			double y = (this.rand.nextInt(this.rand.nextInt(40) + 8) + 20);
			double z = (par3 * 16 + this.rand.nextInt(16));
			byte var13 = 1;

			for (int var14 = 0; var14 < var13; ++var14) {
				float var15 = this.rand.nextFloat() * (float) Math.PI * 2.0F;
				float var16 = (this.rand.nextFloat() - 0.5F) * 2.0F / 8.0F;
				float var17 = (this.rand.nextFloat() * 2.0F + this.rand.nextFloat()) * 2.0F;
				this.generateRavine(this.rand.nextLong(), par4, par5, blocks, metadata, x, y, z, var17, var15, var16, 0, 0, 3.0D);
			}
		}
	}
}
