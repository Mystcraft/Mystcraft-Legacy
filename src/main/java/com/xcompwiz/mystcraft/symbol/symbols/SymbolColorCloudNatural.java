package com.xcompwiz.mystcraft.symbol.symbols;

import com.xcompwiz.mystcraft.api.util.Color;
import com.xcompwiz.mystcraft.api.world.AgeDirector;
import com.xcompwiz.mystcraft.api.world.logic.IDynamicColorProvider;
import com.xcompwiz.mystcraft.symbol.SymbolBase;

import net.minecraft.entity.Entity;
import net.minecraft.world.biome.BiomeGenBase;

public class SymbolColorCloudNatural extends SymbolBase {

	public SymbolColorCloudNatural(String identifier) {
		super(identifier);
	}

	@Override
	public void registerLogic(AgeDirector controller, long seed) {
		controller.registerInterface(new CloudColorizer(), IDynamicColorProvider.CLOUD);
	}

	private class CloudColorizer implements IDynamicColorProvider {
		public CloudColorizer() {}

		@Override
		public Color getColor(Entity entity, BiomeGenBase biome, float time, float celestial_angle, float partialtick) {
			return new Color(1, 1, 1);
		}
	}
}
