package com.xcompwiz.mystcraft.symbol.symbols;

import com.xcompwiz.mystcraft.api.world.IAgeController;
import com.xcompwiz.mystcraft.effects.EffectExplosions;
import com.xcompwiz.mystcraft.instability.InstabilityData;
import com.xcompwiz.mystcraft.symbol.SymbolBase;

public class SymbolEnvExplosions extends SymbolBase {
	@Override
	public void registerLogic(IAgeController controller, long seed) {
		controller.registerInterface(new EffectExplosions());
	}

	@Override
	public String identifier() {
		return "EnvExplosions";
	}

	@Override
	public int instabilityModifier(int count) {
		return InstabilityData.symbol.explosion;
	}
}
