package com.xcompwiz.mystcraft.effects;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EffectEnemyRegeneration extends EffectPotion {
	int	level	= 0;

	public EffectEnemyRegeneration(int level) {
		this.level = level - 1;
	}

	@Override
	protected boolean isTargetValid(World worldObj, Entity entity) {
		if (entity instanceof EntityPlayer) return false;
		return (worldObj.canBlockSeeTheSky(MathHelper.floor_double(entity.posX), MathHelper.floor_double(entity.posY), MathHelper.floor_double(entity.posZ)));
	}

	@Override
	protected PotionEffect getEffect() {
		return new PotionEffect(Potion.regeneration.id, 50 + 25 * (level), level);
	}

}
