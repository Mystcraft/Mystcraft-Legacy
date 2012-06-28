package com.xcompwiz.mystcraft.effects;

import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class EffectWeakness extends EffectPotion {
	int	level	= 0;

	public EffectWeakness(Integer level) {
		this.level = level - 1;
	}

	@Override
	protected PotionEffect getEffect() {
		return new PotionEffect(Potion.weakness.id, 200, level);
	}

}
