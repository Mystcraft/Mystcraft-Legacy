package com.xcompwiz.mystcraft.symbol.symbols;

import com.xcompwiz.mystcraft.api.symbol.ModifierUtils;
import com.xcompwiz.mystcraft.api.util.ColorGradient;
import com.xcompwiz.mystcraft.api.world.AgeDirector;
import com.xcompwiz.mystcraft.effects.EffectLightning;
import com.xcompwiz.mystcraft.instability.InstabilityData;
import com.xcompwiz.mystcraft.symbol.SymbolBase;

public class SymbolEnvLightning extends SymbolBase {

	public SymbolEnvLightning(String identifier) {
		super(identifier);
	}

	@Override
	public void registerLogic(AgeDirector controller, long seed) {
		ColorGradient gradient = ModifierUtils.popGradient(controller);
		if (gradient.getColorCount() == 0) {
			controller.registerInterface(new EffectLightning());
		} else {
			controller.registerInterface(new EffectLightning(gradient));
		}
	}

	@Override
	public int instabilityModifier(int count) {
		if (count > 1) return 0;
		return InstabilityData.symbol.charged;
	}
}
