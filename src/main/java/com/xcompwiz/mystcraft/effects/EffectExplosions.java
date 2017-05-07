package com.xcompwiz.mystcraft.effects;

import java.util.Random;

import com.xcompwiz.mystcraft.api.world.logic.IEnvironmentalEffect;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class EffectExplosions implements IEnvironmentalEffect {

	private int	updateLCG	= (new Random()).nextInt();

	@Override
	public void tick(World worldObj, Chunk chunk) {
		int var5 = chunk.xPosition * 16;
		int var6 = chunk.zPosition * 16;
		int var8;
		int var9;
		int var10;
		int var11;

		if (worldObj.rand.nextInt(1000) == 0) {
			updateLCG = updateLCG * 3 + 1013904223;
			var8 = updateLCG >> 2;
			var9 = var5 + (var8 & 15);
			var10 = var6 + (var8 >> 8 & 15);
			var11 = (var8 >> 16 & 255);
			worldObj.newExplosion((Entity) null, var9, var11 + 1, var10, 3.0F, true, true);
		}
	}

}
