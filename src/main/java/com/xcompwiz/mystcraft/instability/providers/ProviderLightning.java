package com.xcompwiz.mystcraft.instability.providers;

import com.xcompwiz.mystcraft.effects.EffectLightning;
import com.xcompwiz.mystcraft.instability.IInstabilityController;
import com.xcompwiz.mystcraft.instability.IInstabilityProvider;

public class ProviderLightning implements IInstabilityProvider {

	@Override
	public void addEffects(IInstabilityController controller, Integer level) {
		for (int i = 0; i < level; ++i)
			controller.registerEffect(new EffectLightning());
	}
}
