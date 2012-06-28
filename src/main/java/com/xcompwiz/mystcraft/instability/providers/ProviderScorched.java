package com.xcompwiz.mystcraft.instability.providers;

import com.xcompwiz.mystcraft.effects.EffectScorched;
import com.xcompwiz.mystcraft.instability.IInstabilityController;
import com.xcompwiz.mystcraft.instability.IInstabilityProvider;

public class ProviderScorched implements IInstabilityProvider {

	@Override
	public void addEffects(IInstabilityController controller, Integer level) {
		controller.registerEffect(new EffectScorched(level));
	}
}
