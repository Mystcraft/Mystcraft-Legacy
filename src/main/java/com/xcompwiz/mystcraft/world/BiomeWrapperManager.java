package com.xcompwiz.mystcraft.world;

import java.util.HashMap;

import com.xcompwiz.mystcraft.world.biome.BiomeWrapperMyst;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;

public class BiomeWrapperManager {

	private WorldProviderMyst					provider;
	private HashMap<Integer, BiomeWrapperMyst>	biomes	= new HashMap<Integer, BiomeWrapperMyst>();

	public BiomeWrapperManager(WorldProviderMyst provider) {
		this.provider = provider;
	}

	public Biome getWrapper(int x, int z) {
		Biome biome = getBiomeForWorldCoords(x, z); // Biome Id at Chunk Coords
		return getBiomeWrapperForBiome(biome);
	}

	private synchronized BiomeWrapperMyst getBiomeWrapperForBiome(Biome biome) {
		BiomeWrapperMyst wrapper = biomes.get(biome.biomeID);
		if (wrapper == null) {
			wrapper = new BiomeWrapperMyst(provider, biome);
			biomes.put(biome.biomeID, wrapper);
		}
		return wrapper;
	}

	private Biome getBiomeForWorldCoords(int x, int z) {
		int lx = x & 15;
		int lz = z & 15;
		if (provider.worldObj.blockExists(x, 0, z)) {
			Chunk chunk = provider.worldObj.getChunkFromBlockCoords(x, z);
			return chunk.getBiomeGenForWorldCoords(lx, lz, provider.worldChunkMgr);
		}
		return this.provider.worldChunkMgr.getBiomeGenAt(x, z);
	}

}
