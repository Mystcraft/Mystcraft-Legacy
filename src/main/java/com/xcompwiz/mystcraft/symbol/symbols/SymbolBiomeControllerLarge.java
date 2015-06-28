package com.xcompwiz.mystcraft.symbol.symbols;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeCache;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.GenLayerFuzzyZoom;
import net.minecraft.world.gen.layer.GenLayerIsland;
import net.minecraft.world.gen.layer.GenLayerSmooth;
import net.minecraft.world.gen.layer.GenLayerVoronoiZoom;
import net.minecraft.world.gen.layer.IntCache;

import com.xcompwiz.mystcraft.api.symbol.ModifierUtils;
import com.xcompwiz.mystcraft.api.world.AgeDirector;
import com.xcompwiz.mystcraft.api.world.logic.IBiomeController;
import com.xcompwiz.mystcraft.symbol.SymbolBase;
import com.xcompwiz.mystcraft.symbol.modifiers.SymbolBiome;
import com.xcompwiz.mystcraft.world.gen.layer.GenLayerBiomeMyst;
import com.xcompwiz.mystcraft.world.gen.layer.GenLayerZoomMyst;

public class SymbolBiomeControllerLarge extends SymbolBase {

	public SymbolBiomeControllerLarge(String identifier) {
		super(identifier);
	}

	@Override
	public void registerLogic(AgeDirector controller, long seed) {
		List<BiomeGenBase> biomes = new ArrayList<BiomeGenBase>();
		BiomeGenBase biome;
		biome = ModifierUtils.popBiome(controller);
		while (biome != null) {
			biomes.add(biome);
			biome = ModifierUtils.popBiome(controller);
		}

		controller.registerInterface(new BiomeController(controller, 3, biomes));
	}

	static class BiomeController implements IBiomeController {
		private BiomeGenBase		allowedBiomes[];
		/** A list of biomes that the player can spawn in. */
		private List<BiomeGenBase>	biomesToSpawnIn;

		private GenLayer			genBiomes;
		/** A GenLayer containing the indices into BiomeGenBase.biomeList[] */
		private GenLayer			biomeIndexLayer;
		/** The BiomeCache object for this world. */
		private BiomeCache			biomeCache;

		private int					zoomscale;

		protected BiomeController(AgeDirector controller, int zoom, List<BiomeGenBase> biomes) {
			this.zoomscale = zoom;
			biomeCache = new BiomeCache(controller.getWorldChunkManager());
			biomesToSpawnIn = new ArrayList<BiomeGenBase>();
			biomesToSpawnIn.add(BiomeGenBase.forest);
			biomesToSpawnIn.add(BiomeGenBase.plains);
			biomesToSpawnIn.add(BiomeGenBase.taiga);
			biomesToSpawnIn.add(BiomeGenBase.taigaHills);
			biomesToSpawnIn.add(BiomeGenBase.forestHills);
			biomesToSpawnIn.add(BiomeGenBase.jungle);
			biomesToSpawnIn.add(BiomeGenBase.jungleHills);

			Random rand = new Random(controller.getSeed());
			while (biomes.size() < 3) {
				biomes.add(SymbolBiome.getRandomBiome(rand));
			}

			allowedBiomes = new BiomeGenBase[biomes.size()];
			allowedBiomes = biomes.toArray(allowedBiomes);
			GenLayer agenlayer[] = computeGenLayers(controller.getSeed(), WorldType.DEFAULT);
			genBiomes = agenlayer[0];
			biomeIndexLayer = agenlayer[1];
		}

		/**
		 * Gets the list of valid biomes for the player to spawn in.
		 */
		@Override
		public List<BiomeGenBase> getValidSpawnBiomes() {
			return biomesToSpawnIn;
		}

		/**
		 * Returns the BiomeGenBase related to the x, z position on the world.
		 */
		@Override
		public BiomeGenBase getBiomeAtCoords(int par1, int par2) {
			return biomeCache.getBiomeGenAt(par1, par2);
		}

		/**
		 * Returns a list of rainfall values for the specified blocks. Args: listToReuse, x, z, width, length.
		 */
		@Override
		public float[] getRainfallField(float par1ArrayOfFloat[], int par2, int par3, int par4, int par5) {
			IntCache.resetIntCache();

			if (par1ArrayOfFloat == null || par1ArrayOfFloat.length < par4 * par5) {
				par1ArrayOfFloat = new float[par4 * par5];
			}

			int ai[] = biomeIndexLayer.getInts(par2, par3, par4, par5);

			for (int i = 0; i < par4 * par5; i++) {
				float f = BiomeGenBase.getBiome(ai[i]).getIntRainfall() / 65536F;

				if (f > 1.0F) {
					f = 1.0F;
				}

				par1ArrayOfFloat[i] = f;
			}

			return par1ArrayOfFloat;
		}

