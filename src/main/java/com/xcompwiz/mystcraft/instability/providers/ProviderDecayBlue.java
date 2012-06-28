package com.xcompwiz.mystcraft.instability.providers;

import com.xcompwiz.mystcraft.effects.EffectDecayBasic;
import com.xcompwiz.mystcraft.instability.IInstabilityController;
import com.xcompwiz.mystcraft.instability.IInstabilityProvider;
import com.xcompwiz.mystcraft.instability.decay.DecayHandler;

public class ProviderDecayBlue implements IInstabilityProvider {

	@Override
	public void addEffects(IInstabilityController controller, Integer level) {
		for (int i = 0; i < level; ++i)
			controller.registerEffect(new EffectDecayBasic(controller, DecayHandler.BLUE, 25, null));
	}
}
