package com.xcompwiz.mystcraft.symbol.symbols;

import com.xcompwiz.mystcraft.api.symbol.ModifierUtils;
import com.xcompwiz.mystcraft.api.util.ColorGradient;
import com.xcompwiz.mystcraft.api.world.AgeDirector;
import com.xcompwiz.mystcraft.effects.EffectLightning;
import com.xcompwiz.mystcraft.instability.InstabilityData;
import com.xcompwiz.mystcraft.symbol.SymbolBase;

import net.minecraft.util.ResourceLocation;

public class SymbolEnvLightning extends SymbolBase {

	public SymbolEnvLightning(ResourceLocation identifier) {
		super(identifier);
	}

	@Override
	public void registerLogic(AgeDirector controller, long seed) {
		ColorGradient gradient = ModifierUtils.popGradient(controller);
		if (gradient.getColorCount() == 0) {
			controller.registerInterface(new EffectLightning());
		} else {
			controller.registerInterface(new EffectLightning(gradient));
		}
	}

	@Override
	public int instabilityModifier(int count) {
		if (count > 1)
			return 0;
		return InstabilityData.symbol.charged;
	}

	@Override
	public boolean generatesConfigOption() {
		return true;
	}
}
