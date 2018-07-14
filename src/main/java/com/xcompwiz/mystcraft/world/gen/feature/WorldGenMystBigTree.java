package com.xcompwiz.mystcraft.world.gen.feature;

import com.xcompwiz.mystcraft.world.gen.MapGenAdvanced;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;

public class WorldGenMystBigTree extends MapGenAdvanced {

	static final byte otherCoordPairs[] = { 2, 0, 0, 1, 2, 1 };
	static final int coordMaximums[] = { 16, 255, 16 };

	World worldObj;
	int basePos[] = { 0, 0, 0 };
	int trueBase[] = { 0, 0, 0 };
	int heightLimit;
	int height;
	double heightAttenuation;
	double scaleWidth;
	double leafDensity;
	int trunkSize;
	int heightLimitLimit;
	int leafDistanceLimit;
	int leafNodes[][];
	static private int max_height = 50;

	public WorldGenMystBigTree(long seed) {
		super(seed, Blocks.LOG.getDefaultState());

		heightLimit = 0;
		heightAttenuation = 0.9D;
		scaleWidth = 0.2D;
		leafDensity = 0.8D;
		trunkSize = 2;
		heightLimitLimit = max_height;
		leafDistanceLimit = 3;
	}

	void generateLeafNodeList(ChunkPrimer primer) {
		height = (int) (heightLimit * heightAttenuation);
		if (height >= heightLimit) {
			height = heightLimit - 1;
		}
		if (height < max_height / 2) {
			return;
		}
		int i = 4;// (int)(1.0D + Math.pow((leafDensity * (double)heightLimit) / 13D, 2D));
		if (i < 1) {
			i = 1;
		}
		int ai[][] = new int[i * heightLimit][4];
		int j = (basePos[1] + heightLimit) - leafDistanceLimit;
		int k = 1;
		int l = basePos[1] + height;
		int i1 = j - basePos[1];
		ai[0][0] = basePos[0];
		ai[0][1] = j;
		ai[0][2] = basePos[2];
		ai[0][3] = l;
		--j;
		int ai1[] = { 0, 0, 0 };
		int ai2[] = { 0, 0, 0 };
		int ai3[] = { basePos[0], 0, basePos[2] };
		double d = 0.5D;
		while (i1 >= 0) {
			float f = layerSize(i1);
			if (f < 0.0F) {
				--j;
				--i1;
			} else {
				for (int j1 = 0; j1 < i; ++j1) {
					double d1 = scaleWidth * (f * (rand.nextFloat() + 3.0D)); // Limb length (position of node)
					double d2 = rand.nextFloat() * 2D * 3.1415899999999999D; // direction
					int k1 = MathHelper.floor(d1 * Math.sin(d2) + basePos[0] + d);
					int l1 = MathHelper.floor(d1 * Math.cos(d2) + basePos[2] + d);
					ai1[0] = k1;
					ai1[1] = j;
					ai1[2] = l1;
					ai2[0] = k1;
					ai2[1] = j + leafDistanceLimit;
					ai2[2] = l1;
					if (checkBlockLine(ai1, ai2) != -1) {
						continue;
					}
					ai3[1] = j - 2;
					if (checkBlockLine(ai3, ai1) == -1) {
						ai[k][0] = k1;
						ai[k][1] = j;
						ai[k][2] = l1;
						ai[k][3] = ai3[1];
						++k;
					}
				}

				--j;
				--i1;
			}
		}
		leafNodes = new int[k][4];
		System.arraycopy(ai, 0, leafNodes, 0, k);
	}

	void genTreeLayer(ChunkPrimer primer, int x, int y, int z, float f, byte axis0, IBlockState block) {
		int radius = (int) (f + 0.61799999999999999D);
		float f2 = f * f;
		byte axis1 = otherCoordPairs[axis0];
		byte axis2 = otherCoordPairs[axis0 + 3];
		int basePos[] = { x, y, z };
		int localPos[] = { 0, 0, 0 };
		
		localPos[axis0] = basePos[axis0];
		if (localPos[axis0] < 0 || localPos[axis0] >= coordMaximums[axis0])
			return;

		for (int axis1Offset = -radius; axis1Offset <= radius; ++axis1Offset) {
			localPos[axis1] = basePos[axis1] + axis1Offset;
			if (localPos[axis1] < 0 || localPos[axis1] >= coordMaximums[axis1])
				continue;
			for (int axis2Offset = -radius; axis2Offset <= radius; ++axis2Offset) {
				localPos[axis2] = basePos[axis2] + axis2Offset;
				if (localPos[axis2] < 0 || localPos[axis2] >= coordMaximums[axis2])
					continue;
				if (Math.pow(axis1Offset + 0.5D, 2D) + Math.pow(axis2Offset + 0.5D, 2D) < f2) {
					placeBlock(primer, localPos[0], localPos[1], localPos[2], block);
				}
			}
		}
	}

