package com.xcompwiz.mystcraft.symbol.symbols;

import com.xcompwiz.mystcraft.effects.EffectScorched;
import com.xcompwiz.mystcraft.instability.InstabilityData;
import com.xcompwiz.mystcraft.symbol.SymbolBase;
import com.xcompwiz.mystcraft.world.IAgeController;

public class SymbolEnvScorched extends SymbolBase {
	@Override
	public void registerLogic(IAgeController controller, long seed) {
		controller.registerInterface(new EffectScorched(1));
	}

	@Override
	public String identifier() {
		return "EnvScorch";
	}

	@Override
	public int instabilityModifier(int count) {
		return InstabilityData.symbol.scorched;
	}
}
