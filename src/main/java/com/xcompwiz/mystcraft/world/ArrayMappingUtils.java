package com.xcompwiz.mystcraft.world;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.ChunkPrimer;

public class ArrayMappingUtils {

	//private void mapBlocksToChunk(Chunk chunk, IBlockState[] blocks) {
	//	ExtendedBlockStorage[] storageArrays = new ExtendedBlockStorage[16];
	//	int layers = blocks.length / 256;
	//	boolean flag = !worldObj.provider.hasNoSky();
	//	for (int y = 0; y < layers; ++y) {
	//		int storagei = y >> 4;
	//		for (int z = 0; z < 16; ++z) {
	//			for (int x = 0; x < 16; ++x) {
	//				int coords = y << 8 | z << 4 | x;
	//				IBlockState block = blocks[coords];
	//				if (block != null && !block.getBlock().equals(Blocks.AIR)) {
	//					if (storageArrays[storagei] == null) {
	//						storageArrays[storagei] = new ExtendedBlockStorage(storagei << 4, flag);
	//					}
	//					storageArrays[storagei].set(x, y & 15, z, block);
	//				}
	//			}
	//		}
	//	}
	//	chunk.setStorageArrays(storageArrays);
	//}

	//On local indexing, we are incrementing x, then z, then y
	public static void mapLocalToVanilla(IBlockState[] arr1, IBlockState[] arr2) {
		int len = arr1.length;
		if (len != arr2.length)
			throw new RuntimeException("Cannot map data indicies: Arrays of different lengths");
		int maxy = len / 256;
		for (int y = 0; y < maxy; ++y) {
			for (int z = 0; z < 16; ++z) {
				for (int x = 0; x < 16; ++x) {
					int lcoords = y << 8 | z << 4 | x;
					int vcoords = ((x << 4 | z) * maxy) | y;
					arr2[vcoords] = arr1[lcoords];
				}
			}
		}
	}

	//On vanilla indexing, we increment y, then z, then x
	public static void mapVanillaToLocal(IBlockState[] arr1, IBlockState[] arr2) {
		int len = arr1.length;
		if (len != arr2.length)
			throw new RuntimeException("Cannot map data indicies: Arrays of different lengths");
		int maxy = len / 256;
		for (int z = 0; z < 16; ++z) {
			for (int x = 0; x < 16; ++x) {
				for (int y = 0; y < maxy; ++y) {
					int lcoords = y << 8 | z << 4 | x;
					int vcoords = ((x << 4 | z) * maxy) | y;
					if (y < 6 && Blocks.BEDROCK == arr1[vcoords])
						continue; //Filter out biome added bedrock layers
					arr2[lcoords] = arr1[vcoords];
				}
			}
		}
	}

	public static void fillPrimerVanillaIndexing(ChunkPrimer cp, IBlockState[] blocks) {
		for (int i = 0; i < blocks.length; i++) {
			IBlockState state = blocks[i];
			if (state == null)
				continue;
			BlockPos vanillaIndex = getVanillaPos(i);
			cp.setBlockState(vanillaIndex.getX(), vanillaIndex.getY(), vanillaIndex.getZ(), state);
		}
	}

	private static BlockPos getVanillaPos(int index) {
		int x = (index >> 12) & 15;
		int z = (index >> 8) & 15;
		int y = index & 255;
		return new BlockPos(x, y, z);
	}
}
