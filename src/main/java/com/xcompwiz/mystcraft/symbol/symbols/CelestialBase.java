package com.xcompwiz.mystcraft.symbol.symbols;

import com.xcompwiz.mystcraft.api.world.logic.ICelestial;

public abstract class CelestialBase implements ICelestial {

	@Override
	public boolean providesLight() {
		return false;
	}

	@Override
	public float getAltitudeAngle(long time, float partialTime) {
		return 0.5F;
	}

	@Override
	public Long getTimeToDawn(long time) {
		return null;
	}

}
