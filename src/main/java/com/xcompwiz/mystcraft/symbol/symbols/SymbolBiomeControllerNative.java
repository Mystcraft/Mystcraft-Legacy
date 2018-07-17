package com.xcompwiz.mystcraft.symbol.symbols;

import java.util.List;

import com.xcompwiz.mystcraft.api.world.AgeDirector;
import com.xcompwiz.mystcraft.api.world.logic.IBiomeController;
import com.xcompwiz.mystcraft.symbol.SymbolBase;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.storage.WorldInfo;

public class SymbolBiomeControllerNative extends SymbolBase {

	public SymbolBiomeControllerNative(ResourceLocation identifier) {
		super(identifier);
	}

	@Override
	public void registerLogic(AgeDirector controller, long seed) {
		controller.registerInterface(new BiomeController(controller));
	}

	@Override
	public boolean generatesConfigOption() {
		return true;
	}

	private class BiomeController implements IBiomeController {

		private BiomeProvider manager;

		protected BiomeController(AgeDirector controller) {
			//Seems super roundabout, but most parameters are unnecessary and only there to forward the data to the biomeprovider
			this.manager = new BiomeProvider(new WorldInfo(new WorldSettings(controller.getSeed(), GameType.SURVIVAL, false, false, WorldType.DEFAULT), ""));
		}

		/**
		 * Gets the list of valid biomes for the player to spawn in.
		 */
		@Override
		public List<Biome> getValidSpawnBiomes() {
			return manager.getBiomesToSpawnIn();
		}

		/**
		 * Returns the Biome related to the x, z position on the world.
		 */
		@Override
		public Biome getBiomeAtCoords(int par1, int par2) {
			return manager.getBiome(new BlockPos(par1, 0, par2));
		}

		/**
		 * Returns an array of biomes for the location input.
		 */
		@Override
		public Biome[] getBiomesForGeneration(Biome reuseableArray[], int blockPosX, int blockPosZ, int xSize, int zSize) {
			return manager.getBiomesForGeneration(reuseableArray, blockPosX, blockPosZ, xSize, zSize);
		}

		/**
		 * Return a list of biomes for the specified blocks. Args: listToReuse, x, y, width, length, cacheFlag (if false, don't check biomeCache to avoid
		 * infinite loop in BiomeCacheBlock)
		 */
		@Override
		public Biome[] getBiomesAtCoords(Biome[] reuseableArray, int x, int y, int width, int length, boolean cacheFlag) {
			return manager.getBiomes(reuseableArray, x, y, width, length, cacheFlag);
		}

		/**
		 * Calls the BiomeProvider's biomeCache.cleanupCache()
		 */
		@Override
		public void cleanupCache() {
			manager.cleanupCache();
		}
	}
}
