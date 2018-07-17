package com.xcompwiz.mystcraft.world;

import java.util.HashMap;

import com.xcompwiz.mystcraft.world.biome.BiomeWrapperMyst;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;

public class BiomeWrapperManager {

	private WorldProviderMyst provider;
	private HashMap<ResourceLocation, BiomeWrapperMyst> biomes = new HashMap<ResourceLocation, BiomeWrapperMyst>();

	public BiomeWrapperManager(WorldProviderMyst provider) {
		this.provider = provider;
	}

	public Biome getWrapper(BlockPos pos) {
		Biome biome = getBiomeForWorldCoords(pos);
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

	private Biome getBiomeForWorldCoords(BlockPos pos) {
		if (provider.getWorld().isBlockLoaded(pos)) {
			Chunk chunk = provider.getWorld().getChunkFromBlockCoords(pos);
			return chunk.getBiome(pos, provider.getBiomeProvider());
		}
		return this.provider.getBiomeProvider().getBiome(pos);
	}

}
