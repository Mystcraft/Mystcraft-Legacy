package com.xcompwiz.mystcraft.world;

import java.util.HashMap;

import com.xcompwiz.mystcraft.world.biome.BiomeHelper;
import com.xcompwiz.mystcraft.world.biome.BiomeWrapperMyst;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;

public class BiomeWrapperManager {

	private WorldProviderMyst					provider;
	private HashMap<ResourceLocation, BiomeWrapperMyst>	biomes	= new HashMap<ResourceLocation, BiomeWrapperMyst>();

	public BiomeWrapperManager(WorldProviderMyst provider) {
		this.provider = provider;
	}

	public Biome getWrapper(int x, int z) {
		Biome biome = getBiomeForWorldCoords(x, z); // Biome Id at Chunk Coords
		return getBiomeWrapperForBiome(biome);
	}

	private synchronized BiomeWrapperMyst getBiomeWrapperForBiome(Biome biome) {
		ResourceLocation biomeID = biome.getRegistryName();
		BiomeWrapperMyst wrapper = biomes.get(biomeID);
		if (wrapper == null) {
			wrapper = new BiomeWrapperMyst(provider, biome);
			biomes.put(biomeID, wrapper);
		}
		return wrapper;
	}

	private Biome getBiomeForWorldCoords(int x, int z) {
		int lx = x & 15;
		int lz = z & 15;
		if (provider.world.blockExists(x, 0, z)) {
			Chunk chunk = provider.world.getChunkFromBlockCoords(x, z);
			return chunk.getBiomeGenForWorldCoords(lx, lz, provider.getBiomeProvider());
		}
		return this.provider.getBiomeProvider().getBiome(x, z);
	}

}
