package com.xcompwiz.mystcraft.world;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

public class ArrayMappingUtils {

	//On local indexing, we are incrementing x, then z, then y
	public static final void mapLocalToVanilla(Block[] arr1, Block[] arr2) {
		int len = arr1.length;
		if (len != arr2.length) throw new RuntimeException("Cannot map data indicies: Arrays of different lengths");
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
	public static final void mapVanillaToLocal(Block[] arr1, Block[] arr2) {
		int len = arr1.length;
		if (len != arr2.length) throw new RuntimeException("Cannot map data indicies: Arrays of different lengths");
		int maxy = len / 256;
		for (int z = 0; z < 16; ++z) {
			for (int x = 0; x < 16; ++x) {
				for (int y = 0; y < maxy; ++y) {
					int lcoords = y << 8 | z << 4 | x;
					int vcoords = ((x << 4 | z) * maxy) | y;
					if (y < 6 && Blocks.bedrock == arr1[vcoords]) continue; //Filter out biome added bedrock layers
					arr2[lcoords] = arr1[vcoords];
				}
			}
		}
	}

	//On local indexing, we are incrementing x, then z, then y
	public static final void mapLocalToVanilla(byte[] arr1, byte[] arr2) {
		int len = arr1.length;
		if (len != arr2.length) throw new RuntimeException("Cannot map data indicies: Arrays of different lengths");
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
	public static final void mapVanillaToLocal(byte[] arr1, byte[] arr2) {
		int len = arr1.length;
		if (len != arr2.length) throw new RuntimeException("Cannot map data indicies: Arrays of different lengths");
		int maxy = len / 256;
		for (int z = 0; z < 16; ++z) {
			for (int x = 0; x < 16; ++x) {
				for (int y = 0; y < maxy; ++y) {
					int lcoords = y << 8 | z << 4 | x;
					int vcoords = ((x << 4 | z) * maxy) | y;
					arr2[lcoords] = arr1[vcoords];
				}
			}
		}
	}

}
