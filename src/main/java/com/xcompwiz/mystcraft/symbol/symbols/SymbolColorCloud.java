package com.xcompwiz.mystcraft.symbol.symbols;

import com.xcompwiz.mystcraft.api.symbol.ModifierUtils;
import com.xcompwiz.mystcraft.api.util.Color;
import com.xcompwiz.mystcraft.api.util.ColorGradient;
import com.xcompwiz.mystcraft.api.world.AgeDirector;
import com.xcompwiz.mystcraft.api.world.logic.IDynamicColorProvider;
import com.xcompwiz.mystcraft.symbol.SymbolBase;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;

public class SymbolColorCloud extends SymbolBase {

	public SymbolColorCloud(ResourceLocation string) {
		super(string);
	}

	@Override
	public void registerLogic(AgeDirector controller, long seed) {
		ColorGradient gradient = ModifierUtils.popGradient(controller, 1, 1, 1);
		controller.registerInterface(new CloudColorizer(controller, gradient), IDynamicColorProvider.CLOUD);
	}

	@Override
	public boolean generatesConfigOption() {
		return true;
	}

	private class CloudColorizer implements IDynamicColorProvider {
		ColorGradient			gradient;
		private AgeDirector	controller;

		public CloudColorizer(AgeDirector controller, ColorGradient gradient) {
			this.controller = controller;
			this.gradient = gradient;
		}

		@Override
		public Color getColor(Entity entity, Biome biome, float time, float celestial_angle, float partialtick) {
			return gradient.getColor(controller.getTime() / 12000F);
		}
	}
}
