package com.xcompwiz.mystcraft.symbol.symbols;

import com.xcompwiz.mystcraft.api.symbol.ModifierUtils;
import com.xcompwiz.mystcraft.api.util.Color;
import com.xcompwiz.mystcraft.api.util.ColorGradient;
import com.xcompwiz.mystcraft.api.world.AgeDirector;
import com.xcompwiz.mystcraft.api.world.logic.IFogColorProvider;
import com.xcompwiz.mystcraft.symbol.SymbolBase;

public class SymbolColorFog extends SymbolBase {
	@Override
	public void registerLogic(AgeDirector controller, long seed) {
		ColorGradient gradient = ModifierUtils.popGradient(controller, 0.7529412F, 0.8470588F, 1.0F);
		controller.registerInterface(new FogColorizer(controller, gradient));
	}

	@Override
	public String identifier() {
		return "ColorFog";
	}

	private static class FogColorizer implements IFogColorProvider {
		private static final Color	black	= new Color(0.0001F, 0.0001F, 0.0001F);

		ColorGradient				gradient;
		private AgeDirector		controller;

		public FogColorizer(AgeDirector controller, ColorGradient gradient) {
			this.controller = controller;
			this.gradient = gradient;
		}

		@Override
		public Color getFogColor(float f, float f1) {
			Color color = gradient.getColor(controller.getTime() / 12000F);
			if ((color.r == 0) && (color.g == 0) && (color.b == 0)) {
				color = black;
			}
			return color;
		}
	}
}
