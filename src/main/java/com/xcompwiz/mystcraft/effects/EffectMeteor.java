package com.xcompwiz.mystcraft.effects;

import java.util.Random;

import com.xcompwiz.mystcraft.api.world.logic.IEnvironmentalEffect;
import com.xcompwiz.mystcraft.entity.EntityMeteor;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class EffectMeteor implements IEnvironmentalEffect {

	private int updateLCG = (new Random()).nextInt();

	@Override
	public void tick(World worldObj, Chunk chunk) {
		int var5 = chunk.x * 16;
		int var6 = chunk.z * 16;
		int var8;
		int var9;
		int var10;

		if (worldObj.rand.nextInt(50000) == 0) {
			updateLCG = updateLCG * 3 + 1013904223;
			var8 = updateLCG >> 2;
			var9 = var5 + (var8 & 15);
			var10 = var6 + (var8 >> 8 & 15);

			Entity entity = new EntityMeteor(worldObj, 1.0F, 0, var9, 500, var10, worldObj.rand.nextGaussian() * 0.25, worldObj.rand.nextFloat() * -2 - 2, worldObj.rand.nextGaussian() * 0.25);
			worldObj.spawnEntity(entity);
		}
	}

}
