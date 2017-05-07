package com.xcompwiz.mystcraft.api.world.logic;

import com.google.common.annotations.Beta;
import com.xcompwiz.mystcraft.api.world.storage.StorageObject;

import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

// FIXME: This interface needs to be revised/reworked completely
// WARNING! If you use this interface, be prepared for this logic to vanish in a later version of the API
@Beta
public interface IWeatherController {
	public abstract void setDataObject(StorageObject infoObj);

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
