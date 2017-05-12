package com.xcompwiz.mystcraft.instability.providers;

import com.xcompwiz.mystcraft.api.instability.IInstabilityProvider;
import com.xcompwiz.mystcraft.api.instability.InstabilityDirector;
import com.xcompwiz.mystcraft.block.BlockDecay;
import com.xcompwiz.mystcraft.data.ModBlocks;
import com.xcompwiz.mystcraft.effects.EffectDecayBasic;
import com.xcompwiz.mystcraft.effects.EffectExtraTicks;
import com.xcompwiz.mystcraft.instability.decay.DecayHandler;

public class ProviderDecayPurple implements IInstabilityProvider {

	@Override
	public void addEffects(InstabilityDirector controller, Integer level) {
		for (int i = 0; i < level; ++i) {
			controller.registerEffect(new EffectDecayBasic(controller, DecayHandler.DecayType.PURPLE, 25, 54));
			controller.registerEffect(new EffectExtraTicks(ModBlocks.decay.getDefaultState().withProperty(BlockDecay.DECAY_META, DecayHandler.DecayType.PURPLE)));
		}
	}
}
