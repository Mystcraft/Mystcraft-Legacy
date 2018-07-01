package com.xcompwiz.mystcraft.effects;

import com.google.common.collect.Iterables;
import com.xcompwiz.mystcraft.api.world.logic.IEnvironmentalEffect;

import net.minecraft.entity.Entity;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class EffectScorched implements IEnvironmentalEffect {

	int level;

	public EffectScorched(Integer level) {
		this.level = level;
	}

	@Override
	public void tick(World worldObj, Chunk chunk) {
		if (worldObj.rand.nextInt(10) != 0)
			return;
		ClassInheritanceMultiMap<Entity>[] entityLists = chunk.getEntityLists();
		int i = worldObj.rand.nextInt(entityLists.length);
		ClassInheritanceMultiMap<Entity> list = entityLists[i];
		if (list.size() > 0) {
			int index = worldObj.rand.nextInt(list.size());
			Entity entity = Iterables.get(list, index);
			if (worldObj.canSeeSky(entity.getPosition())) {
				entity.setFire(4 * level);
			}
		}
	}
}
