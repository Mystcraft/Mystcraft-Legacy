package com.xcompwiz.mystcraft.world;

import java.util.List;
import java.util.Random;

import net.minecraft.world.ChunkPosition;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.gen.layer.IntCache;

public class WorldChunkManagerMyst extends WorldChunkManager {

	AgeController	controller;

	public WorldChunkManagerMyst(AgeController c) {
		controller = c;
	}

	@Override
	public List<BiomeGenBase> getBiomesToSpawnIn() {
		List<BiomeGenBase> list = controller.getBiomeController().getValidSpawnBiomes();
		return list;
	}

	@Override
	public BiomeGenBase getBiomeGenAt(int x, int z) {
		BiomeGenBase biome = controller.getBiomeController().getBiomeAtCoords(x, z);
		controller.modifyBiomeAt(biome, x, z);
		return biome;
	}

	@Override
	public BiomeGenBase[] getBiomesForGeneration(BiomeGenBase abiomegenbase[], int x, int z, int xSize, int zSize) {
		IntCache.resetIntCache();
		if (abiomegenbase == null || abiomegenbase.length < xSize * zSize) {
			abiomegenbase = new BiomeGenBase[xSize * zSize];
		}
		abiomegenbase = controller.getBiomeController().getBiomesForGeneration(abiomegenbase, x, z, xSize, zSize);
		controller.modifyGenerationBiomesAt(abiomegenbase, x, z, xSize, zSize);
		return abiomegenbase;
	}

	@Override
	public BiomeGenBase[] loadBlockGeneratorData(BiomeGenBase abiomegenbase[], int i, int j, int k, int l) {
		return getBiomeGenAt(abiomegenbase, i, j, k, l, true);
	}

	@Override
	public BiomeGenBase[] getBiomeGenAt(BiomeGenBase abiomegenbase[], int x, int z, int xSize, int zSize, boolean usecache) {
		IntCache.resetIntCache();
		if (abiomegenbase == null || abiomegenbase.length < xSize * zSize) {
			abiomegenbase = new BiomeGenBase[xSize * zSize];
		}
		abiomegenbase = controller.getBiomeController().getBiomesAtCoords(abiomegenbase, x, z, xSize, zSize, usecache);
		controller.modifyBiomesAt(abiomegenbase, x, z, xSize, zSize, usecache);
		return abiomegenbase;
	}

	@Override
	public float getTemperatureAtHeight(float temp, int y) {
		return controller.getTemperatureAtHeight(temp, y);
	}

	@Override
	public boolean areBiomesViable(int i, int j, int k, List list) {
		int l = i - k >> 2;
		int i1 = j - k >> 2;
		int j1 = i + k >> 2;
		int k1 = j + k >> 2;
		int l1 = (j1 - l) + 1;
		int i2 = (k1 - i1) + 1;
		BiomeGenBase biomes[] = getBiomesForGeneration(null, l, i1, l1, i2);
		for (int j2 = 0; j2 < l1 * i2; j2++) {
			if (!list.contains(biomes[j2])) { return false; }
		}

		return true;
	}

	@Override
	public ChunkPosition findBiomePosition(int i, int j, int k, List list, Random random) {
		int l = i - k >> 2;
		int i1 = j - k >> 2;
		int j1 = i + k >> 2;
		int k1 = j + k >> 2;
		int l1 = (j1 - l) + 1;
		int i2 = (k1 - i1) + 1;
		BiomeGenBase biomes[] = getBiomesForGeneration(null, l, i1, l1, i2);
		ChunkPosition chunkposition = null;
		int j2 = 0;
		for (int k2 = 0; k2 < 16 * 16; k2++) {
			int l2 = l + k2 % l1 << 2;
			int i3 = i1 + k2 / l1 << 2;
			if (list.contains(biomes[k2]) && (chunkposition == null || random.nextInt(j2 + 1) == 0)) {
				chunkposition = new ChunkPosition(l2, 0, i3);
				j2++;
			}
		}

		return chunkposition;
	}

	/**
	 * I'm not really sure why this function exists. The only use for it is to build an array in the BiomeCache that nothing ever reads from.
	 */
	@Override
	public float[] getRainfall(float af[], int i, int j, int k, int l) {
		IntCache.resetIntCache();
		if (af == null || af.length < k * l) {
			af = new float[k * l];
		}
		return controller.getBiomeController().getRainfallField(af, i, j, k, l);
	}

	@Override
	public void cleanupCache() {
		controller.getBiomeController().cleanupCache();
	}
}
