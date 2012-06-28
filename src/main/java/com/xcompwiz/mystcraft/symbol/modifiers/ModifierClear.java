package com.xcompwiz.mystcraft.symbol.modifiers;

import com.xcompwiz.mystcraft.symbol.SymbolBase;
import com.xcompwiz.mystcraft.world.IAgeController;

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
