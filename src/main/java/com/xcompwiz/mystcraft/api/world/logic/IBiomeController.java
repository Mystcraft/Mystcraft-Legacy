package com.xcompwiz.mystcraft.api.world.logic;

import java.util.List;

import net.minecraft.world.biome.BiomeGenBase;

import com.google.common.annotations.Beta;

@Beta
public interface IBiomeController {
	public abstract List<BiomeGenBase> getValidSpawnBiomes();

	/**
	 * In practice, this function is useless.  Minecraft has it and it is called, but the results are never used.
	 */
	public abstract float[] getRainfallField(float rainfall[], int x, int z, int xSize, int zSize);

	public abstract BiomeGenBase getBiomeAtCoords(int x, int z);

	public abstract BiomeGenBase[] getBiomesAtCoords(BiomeGenBase abiomegenbase[], int x, int z, int xSize, int zSize, boolean usecache);

	public abstract BiomeGenBase[] getBiomesForGeneration(BiomeGenBase[] abiomegenbase, int x, int z, int xSize, int zSize);

	public abstract void cleanupCache();
}
