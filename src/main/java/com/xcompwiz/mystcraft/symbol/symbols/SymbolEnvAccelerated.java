package com.xcompwiz.mystcraft.symbol.symbols;

import com.xcompwiz.mystcraft.effects.EffectExtraTicks;
import com.xcompwiz.mystcraft.instability.InstabilityData;
import com.xcompwiz.mystcraft.symbol.SymbolBase;
import com.xcompwiz.mystcraft.world.IAgeController;

public class SymbolEnvAccelerated extends SymbolBase {

	@Override
	public void registerLogic(IAgeController controller, long seed) {
		controller.registerInterface(new EffectExtraTicks());
	}

	@Override
	public String identifier() {
		return "EnvAccel";
	}

	@Override
	public int instabilityModifier(int count) {
		return InstabilityData.symbol.accelerated;
	}
}