	float layerSize(int i) {
		if (i < heightLimit * 0.75D) {
			return -1.618F;
		}
		float f = heightLimit * 0.5F;
		float f1 = heightLimit * 0.5F - i;
		float f2;
		if (f1 == 0.0F) {
			f2 = f;
		} else if (Math.abs(f1) >= f) {
			f2 = 0.0F;
		} else {
			f2 = (float) sqrt(Math.pow(f, 2D) - Math.pow(f1, 2D));
		}
		f2 *= 0.5F;
		return f2;
	}

	// SOURCE: http://martin.ankerl.com/2009/01/05/approximation-of-sqrtx-in-java/
	public static double sqrt(final double a) {
		final long x = Double.doubleToLongBits(a) >> 32;
		double y = Double.longBitsToDouble((x + 1072632448) << 31);

		// repeat the following line for more precision
		// y = (y + a / y) * 0.5;
		return y;
	}

	float leafSize(int i) {
		if (i < 0 || i >= leafDistanceLimit) {
			return -1F;
		}
		return i != 0 && i != leafDistanceLimit - 1 ? 3F : 2.0F;
	}

	void generateLeafNode(ChunkPrimer primer, int i, int j, int k) {
		int l = j;
		for (int i1 = j + leafDistanceLimit; l < i1; l++) {
			float f = leafSize(l - j);
			if (f < 0)
				continue;
			genTreeLayer(primer, i, l, k, f, (byte) 1, Blocks.LEAVES.getDefaultState());
		}
	}

	void placeBlockLine(ChunkPrimer primer, int startPoint[], int endPoint[], IBlockState i) {
		int ai2[] = { 0, 0, 0 };
		byte byte0 = 0;
		int j = 0;
		for (; byte0 < 3; ++byte0) {
			ai2[byte0] = endPoint[byte0] - startPoint[byte0];
			if (Math.abs(ai2[byte0]) > Math.abs(ai2[j])) {
				j = byte0;
			}
		}

		if (ai2[j] == 0) {
			return;
		}
		byte byte1 = otherCoordPairs[j];
		byte byte2 = otherCoordPairs[j + 3];
		byte byte3;
		if (ai2[j] > 0) {
			byte3 = 1;
		} else {
			byte3 = -1;
		}
		double d = (double) ai2[byte1] / (double) ai2[j];
		double d1 = (double) ai2[byte2] / (double) ai2[j];
		int localPos[] = { 0, 0, 0 };
		int k = 0;
		for (int l = ai2[j] + byte3; k != l; k += byte3) {
			localPos[j] = MathHelper.floor((startPoint[j] + k) + 0.5D);
			localPos[byte1] = MathHelper.floor(startPoint[byte1] + k * d + 0.5D);
			localPos[byte2] = MathHelper.floor(startPoint[byte2] + k * d1 + 0.5D);
			if (localPos[0] < 0 || localPos[0] >= 16)
				continue;
			if (localPos[1] < 0 || localPos[1] >= 255)
				continue;
			if (localPos[2] < 0 || localPos[2] >= 16)
				continue;
			placeBlock(primer, localPos[0], localPos[1], localPos[2], i);
		}
	}

	void generateLeaves(ChunkPrimer primer) {
		for (int i = 0; i < leafNodes.length; ++i) {
			int k = leafNodes[i][0];
			int l = leafNodes[i][1];
			int i1 = leafNodes[i][2];
			generateLeafNode(primer, k, l, i1);
		}
	}

	boolean leafNodeNeedsBase(int i) {
		return i >= heightLimit * 0.20000000000000001D;
	}

	void generateTrunk(ChunkPrimer primer) {
		int i = trueBase[0];
		int j = trueBase[1];
		int k = basePos[1] + height;
		int l = trueBase[2];
		int ai[] = { i, j, l };
		int ai1[] = { i, k, l };
		placeBlockLine(primer, ai, ai1, Blocks.LOG.getDefaultState());
		if (trunkSize == 2) {
			ai[0]++;
			ai1[0]++;
			placeBlockLine(primer, ai, ai1, Blocks.LOG.getDefaultState());
			ai[2]++;
			ai1[2]++;
			placeBlockLine(primer, ai, ai1, Blocks.LOG.getDefaultState());
			ai[0]--;
			ai1[0]--;
			placeBlockLine(primer, ai, ai1, Blocks.LOG.getDefaultState());
		}
	}

