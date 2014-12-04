package com.xcompwiz.mystcraft.symbol.symbols;

import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.biome.BiomeGenBase;

import com.xcompwiz.mystcraft.api.util.Color;
import com.xcompwiz.mystcraft.api.util.ColorGradient;
import com.xcompwiz.mystcraft.api.world.IAgeController;
import com.xcompwiz.mystcraft.api.world.logic.ISkyColorProvider;
import com.xcompwiz.mystcraft.symbol.ModifierUtils;
import com.xcompwiz.mystcraft.symbol.SymbolBase;

public class SymbolColorSkyNight extends SymbolBase {
	@Override
	public void registerLogic(IAgeController controller, long seed) {
		ColorGradient gradient = ModifierUtils.popGradient(controller, 1, 1, 1);
		controller.registerInterface(new Colorizer(controller, gradient));
	}

	@Override
	public String identifier() {
		return "ColorSkyNight";
	}

	private class Colorizer implements ISkyColorProvider {
		ColorGradient			gradient;
		private IAgeController	controller;

		public Colorizer(IAgeController controller, ColorGradient gradient) {
			this.controller = controller;
			this.gradient = gradient;
		}

		@Override
		public Color getSkyColor(Entity entity, BiomeGenBase biome, float time, float celestial_angle) {
			float alpha = MathHelper.cos(celestial_angle * (float) Math.PI * 2.0F) * 2.0F + 0.5F;

			if (alpha < 0.0F) alpha = 0.0F;
			if (alpha > 1.0F) alpha = 1.0F;
			alpha = (1 - alpha);

			Color color = gradient.getColor(controller.getTime() / 12000F);
			color = new Color(color.r * alpha, color.g * alpha, color.b * alpha);
			return color;
		}
	}
}
