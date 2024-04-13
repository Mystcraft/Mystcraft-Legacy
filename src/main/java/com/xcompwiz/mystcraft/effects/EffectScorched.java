package com.xcompwiz.mystcraft.effects;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import com.xcompwiz.mystcraft.api.world.logic.IEnvironmentalEffect;

public class EffectScorched implements IEnvironmentalEffect {
	int	level;

	public EffectScorched(Integer level) {
		this.level = level;
	}

	@Override
	public void tick(World worldObj, Chunk chunk) {
		if (worldObj.rand.nextInt(10) != 0) return;
		int i = worldObj.rand.nextInt(chunk.entityLists.length);
		List<Entity> list = chunk.entityLists[i];
		if (list.size() > 0) {
			Entity entity = list.get(worldObj.rand.nextInt(list.size()));
			if (worldObj.canBlockSeeTheSky(MathHelper.floor_double(entity.posX), MathHelper.floor_double(entity.posY), MathHelper.floor_double(entity.posZ))) {
				entity.setFire(4 * level);
			}
		}
	}
}
