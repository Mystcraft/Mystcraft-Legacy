package com.xcompwiz.mystcraft.client.entityfx;

import java.util.HashMap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;

public final class ParticleUtils {

	private static HashMap<String, ParticleProvider>	particlemappings	= new HashMap<>();

	public static void registerParticle(String identifier, ParticleProvider provider) {
		particlemappings.put(identifier, provider);
	}

	public static void spawnParticle(String particle, double x, double y, double z, double motionX, double motionY, double motionZ) {
		Minecraft mc = Minecraft.getMinecraft();
		if (mc.getRenderViewEntity() != null && mc.effectRenderer != null) {
			int particlesetting = mc.gameSettings.particleSetting;

			if (particlesetting == 1 && mc.world.rand.nextInt(3) == 0) {
				particlesetting = 2;
			}
			if (particlesetting > 1) {
				return;
			}

			ParticleProvider provider = particlemappings.get(particle);
			if (provider == null) return;

			double lx = mc.getRenderViewEntity().posX - x;
			double ly = mc.getRenderViewEntity().posY - y;
			double lz = mc.getRenderViewEntity().posZ - z;

			double distsq = 256.0D;

			if (lx * lx + ly * ly + lz * lz > distsq) {
				return;
			}
			Particle effect = provider.createParticle(mc.world, x, y, z, motionX, motionY, motionZ);

			if (effect != null) {
				mc.effectRenderer.addEffect(effect);
			}
		}
	}
}
