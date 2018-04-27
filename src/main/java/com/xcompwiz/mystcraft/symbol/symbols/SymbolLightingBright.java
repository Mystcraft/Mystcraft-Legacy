package com.xcompwiz.mystcraft.symbol.symbols;

import com.xcompwiz.mystcraft.api.world.AgeDirector;
import com.xcompwiz.mystcraft.api.world.logic.ILightingController;
import com.xcompwiz.mystcraft.instability.InstabilityData;
import com.xcompwiz.mystcraft.symbol.SymbolBase;
import net.minecraft.util.ResourceLocation;

public class SymbolLightingBright extends SymbolBase {

	public SymbolLightingBright(ResourceLocation identifier) {
		super(identifier);
	}

	@Override
	public void registerLogic(AgeDirector controller, long seed) {
		controller.registerInterface(new LightingController());
	}

	@Override
	public int instabilityModifier(int count) {
		return InstabilityData.symbol.bright;
	}

	@Override
	public boolean generatesConfigOption() {
		return true;
	}

	private class LightingController implements ILightingController {

		@Override
		public void generateLightBrightnessTable(float[] lightBrightnessTable) {
			float f = 0.25F;
			for (int i = 0; i < lightBrightnessTable.length; ++i) {
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
