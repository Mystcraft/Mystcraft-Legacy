package com.xcompwiz.mystcraft.entity;

import com.xcompwiz.mystcraft.api.event.MeteorEvent.MetorExplosion;
import com.xcompwiz.mystcraft.api.event.MeteorEvent.MetorImpact;
import com.xcompwiz.mystcraft.api.event.MeteorEvent.MetorSpawn;
import com.xcompwiz.mystcraft.client.audio.MovingSoundMeteor;
import com.xcompwiz.mystcraft.explosion.ExplosionAdvanced;
import com.xcompwiz.mystcraft.explosion.effects.ExplosionEffectBasic;
import com.xcompwiz.mystcraft.explosion.effects.ExplosionEffectBreakBlocks;
import com.xcompwiz.mystcraft.explosion.effects.ExplosionEffectFire;
import com.xcompwiz.mystcraft.explosion.effects.ExplosionEffectPlaceOres;
import com.xcompwiz.mystcraft.network.MystcraftPacketHandler;
import com.xcompwiz.mystcraft.network.packet.MPacketExplosion;
import com.xcompwiz.mystcraft.network.packet.MPacketParticles;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityMeteor extends Entity implements IEntityAdditionalSpawnData {

	private byte	inGroundTime;
	private float	scale;
	private int		penetration;
	private boolean	updated	= false;

	public EntityMeteor(World worldObj) {
		super(worldObj);
		this.setScale(1.0F, 0);
	}

	public EntityMeteor(World worldObj, float scale, int penetration, double x, double y, double z, double dx, double dy, double dz) {
		this(worldObj);
		this.setScale(scale, penetration);
		setPosition(x, y, z);
		this.motionX = dx;
		this.motionY = dy;
		this.motionZ = dz;
		inGroundTime = 0;
		MinecraftForge.EVENT_BUS.post(new MetorSpawn(this));
	}

	@Override
	protected void entityInit() {}

	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {
		this.isDead = true;
		inGroundTime = compound.getByte("InGround");
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {
		compound.setByte("InGround", inGroundTime);
	}

	@Override
	public void setDead() {
		this.isDead = true;
	}

	/**
	 * Called to update the entity's position/logic.
	 */
	@Override
	public void onUpdate() {
		super.onUpdate();
		this.setFire(1);

		if (!updated) {
			getEntityWorld().playSound(posX, posY, posZ, SoundEvents.ENTITY_LIGHTNING_THUNDER, SoundCategory.WEATHER, 10000.0F, 0.8F + this.rand.nextFloat() * 0.2F, true);
			if (this.getEntityWorld().isRemote) {
				spawnMovingSound();
			}
			updated = true;
		}

		Vec3d var15 = new Vec3d(this.posX, this.posY, this.posZ);
		Vec3d var2 = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
		RayTraceResult var3 = this.getEntityWorld().rayTraceBlocks(var15, var2);
		var15 = new Vec3d(this.posX, this.posY, this.posZ);

		if (var3 != null) {
			++inGroundTime;
			this.onImpact(var3);
		} else {
			inGroundTime = (byte) Math.max(0, --inGroundTime);
		}

		this.posX += this.motionX;
		this.posY += this.motionY;
		this.posZ += this.motionZ;
		float var16 = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
		this.rotationYaw = (float) (Math.atan2(this.motionX, this.motionZ) * 180.0D / Math.PI);

		for (this.rotationPitch = (float) (Math.atan2(this.motionY, var16) * 180.0D / Math.PI); this.rotationPitch - this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F) {
			;
		}

		while (this.rotationPitch - this.prevRotationPitch >= 180.0F) {
			this.prevRotationPitch += 360.0F;
		}

		while (this.rotationYaw - this.prevRotationYaw < -180.0F) {
			this.prevRotationYaw -= 360.0F;
		}

		while (this.rotationYaw - this.prevRotationYaw >= 180.0F) {
			this.prevRotationYaw += 360.0F;
		}

		this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2F;
		this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2F;

		if (this.isInWater()) {
			for (int var19 = 0; var19 < 4; ++var19) {
				float var18 = 0.25F;
				this.getEntityWorld().spawnParticle(EnumParticleTypes.WATER_BUBBLE, this.posX - this.motionX * var18, this.posY - this.motionY * var18, this.posZ - this.motionZ * var18, this.motionX, this.motionY, this.motionZ);
			}
		}

		this.getEntityWorld().spawnParticle(EnumParticleTypes.SMOKE_NORMAL, this.posX, this.posY + 0.5D, this.posZ, 0.0D, 0.0D, 0.0D);
		this.setPosition(this.posX, this.posY, this.posZ);
	}

	@SideOnly(Side.CLIENT)
	private void spawnMovingSound() {
		if (this.getEntityWorld().isRemote) {
			Minecraft.getMinecraft().getSoundHandler().playSound(new MovingSoundMeteor(this));
		}
	}

	protected void onImpact(RayTraceResult rtr) {
		if (!this.getEntityWorld().isRemote) {
			this.motionY *= 0.90D;
			if (rtr.entityHit != null) {
				// par1MovingObjectPosition.entityHit.attackEntityFrom(DamageSource.causeFireballDamage(this, this), 6);
			}

			this.breakBlocksInAABB(this.getCollisionBoundingBox());
			if (inGroundTime >= penetration) {
				newExplosion(this, this.posX, this.posY, this.posZ, 5.0F, false, true);
				newExplosion(this, this.posX, this.posY - scale / 10, this.posZ, scale, false, true);
				newExplosion(this, this.posX, this.posY - scale / 5, this.posZ, scale * 2, false, true);
				newExplosion(this, this.posX, this.posY - scale * 2 / 5, this.posZ, scale, false, true);
				newExplosion(this, this.posX + scale * 4 / 5, this.posY, this.posZ, scale, true, false);
				newExplosion(this, this.posX - scale * 4 / 5, this.posY, this.posZ, scale, true, false);
				newExplosion(this, this.posX, this.posY, this.posZ + scale * 4 / 5, scale, true, false);
				newExplosion(this, this.posX, this.posY, this.posZ - scale * 4 / 5, scale, true, false);
				MinecraftForge.EVENT_BUS.post(new MetorImpact(this));
				this.setDead();
			}
		}
	}

	private ExplosionAdvanced newExplosion(Entity entity, double posX, double posY, double posZ, float power, boolean isFlaming, boolean addOres) {
		ExplosionAdvanced explosion = new ExplosionAdvanced(entity.world, entity, posX, posY, posZ, power);
		explosion.effects.add(ExplosionEffectBasic.instance);
		explosion.effects.add(ExplosionEffectBreakBlocks.noDrop);
		if (isFlaming) {
			explosion.effects.add(ExplosionEffectFire.instance);
		}
		if (addOres) {
			explosion.effects.add(ExplosionEffectPlaceOres.instance);
		}
		explosion.doExplosionA();
		explosion.doExplosionB(false);
		MystcraftPacketHandler.CHANNEL.sendToAllAround(new MPacketExplosion(explosion), new NetworkRegistry.TargetPoint(entity.world.provider.getDimension(), posX, posY, posZ, 64));
		MinecraftForge.EVENT_BUS.post(new MetorExplosion(this, explosion.blocks));

		return explosion;
	}

	/**
	 * Breaks all blocks inside the given bounding box.
	 */
	private boolean breakBlocksInAABB(AxisAlignedBB par1AxisAlignedBB) {
		int var2 = MathHelper.floor(par1AxisAlignedBB.minX + motionX);
		int var3 = MathHelper.floor(par1AxisAlignedBB.minY + motionY);
		int var4 = MathHelper.floor(par1AxisAlignedBB.minZ + motionZ);
		int var5 = MathHelper.floor(par1AxisAlignedBB.maxX + motionX);
		int var6 = MathHelper.floor(par1AxisAlignedBB.maxY + 5 + motionY);
		int var7 = MathHelper.floor(par1AxisAlignedBB.maxZ + motionZ);
		boolean brokeblocks = false;

        BlockPos.PooledMutableBlockPos pool = BlockPos.PooledMutableBlockPos.retain();
        try {
            for (int xx = var2; xx <= var5; ++xx) {
                for (int yy = var3; yy <= var6; ++yy) {
                    for (int zz = var4; zz <= var7; ++zz) {
                        pool.setPos(xx, yy, zz);
                        if(!getEntityWorld().isAirBlock(pool)) {
                            brokeblocks = true;
                            getEntityWorld().setBlockToAir(pool);
                        }
                    }
                }
            }
        } finally {
            pool.release();
        }

		if (brokeblocks) {
			MystcraftPacketHandler.CHANNEL.sendToDimension(new MPacketParticles(posX, posY, posZ, "largeexplode"), world.provider.getDimension());
		}

		return brokeblocks;
	}

	/**
	 * Returns true if other Entities should be prevented from moving through this Entity.
	 */
	@Override
	public boolean canBeCollidedWith() {
		return true;
	}

	@Override
	public float getCollisionBorderSize() {
		return 1.0F;
	}

	public void setScale(float scale, int penetration) {
		this.scale = scale;
		this.penetration = penetration;
		setSize(scale, scale);
	}

	public float getScale() {
		return this.scale;
	}

	@Override
	public void writeSpawnData(ByteBuf data) {
		data.writeFloat(this.scale);
	}

	@Override
	public void readSpawnData(ByteBuf data) {
		setScale(data.readFloat(), 0);
	}
}
