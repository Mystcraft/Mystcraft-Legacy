package com.xcompwiz.mystcraft.symbol.symbols;

import java.util.List;

import com.xcompwiz.mystcraft.api.world.AgeDirector;
import com.xcompwiz.mystcraft.api.world.logic.IBiomeController;
import com.xcompwiz.mystcraft.symbol.SymbolBase;

import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;

public class SymbolBiomeControllerNative extends SymbolBase {

	public SymbolBiomeControllerNative(String identifier) {
		super(identifier);
	}

	@Override
	public void registerLogic(AgeDirector controller, long seed) {
		controller.registerInterface(new BiomeController(controller));
	}

	private class BiomeController implements IBiomeController {
		private WorldChunkManager	manager;

		protected BiomeController(AgeDirector controller) {
			this.manager = new WorldChunkManager(controller.getSeed(), WorldType.DEFAULT);
		}

		/**
		 * Gets the list of valid biomes for the player to spawn in.
		 */
		@Override
		public List<BiomeGenBase> getValidSpawnBiomes() {
			return manager.getBiomesToSpawnIn();
		}

		/**
		 * Returns the BiomeGenBase related to the x, z position on the world.
		 */
		@Override
		public BiomeGenBase getBiomeAtCoords(int par1, int par2) {
			return manager.getBiomeGenAt(par1, par2);
		}

		/**
		 * Returns a list of rainfall values for the specified blocks. Args: listToReuse, x, z, width, length.
		 */
		@Override
		public float[] getRainfallField(float afloat[], int x, int z, int width, int length) {
			return manager.getRainfall(afloat, x, z, width, length);
		}

		/**
		 * Returns an array of biomes for the location input.
		 */
		@Override
		public BiomeGenBase[] getBiomesForGeneration(BiomeGenBase par1ArrayOfBiomeGenBase[], int par2, int par3, int par4, int par5) {
			return manager.getBiomesForGeneration(par1ArrayOfBiomeGenBase, par2, par3, par4, par5);
		}

		/**
		 * Return a list of biomes for the specified blocks. Args: listToReuse, x, y, width, length, cacheFlag (if false, don't check biomeCache to avoid
		 * infinite loop in BiomeCacheBlock)
		 */
		@Override
		public BiomeGenBase[] getBiomesAtCoords(BiomeGenBase par1ArrayOfBiomeGenBase[], int par2, int par3, int par4, int par5, boolean par6) {
			return manager.getBiomeGenAt(par1ArrayOfBiomeGenBase, par2, par3, par4, par5, par6);
		}

		/**
		 * Calls the WorldChunkManager's biomeCache.cleanupCache()
		 */
		@Override
		public void cleanupCache() {
			manager.cleanupCache();
		}
	}
}
