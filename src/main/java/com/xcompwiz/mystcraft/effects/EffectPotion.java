package com.xcompwiz.mystcraft.effects;

import com.google.common.collect.Iterables;
import com.xcompwiz.mystcraft.api.world.logic.IEnvironmentalEffect;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class EffectPotion implements IEnvironmentalEffect {

	private int level;
	private boolean isGlobal;
	private Potion potion;
	private int duration;

	public EffectPotion(int level, Boolean global, Potion type, Integer duration) {
		this.level = level - 1;
		this.isGlobal = global;
		this.potion = type;
		this.duration = duration;
	}

	@Override
	public void tick(World worldObj, Chunk chunk) {
		ClassInheritanceMultiMap<Entity>[] entityLists = chunk.getEntityLists();
		int i = worldObj.rand.nextInt(entityLists.length);
		ClassInheritanceMultiMap<Entity> list = entityLists[i];
		if (list.size() > 0) {
			int index = worldObj.rand.nextInt(list.size());
			Entity entity = Iterables.get(list, index);
			if (entity instanceof EntityLivingBase) {
				if (isTargetValid(worldObj, entity)) {
					((EntityLivingBase) entity).addPotionEffect(getEffect());
				}
			}
		}
	}

	protected boolean isTargetValid(World worldObj, Entity entity) {
		return this.isGlobal || (worldObj.canSeeSky(entity.getPosition()));
	}

	protected PotionEffect getEffect() {
		return new PotionEffect(this.potion, this.duration, this.level);
	}
}
