package com.xcompwiz.mystcraft.client.entityfx;

import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.particle.EntityFX;

public class ParticleProviderLink implements ParticleProvider {

	@Override
	public EntityFX createParticle(WorldClient theWorld, double x, double y, double z, double motionX, double motionY, double motionZ) {
		return new EntityFXLink(theWorld, x, y, z, motionX, motionY, motionZ, 3.0F);
	}

}
