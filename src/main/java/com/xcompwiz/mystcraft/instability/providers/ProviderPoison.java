package com.xcompwiz.mystcraft.instability.providers;

import com.xcompwiz.mystcraft.effects.EffectPoison;
import com.xcompwiz.mystcraft.instability.IInstabilityController;
import com.xcompwiz.mystcraft.instability.IInstabilityProvider;

public class ProviderPoison implements IInstabilityProvider {

	@Override
	public void addEffects(IInstabilityController controller, Integer level) {
		controller.registerEffect(new EffectPoison(level));
	}
}
