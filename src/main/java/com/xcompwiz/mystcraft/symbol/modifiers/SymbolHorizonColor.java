package com.xcompwiz.mystcraft.symbol.modifiers;

import com.xcompwiz.mystcraft.api.symbol.ModifierUtils;
import com.xcompwiz.mystcraft.api.util.ColorGradient;
import com.xcompwiz.mystcraft.api.world.AgeDirector;
import com.xcompwiz.mystcraft.api.world.logic.Modifier;
import com.xcompwiz.mystcraft.symbol.SymbolBase;

public class SymbolHorizonColor extends SymbolBase {
	@Override
	public void registerLogic(AgeDirector controller, long seed) {
		ColorGradient sunset = controller.popModifier(ModifierUtils.SUNSET).asGradient();
		if (sunset == null) sunset = new ColorGradient(); // Create sunset gradient if invalid
		ColorGradient gradient = ModifierUtils.popGradient(controller);
		sunset.appendGradient(gradient); // Append gradient to sunset
		controller.setModifier(ModifierUtils.SUNSET, new Modifier(sunset, 0));
	}

	@Override
	public String identifier() {
		return "ColorHorizon";
	}
}
