package com.xcompwiz.mystcraft.instability.providers;

import net.minecraft.block.material.Material;

import com.xcompwiz.mystcraft.api.instability.IInstabilityProvider;
import com.xcompwiz.mystcraft.api.instability.InstabilityDirector;
import com.xcompwiz.mystcraft.data.ModBlocks;
import com.xcompwiz.mystcraft.effects.EffectDecayBasic;
import com.xcompwiz.mystcraft.effects.EffectExtraTicks;
import com.xcompwiz.mystcraft.instability.decay.DecayHandler;

public class ProviderDecayBlack implements IInstabilityProvider {

	@Override
	public void addEffects(InstabilityDirector controller, Integer level) {
		for (int i = 0; i < level; ++i) {
			EffectDecayBasic effect = new EffectDecayBasic(controller, DecayHandler.BLACK, 0, 12);
			effect.banMaterial(Material.air);
			effect.banMaterial(Material.water);
			effect.banMaterial(Material.lava);
			controller.registerEffect(effect);
			controller.registerEffect(new EffectExtraTicks(ModBlocks.decay, DecayHandler.BLACK));
		}
	}
}
