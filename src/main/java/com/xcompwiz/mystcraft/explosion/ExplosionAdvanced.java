package com.xcompwiz.mystcraft.explosion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

import com.xcompwiz.mystcraft.explosion.effects.ExplosionEffect;
import com.xcompwiz.mystcraft.explosion.effects.ExplosionEffectBasic;
import com.xcompwiz.mystcraft.explosion.effects.ExplosionEffectBreakBlocks;
import com.xcompwiz.mystcraft.explosion.effects.ExplosionEffectFire;
import com.xcompwiz.mystcraft.explosion.effects.ExplosionEffectPlaceOres;

public class ExplosionAdvanced {
	private int										field_77289_h	= 16;
	private Random									explosionRNG	= new Random();
	private World									worldObj;
	public double									explosionX;
	public double									explosionY;
	public double									explosionZ;
	public Entity									exploder;
	public float									explosionSize;
	public List<ChunkPosition>						blocks			= new ArrayList<ChunkPosition>();
	public List<ExplosionEffect>					effects			= new ArrayList<ExplosionEffect>();
	private Map<Entity, Vec3>						players			= new HashMap<Entity, Vec3>();

	private static HashMap<Byte, ExplosionEffect>	effectmap		= new HashMap<Byte, ExplosionEffect>();
	private static HashMap<ExplosionEffect, Byte>	effectid		= new HashMap<ExplosionEffect, Byte>();
	private static byte								nextId			= 0;

	public static void registerEffect(ExplosionEffect effect) {
		if (effectmap.containsKey(nextId)) throw new RuntimeException("Mystcraft has run ut of explosion effect IDs!  Contact XCompWiz and ask for more!");
		effectmap.put(nextId, effect);
		effectid.put(effect, nextId++);
	}

	static {
		registerEffect(ExplosionEffectBasic.instance);
		registerEffect(ExplosionEffectBreakBlocks.dropItems);
		registerEffect(ExplosionEffectBreakBlocks.noDrop);
		registerEffect(ExplosionEffectFire.instance);
		registerEffect(ExplosionEffectPlaceOres.instance);
	}

	public static ExplosionEffect getEffectById(byte id) {
		return effectmap.get(id);
	}

	public static byte getEffectId(ExplosionEffect effect) {
		return effectid.get(effect);
	}

	public ExplosionAdvanced(World worldObj, Entity entity, double x, double y, double z, float size) {
		this.worldObj = worldObj;
		this.exploder = entity;
		this.explosionSize = size;
		this.explosionX = x;
		this.explosionY = y;
		this.explosionZ = z;
	}

