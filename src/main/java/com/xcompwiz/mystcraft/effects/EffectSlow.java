package com.xcompwiz.mystcraft.effects;

import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class EffectSlow extends EffectPotion {
	int	level	= 1;

	public EffectSlow(Integer level) {
		this.level = level;
	}

	@Override
	protected PotionEffect getEffect() {
		return new PotionEffect(Potion.moveSlowdown.id, 200, level);
	}

}
