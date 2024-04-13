package com.xcompwiz.mystcraft.effects;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class EffectPotionEnemy extends EffectPotion {
	public EffectPotionEnemy(int level, Boolean global, Integer potion, Integer duration) {
		super(level, global, potion, duration);
	}

	@Override
	protected boolean isTargetValid(World worldObj, Entity entity) {
		if (entity instanceof EntityPlayer) return false;
		return super.isTargetValid(worldObj, entity);
	}
}
