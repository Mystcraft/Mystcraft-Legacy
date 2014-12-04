package com.xcompwiz.mystcraft.symbol.symbols;

import com.xcompwiz.mystcraft.api.util.Color;
import com.xcompwiz.mystcraft.api.util.ColorGradient;
import com.xcompwiz.mystcraft.api.world.IAgeController;
import com.xcompwiz.mystcraft.api.world.logic.ICloudColorProvider;
import com.xcompwiz.mystcraft.symbol.ModifierUtils;
import com.xcompwiz.mystcraft.symbol.SymbolBase;

public class SymbolColorCloud extends SymbolBase {
	@Override
	public void registerLogic(IAgeController controller, long seed) {
		ColorGradient gradient = ModifierUtils.popGradient(controller, 1, 1, 1);
		controller.registerInterface(new CloudColorizer(controller, gradient));
	}

	@Override
	public String identifier() {
		return "ColorCloud";
	}

	private class CloudColorizer implements ICloudColorProvider {
		ColorGradient			gradient;
		private IAgeController	controller;

		public CloudColorizer(IAgeController controller, ColorGradient gradient) {
			this.controller = controller;
			this.gradient = gradient;
		}

		@Override
		public Color getCloudColor(float time, float celestial_angle) {
			return gradient.getColor(controller.getTime() / 12000F);
		}
	}
}
