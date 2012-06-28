package com.xcompwiz.mystcraft.symbol.symbols;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.biome.BiomeGenBase;

import com.xcompwiz.mystcraft.symbol.ModifierUtils;
import com.xcompwiz.mystcraft.symbol.SymbolBase;
import com.xcompwiz.mystcraft.world.IAgeController;

public class SymbolBiomeControllerTiny extends SymbolBase {

	@Override
	public void registerLogic(IAgeController controller, long seed) {
		List<BiomeGenBase> biomes = new ArrayList<BiomeGenBase>();
		BiomeGenBase biome;
		biome = ModifierUtils.popBiome(controller);
		while (biome != null) {
			biomes.add(biome);
			biome = ModifierUtils.popBiome(controller);
		}

		controller.registerInterface(new SymbolBiomeControllerLarge.BiomeController(controller, 0, biomes));
	}

	@Override
	public String identifier() {
		return "BioConTiny";
	}
}
