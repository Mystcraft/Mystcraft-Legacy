package com.xcompwiz.mystcraft.api.world.logic;

import java.util.List;

import net.minecraft.world.biome.BiomeGenBase;

import com.google.common.annotations.Beta;

@Beta
public interface IBiomeController {
	public abstract List<BiomeGenBase> getValidSpawnBiomes();

	public abstract float[] getRainfallField(float af[], int x, int z, int x1, int z1);

	public abstract BiomeGenBase getBiomeAtCoords(int i, int j);

	public abstract BiomeGenBase[] getBiomesAtCoords(BiomeGenBase abiomegenbase[], int i, int j, int k, int l, boolean flag);

	public abstract BiomeGenBase[] getBiomesFromGenerationField(BiomeGenBase[] abiomegenbase, int i, int j, int k, int l);

	public abstract void cleanupCache();
}
