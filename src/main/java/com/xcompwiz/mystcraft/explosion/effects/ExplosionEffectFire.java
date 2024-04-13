package com.xcompwiz.mystcraft.explosion.effects;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;

import com.xcompwiz.mystcraft.explosion.ExplosionAdvanced;

public class ExplosionEffectFire extends ExplosionEffect {

	public static ExplosionEffect	instance	= new ExplosionEffectFire();

	private ExplosionEffectFire() {}

	@Override
	public void apply(World worldObj, ExplosionAdvanced explosion, ChunkPosition pos, Random rand, boolean isClient) {
		if (worldObj.isRemote) return;
		int x = pos.chunkPosX;
		int y = pos.chunkPosY;
		int z = pos.chunkPosZ;

		Block block = worldObj.getBlock(x, y - 1, z);
		if (block.isOpaqueCube() && rand.nextInt(3) == 0) {
			worldObj.setBlock(x, y, z, Blocks.fire, 0, 3);
		}
	}

}
