package com.xcompwiz.mystcraft.effects;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import com.xcompwiz.mystcraft.api.world.logic.IEnvironmentalEffect;

public abstract class EffectPotion implements IEnvironmentalEffect {

	@Override
	public void tick(World worldObj, Chunk chunk) {
		int i = worldObj.rand.nextInt(chunk.entityLists.length);
		List<Entity> list = chunk.entityLists[i];
		if (list.size() > 0) {
			Entity entity = list.get(worldObj.rand.nextInt(list.size()));
			if (entity instanceof EntityLivingBase) {
				if (isTargetValid(worldObj, entity)) {
					((EntityLivingBase) entity).addPotionEffect(getEffect());
				}
			}
		}
	}

	protected boolean isTargetValid(World worldObj, Entity entity) {
		return (worldObj.canBlockSeeTheSky(MathHelper.floor_double(entity.posX), MathHelper.floor_double(entity.posY), MathHelper.floor_double(entity.posZ)));
	}

	protected abstract PotionEffect getEffect();

}
