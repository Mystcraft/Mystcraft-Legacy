package com.xcompwiz.mystcraft.symbol.symbols;

import com.xcompwiz.mystcraft.symbol.SymbolBase;
import com.xcompwiz.mystcraft.world.IAgeController;

public class SymbolAntiPvP extends SymbolBase {

	@Override
	public void registerLogic(IAgeController controller, long seed) {
		controller.setPvPEnabled(false);
	}

	@Override
	public String identifier() {
		return "PvPOff";
	}
}
