package com.xcompwiz.mystcraft.symbol.symbols;

import com.xcompwiz.mystcraft.api.world.IAgeController;
import com.xcompwiz.mystcraft.api.world.logic.ILightingController;
import com.xcompwiz.mystcraft.symbol.SymbolBase;

public class SymbolLightingNormal extends SymbolBase {

	@Override
	public void registerLogic(IAgeController controller, long seed) {
		controller.registerInterface(new LightingController());
	}

	@Override
	public String identifier() {
		return "LightingNormal";
	}

	private class LightingController implements ILightingController {

		@Override
		public void generateLightBrightnessTable(float[] lightBrightnessTable) {
			float f = 0.0F;
			for (int i = 0; i <= 15; i++) {
				float f1 = 1.0F - i / 15F;
				lightBrightnessTable[i] = ((1.0F - f1) / (f1 * 3F + 1.0F)) * (1.0F - f) + f;
			}
		}

		@Override
		public int scaleLighting(int blockLightValue) {
			return blockLightValue;
		}

	}
}
