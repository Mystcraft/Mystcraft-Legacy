package com.xcompwiz.mystcraft.instability.providers;

import com.xcompwiz.mystcraft.api.instability.IInstabilityProvider;
import com.xcompwiz.mystcraft.api.instability.InstabilityDirector;
import com.xcompwiz.mystcraft.effects.EffectScorched;

public class ProviderScorched implements IInstabilityProvider {

	@Override
	public void addEffects(InstabilityDirector controller, Integer level) {
		controller.registerEffect(new EffectScorched(level));
	}
}
