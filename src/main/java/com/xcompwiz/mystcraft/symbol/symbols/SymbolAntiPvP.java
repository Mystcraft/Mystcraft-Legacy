package com.xcompwiz.mystcraft.symbol.symbols;

import com.xcompwiz.mystcraft.api.world.IAgeController;
import com.xcompwiz.mystcraft.symbol.SymbolBase;

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
