package com.xcompwiz.mystcraft.symbol.symbols;

import com.xcompwiz.mystcraft.api.world.AgeDirector;
import com.xcompwiz.mystcraft.api.world.logic.ILightingController;
import com.xcompwiz.mystcraft.symbol.SymbolBase;
import net.minecraft.util.ResourceLocation;

public class SymbolLightingNormal extends SymbolBase {

	public SymbolLightingNormal(ResourceLocation identifier) {
		super(identifier);
	}

	@Override
	public void registerLogic(AgeDirector controller, long seed) {
		controller.registerInterface(new LightingController());
	}

	@Override
	public boolean generatesConfigOption() {
		return true;
	}

	private class LightingController implements ILightingController {

		@Override
		public void generateLightBrightnessTable(float[] lightBrightnessTable) {
			float f = 0.0F;

			for (int i = 0; i <= 15; ++i) {
				float f1 = 1.0F - (float)i / 15.0F;
				lightBrightnessTable[i] = (1.0F - f1) / (f1 * 3.0F + 1.0F) * (1.0F - f) + f;
			}
			//float f = 0.0F;
			//for (int i = 0; i < lightBrightnessTable.length; ++i) {
			//	float f1 = 1.0F - i / 15F;
			//	lightBrightnessTable[i] = ((1.0F - f1) / (f1 * 3F + 1.0F)) * (1.0F - f) + f;
			//}
		}

		@Override
		public int scaleLighting(int blockLightValue) {
			return blockLightValue;
		}

	}
}
