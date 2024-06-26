package com.xcompwiz.mystcraft.symbol.symbols;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.xcompwiz.mystcraft.api.symbol.ModifierUtils;
import com.xcompwiz.mystcraft.api.world.AgeDirector;
import com.xcompwiz.mystcraft.api.world.logic.IBiomeController;
import com.xcompwiz.mystcraft.symbol.SymbolBase;
import com.xcompwiz.mystcraft.symbol.modifiers.SymbolBiome;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;

public class SymbolBiomeControllerTiled extends SymbolBase {

	public SymbolBiomeControllerTiled(ResourceLocation identifier) {
		super(identifier);
	}

	@Override
	public void registerLogic(AgeDirector controller, long seed) {
		List<Biome> biomes = new ArrayList<Biome>();
		Biome biome;
		biome = ModifierUtils.popBiome(controller);
		while (biome != null) {
			biomes.add(biome);
			biome = ModifierUtils.popBiome(controller);
		}

		Random rand = new Random(controller.getSeed());
		while (biomes.size() < 2) {
			biomes.add(SymbolBiome.getRandomBiome(rand));
		}
		controller.registerInterface(new BiomeController(biomes));
	}

	@Override
	public boolean generatesConfigOption() {
		return true;
	}

	private class BiomeController implements IBiomeController {
		private List<Biome> biomes;

		public BiomeController(List<Biome> biomes) {
			this.biomes = biomes;
		}

		@Override
		public List<Biome> getValidSpawnBiomes() {
			return biomes;
		}

		@Override
		public Biome getBiomeAtCoords(int i, int j) {
			int index = ((i >> 4) + (j >> 4));
			index = index % biomes.size();
			if (index < 0) {
				index += biomes.size();
			}
			return biomes.get(index % biomes.size());
		}

		@Override
		public Biome[] getBiomesForGeneration(Biome[] aBiome, int x, int z, int xSize, int zSize) {
			if (aBiome == null || aBiome.length < xSize * zSize) {
				aBiome = new Biome[xSize * zSize];
			}

			for (int i = 0; i < xSize * zSize; ++i) {
				aBiome[i] = getBiomeAtCoords(x + i % xSize, z + i / xSize);
			}

			return aBiome;
		}

		@Override
		public Biome[] getBiomesAtCoords(Biome[] aBiome, int x, int z, int xSize, int zSize, boolean flag) {
			if (aBiome == null || aBiome.length < xSize * zSize) {
				aBiome = new Biome[xSize * zSize];
			}
			if (flag && xSize == 16 && zSize == 16 && (x & 0xf) == 0 && (z & 0xf) == 0) {
				return createBiomeArray(aBiome, x, z, xSize, zSize);
			}
			for (int i = 0; i < xSize * zSize; i++) {
				aBiome[i] = getBiomeAtCoords(x + i % xSize, z + i / xSize);
			}

			return aBiome;
		}

		private Biome[] createBiomeArray(Biome aBiome[], int i, int j, int k, int l) {
			if (aBiome == null || aBiome.length < k * l) {
				aBiome = new Biome[k * l];
			}
			for (int i1 = 0; i1 < k * l; i1++) {
				aBiome[i1] = getBiomeAtCoords(i + i1 % k, j + i1 / k);
			}
			return aBiome;
		}

		@Override
		public void cleanupCache() {}
	}
}
