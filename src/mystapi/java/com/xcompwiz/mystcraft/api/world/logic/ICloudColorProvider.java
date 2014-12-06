package com.xcompwiz.mystcraft.api.world.logic;

import com.xcompwiz.mystcraft.api.util.Color;

public interface ICloudColorProvider {
	public abstract Color getCloudColor(float time, float celestial_angle);
}