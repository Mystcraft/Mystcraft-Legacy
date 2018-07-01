package com.xcompwiz.mystcraft.client.entityfx;

import java.util.Random;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class EntityFXLink extends Particle {

	Random rand;
	private float tempParticleScale;

	public EntityFXLink(World worldObj, double x, double y, double z, double motionX, double motionY, double motionZ) {
		super(worldObj, x, y, z, motionX, motionY, motionZ);
	}

	public EntityFXLink(World par1World, double par2, double par4, double par6, double par8, double par10, double par12, float par14) {
		super(par1World, par2, par4, par6, 0.0D, 0.0D, 0.0D);
		this.motionX *= 0.10000000149011612D;
		this.motionY *= 0.10000000149011612D;
		this.motionZ *= 0.10000000149011612D;
		this.motionX += par8;
		this.motionY += par10;
		this.motionZ += par12;
		this.particleRed = this.particleGreen = this.particleBlue = (float) (Math.random() * 0.30000001192092896D);
		this.particleScale *= 0.75F;
		this.particleScale *= par14;
		this.tempParticleScale = this.particleScale;
		this.particleMaxAge = (int) (8.0D / (Math.random() * 0.8D + 0.2D));
		this.particleMaxAge = (int) (this.particleMaxAge * par14);
		rand = new Random();
	}

	@Override
	public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
		float var8 = (this.particleAge + partialTicks) / this.particleMaxAge * 32.0F;

		if (var8 < 0.0F) {
			var8 = 0.0F;
		}

		if (var8 > 1.0F) {
			var8 = 1.0F;
		}

		this.particleScale = this.tempParticleScale * var8;
		super.renderParticle(buffer, entityIn, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
	}

	/**
	 * Called to update the entity's position/logic.
	 */
	@Override
	public void onUpdate() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;

		if (this.particleAge++ >= this.particleMaxAge) {
			setExpired();
		}

		this.setParticleTextureIndex(7 - this.particleAge * 8 / this.particleMaxAge);
		this.motionX += 0.005D * rand.nextGaussian();
		this.motionY += 0.001D;
		this.motionZ += 0.005D * rand.nextGaussian();
		this.move(this.motionX, this.motionY, this.motionZ);

		if (this.posY == this.prevPosY) {
			this.motionX *= 1.1D;
			this.motionZ *= 1.1D;
		}

		this.motionX *= 0.9599999785423279D;
		this.motionY *= 0.9599999785423279D;
		this.motionZ *= 0.9599999785423279D;

		if (this.onGround) {
			this.motionX *= 0.699999988079071D;
			this.motionZ *= 0.699999988079071D;
		}
	}
}
