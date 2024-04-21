package com.xcompwiz.mystcraft.api.world.logic;

import com.google.common.annotations.Beta;

@Beta
public interface ILightingController {
	public abstract int scaleLighting(int blockLightValue);

	public abstract void generateLightBrightnessTable(float[] lightBrightnessTable);
}
