package com.xcompwiz.mystcraft.api.world.logic;

import com.xcompwiz.mystcraft.api.util.Color;

public interface ICloudColorProvider {
	/**
	 * Returns a color based on the provided information. Note that the produced color is dynamic, so this type of element can use gradients.
	 * @param time The current time in the dimension
	 * @param celestial_angle The "celestial angle" of the brightest object in the sky
	 * @return A color object which will be averaged together with all applied CLoudColorProviders
	 */
	public abstract Color getCloudColor(float time, float celestial_angle);
}
