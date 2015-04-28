package com.xcompwiz.mystcraft.symbol.modifiers;

import com.xcompwiz.mystcraft.api.symbol.ModifierUtils;
import com.xcompwiz.mystcraft.api.util.ColorGradient;
import com.xcompwiz.mystcraft.api.world.AgeDirector;
import com.xcompwiz.mystcraft.symbol.SymbolBase;

public class SymbolGradient extends SymbolBase {
	public SymbolGradient() {}

	@Override
	public void registerLogic(AgeDirector controller, long seed) {
		ColorGradient gradient = controller.popModifier(ModifierUtils.GRADIENT).asGradient();
		Number factor = controller.popModifier(ModifierUtils.FACTOR).asNumber();
		if (factor == null) {
			factor = 1.0F;
		}
		if (gradient == null) gradient = new ColorGradient();
		gradient.pushColor(controller.popModifier(ModifierUtils.COLOR).asColor(), factor.floatValue());
		controller.setModifier(ModifierUtils.GRADIENT, gradient);
	}

	@Override
	public String identifier() {
		return "ModGradient";
	}
}
