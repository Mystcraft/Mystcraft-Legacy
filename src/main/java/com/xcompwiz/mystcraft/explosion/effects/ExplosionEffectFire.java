package com.xcompwiz.mystcraft.explosion.effects;

import java.util.Random;

import com.xcompwiz.mystcraft.explosion.ExplosionAdvanced;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ExplosionEffectFire extends ExplosionEffect {

	public static ExplosionEffect instance = new ExplosionEffectFire();

	private ExplosionEffectFire() {}

	@Override
	public void apply(World worldObj, ExplosionAdvanced explosion, BlockPos pos, Random rand, boolean isClient) {
		if (worldObj.isRemote)
			return;

		IBlockState blockstate = worldObj.getBlockState(pos.down());
		if (blockstate.isOpaqueCube() && rand.nextInt(3) == 0) {
			worldObj.setBlockState(pos, Blocks.FIRE.getDefaultState(), 3);
		}
	}

}
