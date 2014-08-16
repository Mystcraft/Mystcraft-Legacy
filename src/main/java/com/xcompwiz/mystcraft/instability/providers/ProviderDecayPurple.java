package com.xcompwiz.mystcraft.instability.providers;

import com.xcompwiz.mystcraft.block.BlockDecay;
import com.xcompwiz.mystcraft.effects.EffectDecayBasic;
import com.xcompwiz.mystcraft.effects.EffectExtraTicks;
import com.xcompwiz.mystcraft.instability.IInstabilityController;
import com.xcompwiz.mystcraft.instability.IInstabilityProvider;
import com.xcompwiz.mystcraft.instability.decay.DecayHandler;

public class ProviderDecayPurple implements IInstabilityProvider {

	@Override
	public void addEffects(IInstabilityController controller, Integer level) {
		for (int i = 0; i < level; ++i) {
			controller.registerEffect(new EffectDecayBasic(controller, DecayHandler.PURPLE, 25, 54));
			controller.registerEffect(new EffectExtraTicks(BlockDecay.instance, DecayHandler.PURPLE));
		}
	}
}
