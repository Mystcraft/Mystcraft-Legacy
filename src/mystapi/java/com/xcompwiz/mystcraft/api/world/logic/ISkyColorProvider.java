package com.xcompwiz.mystcraft.api.world.logic;

import net.minecraft.entity.Entity;
import net.minecraft.world.biome.BiomeGenBase;

import com.xcompwiz.mystcraft.api.util.Color;

public interface ISkyColorProvider {
	/**
	 * Returns a color based on the provided information. Note that the produced color is dynamic, so this type of element can use gradients.
	 * @param entity The viewing entity
	 * @param biome The biome the entity is in
	 * @param time The current time in the dimension
	 * @param celestial_angle The "celestial angle" of the brightest object in the sky
	 * @return A color object which will be averaged together with all applied SkyColorProviders
	 */
	public abstract Color getSkyColor(Entity entity, BiomeGenBase biome, float time, float celestial_angle);
}
