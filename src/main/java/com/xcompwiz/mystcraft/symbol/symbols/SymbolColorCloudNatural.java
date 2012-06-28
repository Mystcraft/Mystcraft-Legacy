package com.xcompwiz.mystcraft.symbol.symbols;

import com.xcompwiz.mystcraft.api.world.logic.ICloudColorProvider;
import com.xcompwiz.mystcraft.symbol.Color;
import com.xcompwiz.mystcraft.symbol.SymbolBase;
import com.xcompwiz.mystcraft.world.IAgeController;

public class SymbolColorCloudNatural extends SymbolBase {
	@Override
	public void registerLogic(IAgeController controller, long seed) {
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
