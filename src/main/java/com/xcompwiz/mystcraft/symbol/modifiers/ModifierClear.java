package com.xcompwiz.mystcraft.symbol.modifiers;

import com.xcompwiz.mystcraft.api.world.IAgeController;
import com.xcompwiz.mystcraft.symbol.SymbolBase;

public class ModifierClear extends SymbolBase {

	@Override
	public void registerLogic(IAgeController controller, long seed) {
		controller.clearModifiers();
	}

	@Override
	public String identifier() {
		return "ModClear";
	}
}
