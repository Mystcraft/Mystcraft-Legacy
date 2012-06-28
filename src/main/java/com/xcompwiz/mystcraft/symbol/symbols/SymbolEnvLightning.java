package com.xcompwiz.mystcraft.symbol.symbols;

import com.xcompwiz.mystcraft.effects.EffectLightning;
import com.xcompwiz.mystcraft.instability.InstabilityData;
import com.xcompwiz.mystcraft.symbol.ColorGradient;
import com.xcompwiz.mystcraft.symbol.ModifierUtils;
import com.xcompwiz.mystcraft.symbol.SymbolBase;
import com.xcompwiz.mystcraft.world.IAgeController;

public class SymbolEnvLightning extends SymbolBase {
	@Override
	public void registerLogic(IAgeController controller, long seed) {
		ColorGradient gradient = ModifierUtils.popGradient(controller);
		if (gradient.getColorCount() == 0) {
			controller.registerInterface(new EffectLightning());
		} else {
			controller.registerInterface(new EffectLightning(gradient));
		}
	}

	@Override
	public String identifier() {
		return "EnvLightning";
	}

	@Override
	public int instabilityModifier(int count) {
		return InstabilityData.symbol.charged;
	}
}
