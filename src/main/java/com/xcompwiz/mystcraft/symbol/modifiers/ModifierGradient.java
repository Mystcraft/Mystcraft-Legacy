package com.xcompwiz.mystcraft.symbol.modifiers;

import com.xcompwiz.mystcraft.symbol.ColorGradient;
import com.xcompwiz.mystcraft.symbol.ModifierUtils;
import com.xcompwiz.mystcraft.symbol.SymbolBase;
import com.xcompwiz.mystcraft.world.IAgeController;

public class ModifierGradient extends SymbolBase {
	public ModifierGradient() {}

	@Override
	public void registerLogic(IAgeController controller, long seed) {
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
