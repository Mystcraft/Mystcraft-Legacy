package com.xcompwiz.mystcraft.effects;

import net.minecraft.entity.Entity;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class EffectFatigue extends EffectPotion {
	int	level	= 2;

	public EffectFatigue(Integer level) {
		this.level = level + 1;
	}

	@Override
	protected boolean isTargetValid(World worldObj, Entity entity) {
		return true;
	}

	@Override
	protected PotionEffect getEffect() {
		return new PotionEffect(Potion.digSlowdown.id, 200, level);
	}

}
