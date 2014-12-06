package com.xcompwiz.mystcraft.symbol.symbols;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.biome.BiomeGenBase;

import com.xcompwiz.mystcraft.api.symbol.ModifierUtils;
import com.xcompwiz.mystcraft.api.world.IAgeController;
import com.xcompwiz.mystcraft.symbol.SymbolBase;

public class SymbolBiomeControllerSmall extends SymbolBase {

	@Override
	public void registerLogic(IAgeController controller, long seed) {
		List<BiomeGenBase> biomes = new ArrayList<BiomeGenBase>();
		BiomeGenBase biome;
		biome = ModifierUtils.popBiome(controller);
		while (biome != null) {
			biomes.add(biome);
			biome = ModifierUtils.popBiome(controller);
		}

		controller.registerInterface(new SymbolBiomeControllerLarge.BiomeController(controller, 1, biomes));
	}

	@Override
	public String identifier() {
		return "BioConSmall";
	}
}
