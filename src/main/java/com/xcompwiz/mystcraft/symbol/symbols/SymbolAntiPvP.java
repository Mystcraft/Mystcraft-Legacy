package com.xcompwiz.mystcraft.symbol.symbols;

import com.xcompwiz.mystcraft.api.world.AgeDirector;
import com.xcompwiz.mystcraft.symbol.SymbolBase;

public class SymbolAntiPvP extends SymbolBase {

	@Override
	public void registerLogic(AgeDirector controller, long seed) {
		controller.setPvPEnabled(false);
	}

	@Override
	public String identifier() {
		return "PvPOff";
	}
}
