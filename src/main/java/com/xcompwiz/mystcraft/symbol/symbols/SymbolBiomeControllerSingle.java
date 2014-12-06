package com.xcompwiz.mystcraft.symbol.symbols;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.world.biome.BiomeGenBase;

import com.xcompwiz.mystcraft.api.symbol.ModifierUtils;
import com.xcompwiz.mystcraft.api.world.IAgeController;
import com.xcompwiz.mystcraft.api.world.logic.IBiomeController;
import com.xcompwiz.mystcraft.symbol.SymbolBase;
import com.xcompwiz.mystcraft.symbol.modifiers.ModifierBiome;

public class SymbolBiomeControllerSingle extends SymbolBase {

	@Override
	public void registerLogic(IAgeController controller, long seed) {
		List<BiomeGenBase> biomes = new ArrayList<BiomeGenBase>();
		BiomeGenBase biome;
		biome = ModifierUtils.popBiome(controller);
		if (biome != null) {
			biomes.add(biome);
		}

		Random rand = new Random(controller.getSeed());
		while (biomes.size() < 1) {
			biomes.add(ModifierBiome.getRandomBiome(rand));
		}
		controller.registerInterface(new BiomeController(biomes));
	}

	@Override
	public String identifier() {
		return "BioConSingle";
	}

	private class BiomeController implements IBiomeController {
		private List<BiomeGenBase>	biomes;

		public BiomeController(List<BiomeGenBase> biomes) {
			this.biomes = biomes;
		}

		@Override
		public List<BiomeGenBase> getValidSpawnBiomes() {
			return biomes;
		}

		@Override
		public float[] getRainfallField(float[] af, int x, int z, int k, int l) {
			if (af == null || af.length < k * l) {
				af = new float[k * l];
			}
			for (int i1 = 0; i1 < k * l; i1++) {
				float f = biomes.get(0).rainfall;
				if (f > 1.0F) {
					f = 1.0F;
				}
				af[i1] = f;
			}

			return af;
		}

		@Override
		public BiomeGenBase getBiomeAtCoords(int i, int j) {
			return biomes.get(0);
		}

		@Override
		public BiomeGenBase[] getBiomesFromGenerationField(BiomeGenBase[] abiomegenbase, int i, int j, int k, int l) {
			if (abiomegenbase == null || abiomegenbase.length < k * l) {
				abiomegenbase = new BiomeGenBase[k * l];
			}
			for (int i1 = 0; i1 < k * l; i1++) {
				abiomegenbase[i1] = biomes.get(0);
			}

			return abiomegenbase;
		}

		@Override
		public BiomeGenBase[] getBiomesAtCoords(BiomeGenBase[] abiomegenbase, int i, int j, int k, int l, boolean flag) {
			if (abiomegenbase == null || abiomegenbase.length < k * l) {
				abiomegenbase = new BiomeGenBase[k * l];
			}
			if (flag && k == 16 && l == 16 && (i & 0xf) == 0 && (j & 0xf) == 0) { return createBiomeArray(abiomegenbase, k, l, biomes.get(0)); }
			for (int i1 = 0; i1 < k * l; i1++) {
				abiomegenbase[i1] = biomes.get(0);
			}

			return abiomegenbase;
		}

		private BiomeGenBase[] createBiomeArray(BiomeGenBase abiomegenbase[], int k, int l, BiomeGenBase biome) {
			if (abiomegenbase == null || abiomegenbase.length < k * l) {
				abiomegenbase = new BiomeGenBase[k * l];
			}
			for (int i1 = 0; i1 < k * l; i1++) {
				abiomegenbase[i1] = biome;
			}
			return abiomegenbase;
		}

		@Override
		public void cleanupCache() {}
	}
}
