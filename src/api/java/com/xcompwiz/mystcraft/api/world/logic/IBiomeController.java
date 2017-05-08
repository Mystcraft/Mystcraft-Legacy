package com.xcompwiz.mystcraft.api.world.logic;

import java.util.List;

import com.google.common.annotations.Beta;

import net.minecraft.world.biome.Biome;

@Beta
public interface IBiomeController {
	public abstract List<Biome> getValidSpawnBiomes();

	/**
	 * In practice, this function is useless.  Minecraft has it and it is called, but the results are never used.
	 */
	public abstract float[] getRainfallField(float rainfall[], int x, int z, int xSize, int zSize);

	public abstract Biome getBiomeAtCoords(int x, int z);

	public abstract Biome[] getBiomesAtCoords(Biome aBiome[], int x, int z, int xSize, int zSize, boolean usecache);

	public abstract Biome[] getBiomesForGeneration(Biome[] aBiome, int x, int z, int xSize, int zSize);

	public abstract void cleanupCache();
}
