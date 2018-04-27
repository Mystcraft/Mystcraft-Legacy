package com.xcompwiz.mystcraft.symbol.symbols;

import com.xcompwiz.mystcraft.api.world.AgeDirector;
import com.xcompwiz.mystcraft.effects.EffectExplosions;
import com.xcompwiz.mystcraft.instability.InstabilityData;
import com.xcompwiz.mystcraft.symbol.SymbolBase;
import net.minecraft.util.ResourceLocation;

public class SymbolEnvExplosions extends SymbolBase {

	public SymbolEnvExplosions(ResourceLocation identifier) {
		super(identifier);
	}

	@Override
	public void registerLogic(AgeDirector controller, long seed) {
		controller.registerInterface(new EffectExplosions());
	}

	@Override
	public int instabilityModifier(int count) {
		return InstabilityData.symbol.explosion;
	}

	@Override
	public boolean generatesConfigOption() {
		return true;
	}
}
