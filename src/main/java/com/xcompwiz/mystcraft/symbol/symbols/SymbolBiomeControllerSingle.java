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

public class SymbolBiomeControllerSingle extends SymbolBase {

	public SymbolBiomeControllerSingle(ResourceLocation identifier) {
		super(identifier);
	}

	@Override
	public void registerLogic(AgeDirector controller, long seed) {
		List<Biome> biomes = new ArrayList<Biome>();
		Biome biome;
		biome = ModifierUtils.popBiome(controller);
		if (biome != null) {
			biomes.add(biome);
		}

		Random rand = new Random(controller.getSeed());
		while (biomes.size() < 1) {
			biomes.add(SymbolBiome.getRandomBiome(rand));
		}
		controller.registerInterface(new BiomeController(biomes));
	}

	@Override
	public boolean generatesConfigOption() {
		return true;
	}

	private class BiomeController implements IBiomeController {
		private List<Biome>	biomes;

		public BiomeController(List<Biome> biomes) {
			this.biomes = biomes;
		}

		@Override
		public List<Biome> getValidSpawnBiomes() {
			return biomes;
		}

		@Override
		public Biome getBiomeAtCoords(int i, int j) {
			return biomes.get(0);
		}

		@Override
		public Biome[] getBiomesForGeneration(Biome[] aBiome, int i, int j, int k, int l) {
			if (aBiome == null || aBiome.length < k * l) {
				aBiome = new Biome[k * l];
			}
			for (int i1 = 0; i1 < k * l; i1++) {
				aBiome[i1] = biomes.get(0);
			}

			return aBiome;
		}

		@Override
		public Biome[] getBiomesAtCoords(Biome[] aBiome, int i, int j, int k, int l, boolean flag) {
			if (aBiome == null || aBiome.length < k * l) {
				aBiome = new Biome[k * l];
			}
			if (flag && k == 16 && l == 16 && (i & 0xf) == 0 && (j & 0xf) == 0) { return createBiomeArray(aBiome, k, l, biomes.get(0)); }
			for (int i1 = 0; i1 < k * l; i1++) {
				aBiome[i1] = biomes.get(0);
			}

			return aBiome;
		}

		private Biome[] createBiomeArray(Biome aBiome[], int k, int l, Biome biome) {
			if (aBiome == null || aBiome.length < k * l) {
				aBiome = new Biome[k * l];
			}
			for (int i1 = 0; i1 < k * l; i1++) {
				aBiome[i1] = biome;
			}
			return aBiome;
		}

		@Override
		public void cleanupCache() {}
	}
}
