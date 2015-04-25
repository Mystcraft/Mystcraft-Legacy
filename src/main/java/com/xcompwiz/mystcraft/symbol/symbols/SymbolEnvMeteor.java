package com.xcompwiz.mystcraft.symbol.symbols;

import com.xcompwiz.mystcraft.api.world.AgeDirector;
import com.xcompwiz.mystcraft.effects.EffectMeteor;
import com.xcompwiz.mystcraft.instability.InstabilityData;
import com.xcompwiz.mystcraft.symbol.SymbolBase;

public class SymbolEnvMeteor extends SymbolBase {
	@Override
	public void registerLogic(AgeDirector controller, long seed) {
		controller.registerInterface(new EffectMeteor());
	}

	@Override
	public String identifier() {
		return "EnvMeteor";
	}

	@Override
	public int instabilityModifier(int count) {
		return InstabilityData.symbol.meteors;
	}
}
