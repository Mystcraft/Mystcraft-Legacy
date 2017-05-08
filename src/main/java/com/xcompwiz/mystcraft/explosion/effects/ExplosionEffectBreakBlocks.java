package com.xcompwiz.mystcraft.explosion.effects;

import java.util.Random;

import com.xcompwiz.mystcraft.explosion.ExplosionAdvanced;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ExplosionEffectBreakBlocks extends ExplosionEffect {
	public static ExplosionEffect	noDrop		= new ExplosionEffectBreakBlocks(false);
	public static ExplosionEffect	dropItems	= new ExplosionEffectBreakBlocks(true);

	private boolean					dropitems;

	private ExplosionEffectBreakBlocks(boolean b) {
		dropitems = b;
	}

	@Override
	public void apply(World worldObj, ExplosionAdvanced explosion, BlockPos pos, Random rand, boolean isClient) {
		if (worldObj.isRemote) return;
		IBlockState blockstate = worldObj.getBlockState(pos);

		if (dropitems && blockstate.getBlock().canDropFromExplosion(explosion.toExplosion())) {
			blockstate.getBlock().dropBlockAsItemWithChance(worldObj, pos, blockstate, 0.3F, 0);
		}

		worldObj.setBlockToAir(pos);

		blockstate.getBlock().onBlockExploded(worldObj, pos, explosion.toExplosion());
	}

}
