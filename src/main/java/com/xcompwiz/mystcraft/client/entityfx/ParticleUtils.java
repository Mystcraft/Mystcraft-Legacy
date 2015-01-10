package com.xcompwiz.mystcraft.client.entityfx;

import java.util.HashMap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;

public final class ParticleUtils {

	private static HashMap<String, ParticleProvider>	particlemappings	= new HashMap<String, ParticleProvider>();

	public static void registerParticle(String identifier, ParticleProvider provider) {
		particlemappings.put(identifier, provider);
	}

	public static void spawnParticle(String particle, double x, double y, double z, double motionX, double motionY, double motionZ) {
		Minecraft mc = Minecraft.getMinecraft();
		if (mc != null && mc.renderViewEntity != null && mc.effectRenderer != null) {
			int particlesetting = mc.gameSettings.particleSetting;

			if (particlesetting == 1 && mc.theWorld.rand.nextInt(3) == 0) {
				particlesetting = 2;
			}
			if (particlesetting > 1) { return; }

			ParticleProvider provider = particlemappings.get(particle);
			if (provider == null) return;

			double lx = mc.renderViewEntity.posX - x;
			double ly = mc.renderViewEntity.posY - y;
			double lz = mc.renderViewEntity.posZ - z;
			EntityFX effect = null;

			double distsq = 256.0D;

			if (lx * lx + ly * ly + lz * lz > distsq) { return; }
			effect = provider.createParticle(mc.theWorld, x, y, z, motionX, motionY, motionZ);

			if (effect != null) {
				mc.effectRenderer.addEffect(effect);
			}
		}
	}
}
