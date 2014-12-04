package com.xcompwiz.mystcraft.symbol.symbols;

import com.xcompwiz.mystcraft.api.world.IAgeController;
import com.xcompwiz.mystcraft.symbol.SymbolBase;

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
