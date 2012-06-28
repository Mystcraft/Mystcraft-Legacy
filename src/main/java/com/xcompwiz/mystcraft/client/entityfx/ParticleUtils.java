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
			int var14 = mc.gameSettings.particleSetting;

			if (var14 == 1 && mc.theWorld.rand.nextInt(3) == 0) {
				var14 = 2;
			}
			if (var14 > 1) { return; }

			ParticleProvider provider = particlemappings.get(particle);
			if (provider == null) return;

			double var15 = mc.renderViewEntity.posX - x;
			double var17 = mc.renderViewEntity.posY - y;
			double var19 = mc.renderViewEntity.posZ - z;
			EntityFX effect = null;

			double var22 = 256.0D;

			if (var15 * var15 + var17 * var17 + var19 * var19 > var22) {
				return;
			}
			effect = provider.createParticle(mc.theWorld, x, y, z, motionX, motionY, motionZ);

			if (effect != null) {
				mc.effectRenderer.addEffect(effect);
			}
		}
	}
}
