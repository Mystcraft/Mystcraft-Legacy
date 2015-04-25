package com.xcompwiz.mystcraft.api.world.logic;

import com.google.common.annotations.Beta;
import com.xcompwiz.mystcraft.api.util.Color;

@Beta
public interface IFogColorProvider {
	/**
	 * Returns a color based on the provided information. Note that the produced color is dynamic, so this type of element can use gradients.
	 * @param celestial_angle The "celestial angle" of the brightest object in the sky
	 * @param partialticks Partial Render ticks
	 * @return A color object which will be averaged together with all applied FogColorProviders
	 */
	public abstract Color getFogColor(float celestial_angle, float partialticks);
}
