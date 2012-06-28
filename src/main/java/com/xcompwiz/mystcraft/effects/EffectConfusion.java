package com.xcompwiz.mystcraft.effects;

import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class EffectConfusion extends EffectPotion {

	@Override
	protected PotionEffect getEffect() {
		return new PotionEffect(Potion.confusion.id, 200, 0);
	}
}
