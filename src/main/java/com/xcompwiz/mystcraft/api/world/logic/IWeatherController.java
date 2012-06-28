package com.xcompwiz.mystcraft.api.world.logic;

import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import com.xcompwiz.mystcraft.world.storage.IStorageObject;

// FIXME: This interface needs to be revised/reworked completely
//WARNING! If you use this interface, be prepared for your symbol to not update to the next API version
public interface IWeatherController {
	public abstract void setDataObject(IStorageObject infoObj);

	public abstract void updateRaining();

	public abstract void tick(World worldObj, Chunk chunk);

	public abstract void reset();

	public abstract void togglePrecipitation();

	public abstract float getRainingStrength();
	public abstract float getStormStrength();

	public abstract float getTemperature(float current, int biomeId);
	public abstract float getRainfall(float current, int biomeId); // Only used for grass color!?
	public abstract boolean getEnableSnow(boolean current, int biomeId);
	public abstract boolean getEnableRain(boolean current, int biomeId);
}
