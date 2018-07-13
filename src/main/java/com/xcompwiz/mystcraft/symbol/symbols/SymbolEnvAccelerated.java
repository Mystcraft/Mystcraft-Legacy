package com.xcompwiz.mystcraft.symbol.symbols;

import com.xcompwiz.mystcraft.api.world.AgeDirector;
import com.xcompwiz.mystcraft.effects.EffectExtraTicks;
import com.xcompwiz.mystcraft.instability.InstabilityData;
import com.xcompwiz.mystcraft.symbol.SymbolBase;

import net.minecraft.util.ResourceLocation;

public class SymbolEnvAccelerated extends SymbolBase {

	public SymbolEnvAccelerated(ResourceLocation identifier) {
		super(identifier);
	}

	@Override
	public void registerLogic(AgeDirector controller, long seed) {
		controller.registerInterface(new EffectExtraTicks());
	}

	@Override
	public int instabilityModifier(int count) {
		return InstabilityData.symbol.accelerated;
	}

	@Override
	public boolean generatesConfigOption() {
		return true;
	}
}
