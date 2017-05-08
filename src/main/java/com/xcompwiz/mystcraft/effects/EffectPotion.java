package com.xcompwiz.mystcraft.effects;

import java.util.List;

import com.xcompwiz.mystcraft.api.world.logic.IEnvironmentalEffect;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class EffectPotion implements IEnvironmentalEffect {

	private int		level;
	private boolean	isGlobal;
	private int		potionid;
	private int		duration;

	public EffectPotion(int level, Boolean global, Integer potion, Integer duration) {
		this.level = level - 1;
		this.isGlobal = global;
		this.potionid = potion;
		this.duration = duration;
	}

	@Override
	public void tick(World worldObj, Chunk chunk) {
		ClassInheritanceMultiMap<Entity>[] entityLists = chunk.getEntityLists();
		int i = worldObj.rand.nextInt(entityLists.length);
		ClassInheritanceMultiMap<Entity> list = entityLists[i];
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
		return this.isGlobal || (worldObj.canBlockSeeTheSky(MathHelper.floor_double(entity.posX), MathHelper.floor_double(entity.posY), MathHelper.floor_double(entity.posZ)));
	}

	protected PotionEffect getEffect() {
		return new PotionEffect(this.potionid, this.duration, this.level);
	}
}