	void generateRoots(ChunkPrimer primer) {
		int i = trueBase[0];
		int j = trueBase[1];
		int k = trueBase[2];
		int ai[] = { i, j + 1, k };
		int ai1[] = { i, j, k };
		int range = height >> 2;
		for (int c = 0; c < range; ++c) {
			ai[0] = i + c % 2;
			ai[2] = k + (c > 2 ? 1 : 0);
			ai1[0] = i + rand.nextInt(9) - 4;
			ai1[1] = j - rand.nextInt(range + 1) - 3;
			ai1[2] = k + rand.nextInt(9) - 4;
			placeBlockLine(primer, ai, ai1, Blocks.LOG.getDefaultState());
		}
	}

	void generateLeafNodeBases(ChunkPrimer primer) {
		int i = 0;
		int j = leafNodes.length;
		int ai[] = { basePos[0], basePos[1], basePos[2] };
		for (; i < j; i++) {
			int ai1[] = leafNodes[i];
			int ai2[] = { ai1[0], ai1[1], ai1[2] };
			ai[1] = ai1[3];
			int k = ai[1] - basePos[1];
			if (leafNodeNeedsBase(k)) {
				placeBlockLine(primer, ai, ai2, Blocks.LOG.getDefaultState());
			}
		}
	}

	int checkBlockLine(int ai[], int ai1[]) {
		return -1;
		// int ai2[] =
		// {
		// 0, 0, 0
		// };
		// byte byte0 = 0;
		// int i = 0;
		// for (; byte0 < 3; ++byte0) {
		// ai2[byte0] = ai1[byte0] - ai[byte0];
		// if (Math.abs(ai2[byte0]) > Math.abs(ai2[i])) {
		// i = byte0;
		// }
		// }
		//
		// if (ai2[i] == 0) {
		// return -1;
		// }
		// byte byte1 = otherCoordPairs[i];
		// byte byte2 = otherCoordPairs[i + 3];
		// byte byte3;
		// if (ai2[i] > 0) {
		// byte3 = 1;
		// } else {
		// byte3 = -1;
		// }
		// double d = (double)ai2[byte1] / (double)ai2[i];
		// double d1 = (double)ai2[byte2] / (double)ai2[i];
		// int ai3[] =
		// {
		// 0, 0, 0
		// };
		// int j = 0;
		// int k = ai2[i] + byte3;
		// do
		// {
		// if (j == k)
		// {
		// break;
		// }
		// ai3[i] = ai[i] + j;
		// ai3[byte1] = MathHelper.floor((double)ai[byte1] + (double)j * d);
		// ai3[byte2] = MathHelper.floor((double)ai[byte2] + (double)j * d1);
		// int l = getBlockId(ai3[0], ai3[1], ai3[2]);
		// if (l != 0 && l != 18) {
		// break;
		// }
		// j += byte3;
		// }
		// while (true);
		// if (j == k)
		// {
		// return -1;
		// }
		// else
		// {
		// return Math.abs(j);
		// }
	}

	boolean validTreeLocation(ChunkPrimer primer) {
		int ai[] = { basePos[0], basePos[1], basePos[2] };
		IBlockState block = primer.getBlockState(basePos[0], basePos[1] - 1, basePos[2]);
		while (block == Blocks.WATER || block == Blocks.FLOWING_WATER) {
			--basePos[1];
			block = primer.getBlockState(basePos[0], basePos[1] - 1, basePos[2]);
		}
		if (block.getBlock() != Blocks.DIRT && block.getBlock() != Blocks.GRASS) {
			return false;
		}
		int ai1[] = { basePos[0], (basePos[1] + heightLimit) - 1, basePos[2] };
		int j = checkBlockLine(ai, ai1);
		if (j == -1) {
			return true;
		}
		if (j < max_height / 2) {
			return false;
		}
		heightLimit = j;
		return true;
	}

	/**
	 * Recursively called by generate() (generate) and optionally by itself.
	 */
	@Override
	protected void recursiveGenerate(World worldObj, int x, int z, int chunkX, int chunkZ, ChunkPrimer primer) {
		int pX = x * 16 + rand.nextInt(16) - chunkX * 16;
		int pY = rand.nextInt(16);
		int pZ = z * 16 + rand.nextInt(16) - chunkZ * 16;

		trueBase[0] = basePos[0] = pX;
		basePos[1] = pY;
		trueBase[2] = basePos[2] = pZ;
		if (heightLimit == 0) {
			heightLimit = 20 + rand.nextInt(heightLimitLimit);
		}
//		if (!validTreeLocation(primer)) {
//			return;
//		}
		trueBase[1] = basePos[1];
		basePos[1] = Math.max(trueBase[1], 128 - heightLimit - rand.nextInt(16));
		generateLeafNodeList(primer);
		if (height < max_height / 2)
			return;
		generateLeaves(primer);
		generateTrunk(primer);
		generateRoots(primer);
		generateLeafNodeBases(primer);
	}
}
