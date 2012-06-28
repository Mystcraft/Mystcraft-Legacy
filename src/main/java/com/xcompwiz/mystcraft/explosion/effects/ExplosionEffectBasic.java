package com.xcompwiz.mystcraft.explosion.effects;

import java.util.Random;

import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;

import com.xcompwiz.mystcraft.explosion.ExplosionAdvanced;

public class ExplosionEffectBasic extends ExplosionEffect {

	public static ExplosionEffect	instance	= new ExplosionEffectBasic();

	private ExplosionEffectBasic() {}

	@Override
	public void apply(World worldObj, ExplosionAdvanced explosion, ChunkPosition pos, Random rand, boolean isClient) {
		int x = pos.chunkPosX;
		int y = pos.chunkPosY;
		int z = pos.chunkPosZ;

		if (isClient) {
			double var8 = (x + worldObj.rand.nextFloat());
			double var10 = (y + worldObj.rand.nextFloat());
			double var12 = (z + worldObj.rand.nextFloat());
			double var14 = var8 - explosion.explosionX;
			double var16 = var10 - explosion.explosionY;
			double var18 = var12 - explosion.explosionZ;
			double var20 = MathHelper.sqrt_double(var14 * var14 + var16 * var16 + var18 * var18);
			var14 /= var20;
			var16 /= var20;
			var18 /= var20;
			double var22 = 0.5D / (var20 / explosion.explosionSize + 0.1D);
			var22 *= (worldObj.rand.nextFloat() * worldObj.rand.nextFloat() + 0.3F);
			var14 *= var22;
			var16 *= var22;
			var18 *= var22;
			worldObj.spawnParticle("explode", (var8 + explosion.explosionX * 1.0D) / 2.0D, (var10 + explosion.explosionY * 1.0D) / 2.0D, (var12 + explosion.explosionZ * 1.0D) / 2.0D, var14, var16, var18);
			worldObj.spawnParticle("smoke", var8, var10, var12, var14, var16, var18);
		}
	}

}
