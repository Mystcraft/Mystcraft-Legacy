package com.xcompwiz.mystcraft.symbol.symbols;

import com.xcompwiz.mystcraft.api.symbol.ModifierUtils;
import com.xcompwiz.mystcraft.api.util.Color;
import com.xcompwiz.mystcraft.api.util.ColorGradient;
import com.xcompwiz.mystcraft.api.world.AgeDirector;
import com.xcompwiz.mystcraft.api.world.logic.IDynamicColorProvider;
import com.xcompwiz.mystcraft.symbol.SymbolBase;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;

public class SymbolColorSky extends SymbolBase {

	public SymbolColorSky(String identifier) {
		super(identifier);
	}

	@Override
	public void registerLogic(AgeDirector controller, long seed) {
		ColorGradient gradient = ModifierUtils.popGradient(controller, 1, 1, 1);
		controller.registerInterface(new Colorizer(controller, gradient), IDynamicColorProvider.SKY);
	}

	private class Colorizer implements IDynamicColorProvider {
		ColorGradient		gradient;
		private AgeDirector	controller;

		public Colorizer(AgeDirector controller, ColorGradient gradient) {
			this.controller = controller;
			this.gradient = gradient;
		}

		@Override
		public Color getColor(Entity entity, Biome biome, float time, float celestial_angle, float partialtick) {
			float alpha = MathHelper.cos(celestial_angle * (float) Math.PI * 2.0F) * 2.0F + 0.5F;

			if (alpha < 0.0F) alpha = 0.0F;
			if (alpha > 1.0F) alpha = 1.0F;

			Color color = gradient.getColor(controller.getTime() / 12000F);
			color = new Color(color.r * alpha, color.g * alpha, color.b * alpha);
			return color;
		}
	}
}
