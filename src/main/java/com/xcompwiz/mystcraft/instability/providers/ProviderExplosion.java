package com.xcompwiz.mystcraft.instability.providers;

import com.xcompwiz.mystcraft.effects.EffectExplosions;
import com.xcompwiz.mystcraft.instability.IInstabilityController;
import com.xcompwiz.mystcraft.instability.IInstabilityProvider;

public class ProviderExplosion implements IInstabilityProvider {

	@Override
	public void addEffects(IInstabilityController controller, Integer level) {
		for (int i = 0; i < level; ++i)
			controller.registerEffect(new EffectExplosions());
	}
}
