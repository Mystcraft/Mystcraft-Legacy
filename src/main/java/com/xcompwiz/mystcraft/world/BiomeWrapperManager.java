package com.xcompwiz.mystcraft.world;

import java.util.HashMap;

import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;

import com.xcompwiz.mystcraft.world.biome.BiomeWrapperMyst;

public class BiomeWrapperManager {

	private WorldProviderMyst					provider;
	private HashMap<Integer, BiomeWrapperMyst>	biomes	= new HashMap<Integer, BiomeWrapperMyst>();

	public BiomeWrapperManager(WorldProviderMyst provider) {
		this.provider = provider;
	}

	public BiomeGenBase getWrapper(int x, int z) {
		BiomeGenBase biome = getBiomeForWorldCoords(x, z); // Biome Id at Chunk Coords
		return getBiomeWrapperForBiome(biome);
	}

	private synchronized BiomeWrapperMyst getBiomeWrapperForBiome(BiomeGenBase biome) {
		BiomeWrapperMyst wrapper = biomes.get(biome.biomeID);
		if (wrapper == null) {
			wrapper = new BiomeWrapperMyst(provider, biome);
			biomes.put(biome.biomeID, wrapper);
		}
		return wrapper;
	}

	private BiomeGenBase getBiomeForWorldCoords(int x, int z) {
		int lx = x & 15;
		int lz = z & 15;
		if (provider.worldObj.blockExists(x, 0, z)) {
			Chunk chunk = provider.worldObj.getChunkFromBlockCoords(x, z);
			return chunk.getBiomeGenForWorldCoords(lx, lz, provider.worldChunkMgr);
		}
		return this.provider.worldChunkMgr.getBiomeGenAt(x, z);
	}

}
