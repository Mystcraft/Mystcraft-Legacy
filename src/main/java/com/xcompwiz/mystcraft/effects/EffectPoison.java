package com.xcompwiz.mystcraft.effects;

import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class EffectPoison extends EffectPotion {
	int	level	= 0;

	public EffectPoison(Integer level) {
		this.level = level - 1;
	}

	@Override
	protected PotionEffect getEffect() {
		return new PotionEffect(Potion.poison.id, 200, level);
	}

}
