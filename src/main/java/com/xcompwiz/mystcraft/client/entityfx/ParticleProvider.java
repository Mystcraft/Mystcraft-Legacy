package com.xcompwiz.mystcraft.client.entityfx;

import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.particle.Particle;

public interface ParticleProvider {

	public abstract Particle createParticle(WorldClient theWorld, double x, double y, double z, double motionX, double motionY, double motionZ);

}
