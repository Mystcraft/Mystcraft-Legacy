package com.xcompwiz.mystcraft.world;

import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import com.xcompwiz.mystcraft.world.agedata.AgeData;

public class WorldInfoHelper {

	public static long getWorldTimeAtNextCelestialAngle(WorldServer world, float target) {
		target = Math.abs(target);
		long time = world.getWorldTime();
		float lastangle = world.provider.calculateCelestialAngle(time, 0);
		if (lastangle < 0) return time;
		float angle = lastangle;
		int steprate = 10;
		for (int attempt = 0; attempt < 1000; ++attempt) {
			if (Math.abs(angle - target) < 0.05) {
				return time;
			}
			time += steprate;
			angle = world.provider.calculateCelestialAngle(time, 0);
			if (Math.abs(angle - lastangle) < 0.01) {
				steprate *= 10;
			}
			if (Math.abs(angle - lastangle) > 0.1) {
				steprate /= 2;
			}
			lastangle = angle;
		}
		return world.getWorldTime();
	}

	public static long getWorldNextDawnTime(WorldServer world) {
		return getWorldTimeAtNextCelestialAngle(world, 0.78F);
	}

	public static long getWorldNextDuskTime(WorldServer world) {
		return getWorldTimeAtNextCelestialAngle(world, 0.30F);
	}

	private static AgeData getWorldAgeData(World world) {
		if (!(world.provider instanceof WorldProviderMyst)) return null;
		return ((WorldProviderMyst) world.provider).agedata;
	}

	public static boolean isMystcraftAge(World world) {
		return world.provider instanceof WorldProviderMyst;
	}

	public static boolean isInstabilityEnabled(World world) {
		AgeData data = getWorldAgeData(world);
		if (data == null) return false;
		return data.isInstabilityEnabled();
	}

}
