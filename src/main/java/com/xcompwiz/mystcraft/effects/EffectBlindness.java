package com.xcompwiz.mystcraft.effects;

import net.minecraft.entity.Entity;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class EffectBlindness extends EffectPotion {

	@Override
	protected boolean isTargetValid(World worldObj, Entity entity) {
		return true;
	}

	@Override
	protected PotionEffect getEffect() {
		return new PotionEffect(Potion.blindness.id, 200, 0);
	}
}
