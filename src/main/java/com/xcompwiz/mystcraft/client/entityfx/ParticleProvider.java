package com.xcompwiz.mystcraft.client.entityfx;

import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.particle.EntityFX;

public interface ParticleProvider {

	public abstract EntityFX createParticle(WorldClient theWorld, double x, double y, double z, double motionX, double motionY, double motionZ);

}
