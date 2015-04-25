package com.xcompwiz.mystcraft.symbol.symbols;

import com.xcompwiz.mystcraft.api.world.AgeDirector;
import com.xcompwiz.mystcraft.effects.EffectScorched;
import com.xcompwiz.mystcraft.instability.InstabilityData;
import com.xcompwiz.mystcraft.symbol.SymbolBase;

public class SymbolEnvScorched extends SymbolBase {
	@Override
	public void registerLogic(AgeDirector controller, long seed) {
		controller.registerInterface(new EffectScorched(1));
	}

	@Override
	public String identifier() {
		return "EnvScorch";
	}

	@Override
	public int instabilityModifier(int count) {
		if (count > 1) return 0;
		return InstabilityData.symbol.scorched;
	}
}
