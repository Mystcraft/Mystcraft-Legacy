package com.xcompwiz.mystcraft.api.world.logic;

import com.xcompwiz.mystcraft.api.util.Color;

import net.minecraft.entity.Entity;
import net.minecraft.world.biome.BiomeGenBase;

public interface IDynamicColorProvider {
	public final static String	CLOUD	= "cloud";
	public final static String	FOG		= "fog";
	public final static String	SKY		= "sky";

	/**
	 * Returns a color based on the provided information. Note that the produced color is dynamic, so this type of element can use gradients. Returning null has
	 * no effect.
	 * @param entity The viewing entity
	 * @param biome The biome the entity is in
	 * @param time The current time in the dimension
	 * @param celestial_angle The "celestial angle" of the brightest object in the sky
	 * @param partialtick Partial Render tick
	 * @return A color object which will be averaged together with all applied SkyColorProviders
	 */
	public abstract Color getColor(Entity entity, BiomeGenBase biome, float time, float celestial_angle, float partialtick);
}
