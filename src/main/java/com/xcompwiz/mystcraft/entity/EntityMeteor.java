package com.xcompwiz.mystcraft.entity;

import io.netty.buffer.ByteBuf;

import java.util.Iterator;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import com.xcompwiz.mystcraft.api.event.MeteorEvent.MetorExplosion;
import com.xcompwiz.mystcraft.api.event.MeteorEvent.MetorImpact;
import com.xcompwiz.mystcraft.api.event.MeteorEvent.MetorSpawn;
import com.xcompwiz.mystcraft.client.audio.MovingSoundMeteor;
import com.xcompwiz.mystcraft.explosion.ExplosionAdvanced;
import com.xcompwiz.mystcraft.explosion.effects.ExplosionEffectBasic;
import com.xcompwiz.mystcraft.explosion.effects.ExplosionEffectBreakBlocks;
import com.xcompwiz.mystcraft.explosion.effects.ExplosionEffectFire;
import com.xcompwiz.mystcraft.explosion.effects.ExplosionEffectPlaceOres;
import com.xcompwiz.mystcraft.network.MPacketExplosion;
import com.xcompwiz.mystcraft.network.MPacketParticles;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityMeteor extends Entity implements IEntityAdditionalSpawnData {

	private byte	inGroundTime;
	private float	scale;
	private int		penetration;
	private boolean	updated	= false;

	public EntityMeteor(World worldObj) {
		super(worldObj);
		this.setScale(1.0F, 0);
		renderDistanceWeight = 80.0D;
		yOffset = 0.0F;
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
			this.worldObj.playSoundEffect(this.posX, this.posY, this.posZ, "ambient.weather.thunder", 10000.0F, 0.8F + this.rand.nextFloat() * 0.2F);
			if (this.worldObj.isRemote) spawnMovingSound();
			updated = true;
		}

		Vec3 var15 = Vec3.createVectorHelper(this.posX, this.posY, this.posZ);
		Vec3 var2 = Vec3.createVectorHelper(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
		MovingObjectPosition var3 = this.worldObj.rayTraceBlocks(var15, var2);
		var15 = Vec3.createVectorHelper(this.posX, this.posY, this.posZ);

		if (var3 != null) {
			++inGroundTime;
			this.onImpact(var3);
		} else {
			inGroundTime = (byte) Math.max(0, --inGroundTime);
		}

		this.posX += this.motionX;
		this.posY += this.motionY;
		this.posZ += this.motionZ;
		float var16 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
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
				this.worldObj.spawnParticle("bubble", this.posX - this.motionX * var18, this.posY - this.motionY * var18, this.posZ - this.motionZ * var18, this.motionX, this.motionY, this.motionZ);
			}
		}

		this.worldObj.spawnParticle("smoke", this.posX, this.posY + 0.5D, this.posZ, 0.0D, 0.0D, 0.0D);
		this.setPosition(this.posX, this.posY, this.posZ);
	}

	@SideOnly(Side.CLIENT)
	private void spawnMovingSound() {
		if (this.worldObj.isRemote) Minecraft.getMinecraft().getSoundHandler().playSound(new MovingSoundMeteor(this));
	}

	/**
	 * Called when this EntityFireball hits a block or entity.
	 */
	protected void onImpact(MovingObjectPosition par1MovingObjectPosition) {
		if (!this.worldObj.isRemote) {
			this.motionY *= 0.90D;
			if (par1MovingObjectPosition.entityHit != null) {
				// par1MovingObjectPosition.entityHit.attackEntityFrom(DamageSource.causeFireballDamage(this, this), 6);
			}

			this.breakBlocksInAABB(this.boundingBox);
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
		World worldObj = entity.worldObj;
		ExplosionAdvanced explosion = new ExplosionAdvanced(worldObj, entity, posX, posY, posZ, power);
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
		Iterator<EntityPlayerMP> var11 = worldObj.playerEntities.iterator();

		while (var11.hasNext()) {
			EntityPlayerMP player = var11.next();

			if (player.getDistanceSq(posX, posY, posZ) < 4096.0D) {
				player.playerNetServerHandler.sendPacket(MPacketExplosion.createPacket(player, explosion));
			}
		}
		MinecraftForge.EVENT_BUS.post(new MetorExplosion(this, explosion.blocks));

		return explosion;
	}

	/**
	 * Breaks all blocks inside the given bounding box.
	 */
	private boolean breakBlocksInAABB(AxisAlignedBB par1AxisAlignedBB) {
		int var2 = MathHelper.floor_double(par1AxisAlignedBB.minX + motionX);
		int var3 = MathHelper.floor_double(par1AxisAlignedBB.minY + motionY);
		int var4 = MathHelper.floor_double(par1AxisAlignedBB.minZ + motionZ);
		int var5 = MathHelper.floor_double(par1AxisAlignedBB.maxX + motionX);
		int var6 = MathHelper.floor_double(par1AxisAlignedBB.maxY + 5 + motionY);
		int var7 = MathHelper.floor_double(par1AxisAlignedBB.maxZ + motionZ);
		boolean brokeblocks = false;

		for (int var10 = var2; var10 <= var5; ++var10) {
			for (int var11 = var3; var11 <= var6; ++var11) {
				for (int var12 = var4; var12 <= var7; ++var12) {
					if (!this.worldObj.isAirBlock(var10, var11, var12)) {
						brokeblocks = true;
						this.worldObj.setBlock(var10, var11, var12, Blocks.air, 0, 3);
					}
				}
			}
		}

		if (brokeblocks) {
			Packet pkt = MPacketParticles.createPacket(this, "largeexplode");
			MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
			if (server != null) server.getConfigurationManager().sendPacketToAllPlayersInDimension(pkt, this.worldObj.provider.dimensionId);
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
