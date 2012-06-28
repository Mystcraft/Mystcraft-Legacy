package com.xcompwiz.mystcraft.symbol.symbols;

import com.xcompwiz.mystcraft.symbol.SymbolBase;
import com.xcompwiz.mystcraft.world.IAgeController;

public class SymbolHideHorizon extends SymbolBase {

	@Override
	public void registerLogic(IAgeController controller, long seed) {
		controller.setHorizon(0);
		controller.setDrawHorizon(false);
		controller.setDrawVoid(false);
	}

	@Override
	public String identifier() {
		return "NoHorizon";
	}
}
