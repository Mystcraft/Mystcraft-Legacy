package com.xcompwiz.mystcraft.explosion.effects;

import java.util.Random;

import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;

import com.xcompwiz.mystcraft.explosion.ExplosionAdvanced;

public abstract class ExplosionEffect {

	public abstract void apply(World worldObj, ExplosionAdvanced explosion, ChunkPosition pos, Random rand, boolean isClient);

}
