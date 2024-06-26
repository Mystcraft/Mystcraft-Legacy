package com.xcompwiz.mystcraft.symbol.symbols;

import com.xcompwiz.mystcraft.api.util.Color;
import com.xcompwiz.mystcraft.api.world.AgeDirector;
import com.xcompwiz.mystcraft.api.world.logic.IDynamicColorProvider;
import com.xcompwiz.mystcraft.symbol.SymbolBase;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;

public class SymbolColorCloudNatural extends SymbolBase {

	public SymbolColorCloudNatural(ResourceLocation identifier) {
		super(identifier);
	}

	@Override
	public void registerLogic(AgeDirector controller, long seed) {
		controller.registerInterface(new CloudColorizer(), IDynamicColorProvider.CLOUD);
	}

	@Override
	public boolean generatesConfigOption() {
		return true;
	}

	private class CloudColorizer implements IDynamicColorProvider {
		public CloudColorizer() {}

		@Override
		public Color getColor(Entity entity, Biome biome, float time, float celestial_angle, float partialtick) {
			return new Color(1, 1, 1);
		}
	}
}
