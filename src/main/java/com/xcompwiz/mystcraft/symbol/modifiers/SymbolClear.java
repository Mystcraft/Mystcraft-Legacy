package com.xcompwiz.mystcraft.symbol.modifiers;

import com.xcompwiz.mystcraft.api.world.AgeDirector;
import com.xcompwiz.mystcraft.symbol.SymbolBase;

public class SymbolClear extends SymbolBase {

	@Override
	public void registerLogic(AgeDirector controller, long seed) {
		controller.clearModifiers();
	}

	@Override
	public String identifier() {
		return "ModClear";
	}
}
