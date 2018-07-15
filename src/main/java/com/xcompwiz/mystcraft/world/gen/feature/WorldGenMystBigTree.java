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
	int blobPos[] = { 0, 0, 0 };
	int rootPos[] = { 0, 0, 0 };
	int blobHeight;
	int leafNodes[][];

	private static double scaleWidth = 0.2D;
	private static int maxY = 180;
	private static int minY = 128;
	private static int leafDistanceLimit = 3;
	private static int trunkSize = 2;

	public WorldGenMystBigTree(long seed) {
		super(seed, Blocks.LOG.getDefaultState());

		blobHeight = 0;
	}

	void generateLeafNodeList(ChunkPrimer primer) {
		int i = 4;
		if (i < 1) {
			i = 1;
		}
		int ai[][] = new int[i * blobHeight][4];
		int j = (blobPos[1] + blobHeight) - leafDistanceLimit;
		int k = 1;
		int l = (int) (blobPos[1] + blobHeight * 0.9D);
		int i1 = j - blobPos[1];
		ai[0][0] = blobPos[0];
		ai[0][1] = j;
		ai[0][2] = blobPos[2];
		ai[0][3] = l;
		--j;
		int ai1[] = { 0, 0, 0 };
		int ai2[] = { 0, 0, 0 };
		int ai3[] = { blobPos[0], 0, blobPos[2] };
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
					int k1 = MathHelper.floor(d1 * Math.sin(d2) + blobPos[0] + d);
					int l1 = MathHelper.floor(d1 * Math.cos(d2) + blobPos[2] + d);
					ai1[0] = k1;
					ai1[1] = j;
					ai1[2] = l1;
					ai2[0] = k1;
					ai2[1] = j + leafDistanceLimit;
					ai2[2] = l1;
					ai3[1] = j - 2;
					ai[k][0] = k1;
					ai[k][1] = j;
					ai[k][2] = l1;
					ai[k][3] = ai3[1];
					++k;
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
		if (i < blobHeight * 0.75D) {
			return -1.618F;
		}
		float f = blobHeight * 0.5F;
		float f1 = blobHeight * 0.5F - i;
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
		return i >= blobHeight * 0.20000000000000001D;
	}

	void generateTrunk(ChunkPrimer primer) {
		int i = rootPos[0];
		int j = rootPos[1];
		int k = (int) (blobPos[1] + blobHeight * 0.9D);
		int l = rootPos[2];
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
		int i = rootPos[0];
		int j = rootPos[1];
		int k = rootPos[2];
		int ai[] = { i, j + 1, k };
		int ai1[] = { i, j, k };
		int range = rootPos[1];
		int count = blobHeight >> 2;
		for (int c = 0; c < count; ++c) {
			ai[0] = i + c % 2;
			ai[2] = k + (c > 2 ? 1 : 0);
			ai1[0] = i + rand.nextInt(13) - 6;
			ai1[1] = j - rand.nextInt(range + 1) - 3;
			ai1[2] = k + rand.nextInt(13) - 6;
			placeBlockLine(primer, ai, ai1, Blocks.LOG.getDefaultState());
		}
	}

	void generateLeafNodeBases(ChunkPrimer primer) {
		int i = 0;
		int j = leafNodes.length;
		int ai[] = { blobPos[0], blobPos[1], blobPos[2] };
		for (; i < j; i++) {
			int ai1[] = leafNodes[i];
			int ai2[] = { ai1[0], ai1[1], ai1[2] };
			ai[1] = ai1[3];
			int k = ai[1] - blobPos[1];
			if (leafNodeNeedsBase(k)) {
				placeBlockLine(primer, ai, ai2, Blocks.LOG.getDefaultState());
			}
		}
	}

	/**
	 * Recursively called by generate() (generate) and optionally by itself.
	 */
	@Override
	protected void recursiveGenerate(World worldObj, int x, int z, int chunkX, int chunkZ, ChunkPrimer primer) {
		if (rand.nextInt(2) != 0)
			return;
		int pX = x * 16 + rand.nextInt(16) - chunkX * 16;
		int pY = rand.nextInt(8) + 4;
		int pZ = z * 16 + rand.nextInt(16) - chunkZ * 16;

		rootPos[0] = blobPos[0] = pX;
		rootPos[1] = blobPos[1] = pY;
		rootPos[2] = blobPos[2] = pZ;

		blobPos[1] = rand.nextInt(maxY - minY + 1) + minY;
		blobHeight = rand.nextInt(30) + 40;
		generateLeafNodeList(primer);
		generateLeaves(primer);
		generateTrunk(primer);
		generateRoots(primer);
		generateLeafNodeBases(primer);
	}
}
