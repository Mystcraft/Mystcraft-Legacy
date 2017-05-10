package com.xcompwiz.mystcraft.effects;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.world.World;

public class EffectPotionEnemy extends EffectPotion {

	public EffectPotionEnemy(int level, Boolean global, Potion potion, Integer duration) {
		super(level, global, potion, duration);
	}

	@Override
	protected boolean isTargetValid(World worldObj, Entity entity) {
		if (entity instanceof EntityPlayer) return false;
		return super.isTargetValid(worldObj, entity);
	}
}
