package com.xcompwiz.mystcraft.api.world.logic;

import com.xcompwiz.mystcraft.symbol.Color;

public interface IFogColorProvider {
	public abstract Color getFogColor(float celestial_angle, float partialticks);
}
