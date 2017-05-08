package com.xcompwiz.mystcraft.explosion.effects;

import java.util.Random;

import com.xcompwiz.mystcraft.explosion.ExplosionAdvanced;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class ExplosionEffect {

	public abstract void apply(World worldObj, ExplosionAdvanced explosion, BlockPos pos, Random rand, boolean isClient);

}
