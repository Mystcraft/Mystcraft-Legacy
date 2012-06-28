package com.xcompwiz.mystcraft.effects;

import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class EffectHunger extends EffectPotion {
	int	level	= 2;

	public EffectHunger(Integer level) {
		this.level = level + 1;
	}

	@Override
	protected PotionEffect getEffect() {
		return new PotionEffect(Potion.hunger.id, 50, level);
	}

}
