package com.xcompwiz.mystcraft.symbol.symbols;

import com.xcompwiz.mystcraft.api.world.AgeDirector;
import com.xcompwiz.mystcraft.effects.EffectMeteor;
import com.xcompwiz.mystcraft.instability.InstabilityData;
import com.xcompwiz.mystcraft.symbol.SymbolBase;

public class SymbolEnvMeteor extends SymbolBase {
	
	public SymbolEnvMeteor(String identifier) {
		super(identifier);
	}

	@Override
	public void registerLogic(AgeDirector controller, long seed) {
		controller.registerInterface(new EffectMeteor());
	}

	@Override
	public int instabilityModifier(int count) {
		return InstabilityData.symbol.meteors;
	}
}