	/**
	 * Does the first part of the explosion
	 */
	public void doExplosionA() {
		float explosionSize = this.explosionSize;
		HashSet<ChunkPosition> blocks = new HashSet<ChunkPosition>();

		for (int x = 0; x < this.field_77289_h; ++x) {
			for (int y = 0; y < this.field_77289_h; ++y) {
				for (int z = 0; z < this.field_77289_h; ++z) {
					if (x == 0 || x == this.field_77289_h - 1 || y == 0 || y == this.field_77289_h - 1 || z == 0 || z == this.field_77289_h - 1) {
						double dx = (x / (this.field_77289_h - 1.0F) * 2.0F - 1.0F);
						double dy = (y / (this.field_77289_h - 1.0F) * 2.0F - 1.0F);
						double dz = (z / (this.field_77289_h - 1.0F) * 2.0F - 1.0F);
						double length = Math.sqrt(dx * dx + dy * dy + dz * dz);
						dx /= length;
						dy /= length;
						dz /= length;
						float power = explosionSize * (0.7F + this.worldObj.rand.nextFloat() * 0.6F);
						double posX = this.explosionX;
						double posY = this.explosionY;
						double posZ = this.explosionZ;

						for (float factor = 0.3F; power > 0.0F; power -= factor * 0.75F) {
							int blockX = MathHelper.floor_double(posX);
							int blockY = MathHelper.floor_double(posY);
							int blockZ = MathHelper.floor_double(posZ);
							Block block = this.worldObj.getBlock(blockX, blockY, blockZ);

							power -= (block.getExplosionResistance(this.exploder, worldObj, blockX, blockY, blockZ, explosionX, explosionY, explosionZ) + 0.3F) * factor;

							if (power > 0.0F) {
								blocks.add(new ChunkPosition(blockX, blockY, blockZ));
							}

							posX += dx * factor;
							posY += dy * factor;
							posZ += dz * factor;
						}
					}
				}
			}
		}

		this.blocks.addAll(blocks);
		explosionSize *= 2.0F;
		int minX = MathHelper.floor_double(this.explosionX - explosionSize - 1.0D);
		int maxX = MathHelper.floor_double(this.explosionX + explosionSize + 1.0D);
		int minY = MathHelper.floor_double(this.explosionY - explosionSize - 1.0D);
		int maxY = MathHelper.floor_double(this.explosionY + explosionSize + 1.0D);
		int minZ = MathHelper.floor_double(this.explosionZ - explosionSize - 1.0D);
		int maxZ = MathHelper.floor_double(this.explosionZ + explosionSize + 1.0D);
		List<Entity> entities = this.worldObj.getEntitiesWithinAABBExcludingEntity(this.exploder, AxisAlignedBB.getBoundingBox(minX, minY, minZ, maxX, maxY, maxZ));
		Vec3 explosionVec = Vec3.createVectorHelper(this.explosionX, this.explosionY, this.explosionZ);

		for (Entity entity : entities) {
			double dist = entity.getDistance(this.explosionX, this.explosionY, this.explosionZ) / explosionSize;

			if (dist < 1.0D) {
				double dx = entity.posX - this.explosionX;
				double dy = entity.posY + entity.getEyeHeight() - this.explosionY;
				double dz = entity.posZ - this.explosionZ;
				double length = MathHelper.sqrt_double(dx * dx + dy * dy + dz * dz);

				if (length != 0.0D) {
					dx /= length;
					dy /= length;
					dz /= length;
					double reduction = this.worldObj.getBlockDensity(explosionVec, entity.boundingBox);
					double force = (1.0D - dist) * reduction;
					entity.attackEntityFrom(getDamageSource(this), (int) ((force * force + force) / 2.0D * 8.0D * explosionSize + 1.0D));
					entity.motionX += dx * force;
					entity.motionY += dy * force;
					entity.motionZ += dz * force;

					if (entity instanceof EntityPlayer) {
						this.players.put(entity, Vec3.createVectorHelper(dx * force, dy * force, dz * force));
					}
				}
			}
		}
	}

	public static DamageSource getDamageSource(ExplosionAdvanced explosion) {
		return explosion != null && explosion.exploder != null ? (new EntityDamageSource("explosion.player", explosion.exploder)).setDifficultyScaled().setExplosion() : DamageSource.setExplosionSource(null);
	}

	/**
	 * Does the second part of the explosion (sound, particles, drop spawn)
	 */
	public void doExplosionB(boolean isClient) {
		this.worldObj.playSoundEffect(this.explosionX, this.explosionY, this.explosionZ, "random.explode", 4.0F, (1.0F + (this.worldObj.rand.nextFloat() - this.worldObj.rand.nextFloat()) * 0.2F) * 0.7F);
		this.worldObj.spawnParticle("hugeexplosion", this.explosionX, this.explosionY, this.explosionZ, 0.0D, 0.0D, 0.0D);

		for (ChunkPosition pos : blocks) {
			for (ExplosionEffect effect : effects) {
				effect.apply(worldObj, this, pos, explosionRNG, isClient);
			}
		}
	}

	public Map<Entity, Vec3> getPlayerMap() {
		return this.players;
	}

	public Explosion toExplosion() {
		return new Explosion(worldObj, exploder, explosionX, explosionX, explosionX, explosionSize);
	}
}
