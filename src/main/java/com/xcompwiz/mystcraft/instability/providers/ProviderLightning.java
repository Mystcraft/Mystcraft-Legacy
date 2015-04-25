package com.xcompwiz.mystcraft.instability.providers;

import com.xcompwiz.mystcraft.api.instability.InstabilityDirector;
import com.xcompwiz.mystcraft.api.instability.IInstabilityProvider;
import com.xcompwiz.mystcraft.effects.EffectLightning;

public class ProviderLightning implements IInstabilityProvider {

	@Override
	public void addEffects(InstabilityDirector controller, Integer level) {
		for (int i = 0; i < level; ++i)
			controller.registerEffect(new EffectLightning());
	}
}
