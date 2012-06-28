package com.xcompwiz.mystcraft.symbol.symbols;

import com.xcompwiz.mystcraft.api.world.logic.ILightingController;
import com.xcompwiz.mystcraft.symbol.SymbolBase;
import com.xcompwiz.mystcraft.world.IAgeController;

public class SymbolLightingBright extends SymbolBase {

	@Override
	public void registerLogic(IAgeController controller, long seed) {
		controller.registerInterface(new LightingController());
	}

	@Override
	public String identifier() {
		return "LightingBright";
	}

	private class LightingController implements ILightingController {

		@Override
		public void generateLightBrightnessTable(float[] lightBrightnessTable) {
			float f = 0.25F;
			for (int i = 0; i < 16; ++i) {
				float f1 = 1.0F - i / 15F;
				lightBrightnessTable[i] = ((1.0F - f1) / (f1 * 3F + 1.0F)) * (1.0F - f) + f;
			}
		}

		@Override
		public int scaleLighting(int blockLightValue) {
			return blockLightValue + (15 - blockLightValue) / 2;
		}

	}
}