		/**
		 * Returns an array of biomes for the location input.
		 */
		@Override
		public BiomeGenBase[] getBiomesForGeneration(BiomeGenBase par1ArrayOfBiomeGenBase[], int par2, int par3, int par4, int par5) {
			IntCache.resetIntCache();

			if (par1ArrayOfBiomeGenBase == null || par1ArrayOfBiomeGenBase.length < par4 * par5) {
				par1ArrayOfBiomeGenBase = new BiomeGenBase[par4 * par5];
			}

			int ai[] = genBiomes.getInts(par2, par3, par4, par5);

			for (int i = 0; i < par4 * par5; i++) {
				par1ArrayOfBiomeGenBase[i] = BiomeGenBase.getBiome(ai[i]);
			}

			return par1ArrayOfBiomeGenBase;
		}

		/**
		 * Return a list of biomes for the specified blocks. Args: listToReuse, x, y, width, length, cacheFlag (if false, don't check biomeCache to avoid
		 * infinite loop in BiomeCacheBlock)
		 */
		@Override
		public BiomeGenBase[] getBiomesAtCoords(BiomeGenBase par1ArrayOfBiomeGenBase[], int par2, int par3, int par4, int par5, boolean par6) {
			IntCache.resetIntCache();

			if (par1ArrayOfBiomeGenBase == null || par1ArrayOfBiomeGenBase.length < par4 * par5) {
				par1ArrayOfBiomeGenBase = new BiomeGenBase[par4 * par5];
			}

			if (par6 && par4 == 16 && par5 == 16 && (par2 & 0xf) == 0 && (par3 & 0xf) == 0) {
				BiomeGenBase abiomegenbase[] = biomeCache.getCachedBiomes(par2, par3);
				System.arraycopy(abiomegenbase, 0, par1ArrayOfBiomeGenBase, 0, par4 * par5);
				return par1ArrayOfBiomeGenBase;
			}

			int ai[] = biomeIndexLayer.getInts(par2, par3, par4, par5);

			for (int i = 0; i < par4 * par5; i++) {
				par1ArrayOfBiomeGenBase[i] = BiomeGenBase.getBiome(ai[i]);
			}

			return par1ArrayOfBiomeGenBase;
		}

		/**
		 * Calls the WorldChunkManager's biomeCache.cleanupCache()
		 */
		@Override
		public void cleanupCache() {
			biomeCache.cleanupCache();
		}

		private GenLayer[] computeGenLayers(long par0, WorldType par2WorldType) {
			GenLayer obj = new GenLayerIsland(1L);
			obj = new GenLayerFuzzyZoom(2000L, (obj));
			GenLayer obj1 = obj;
			obj1 = GenLayerZoomMyst.func_35515_a(1000L, (obj1), 0);
			// obj1 = new GenLayerRiverInit(100L, ((GenLayer)(obj1)));
			obj1 = GenLayerZoomMyst.func_35515_a(1000L, (obj1), zoomscale + 1);
			// obj1 = new GenLayerRiver(1L, ((GenLayer)(obj1)));
			obj1 = new GenLayerSmooth(1000L, (obj1));
			GenLayer obj2 = obj;
			obj2 = GenLayerZoomMyst.func_35515_a(1000L, (obj2), 0);
			obj2 = new GenLayerBiomeMyst(200L, (obj2), par2WorldType, allowedBiomes);
			obj2 = GenLayerZoomMyst.func_35515_a(1000L, (obj2), 2);

			for (int i = 0; i < zoomscale; i++) {
				obj2 = new GenLayerZoomMyst(1000 + i, (obj2));
			}

			obj2 = new GenLayerSmooth(1000L, (obj2));
			// obj2 = new GenLayerRiverMix(100L, ((GenLayer)(obj2)), ((GenLayer)(obj1)));
			GenLayerVoronoiZoom genlayervoronoizoom = new GenLayerVoronoiZoom(10L, (obj2));
			(obj2).initWorldGenSeed(par0);
			genlayervoronoizoom.initWorldGenSeed(par0);
			return (new GenLayer[] { obj2, genlayervoronoizoom });
		}
	}
}
