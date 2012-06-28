package com.xcompwiz.mystcraft.effects;

import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class EffectJump extends EffectPotion {

	@Override
	protected PotionEffect getEffect() {
		return new PotionEffect(Potion.jump.id, 200, 2);
	}

}
