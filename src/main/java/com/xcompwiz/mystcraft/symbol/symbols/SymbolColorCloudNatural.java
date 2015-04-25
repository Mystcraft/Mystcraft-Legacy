package com.xcompwiz.mystcraft.symbol.symbols;

import com.xcompwiz.mystcraft.api.util.Color;
import com.xcompwiz.mystcraft.api.world.AgeDirector;
import com.xcompwiz.mystcraft.api.world.logic.ICloudColorProvider;
import com.xcompwiz.mystcraft.symbol.SymbolBase;

public class SymbolColorCloudNatural extends SymbolBase {
	@Override
	public void registerLogic(AgeDirector controller, long seed) {
		controller.registerInterface(new CloudColorizer());
	}

	@Override
	public String identifier() {
		return "ColorCloudNat";
	}

	private class CloudColorizer implements ICloudColorProvider {
		public CloudColorizer() {}

		@Override
		public Color getCloudColor(float time, float celestial_angle) {
			return new Color(1, 1, 1);
		}
	}
}
