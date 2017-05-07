package com.xcompwiz.mystcraft.explosion.effects;

import java.util.Random;

import com.xcompwiz.mystcraft.explosion.ExplosionAdvanced;

import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;

public abstract class ExplosionEffect {

	public abstract void apply(World worldObj, ExplosionAdvanced explosion, ChunkPosition pos, Random rand, boolean isClient);

}
