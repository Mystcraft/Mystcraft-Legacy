package com.xcompwiz.mystcraft.symbol;

import java.util.Random;

import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import com.xcompwiz.mystcraft.api.world.logic.IWeatherController;
import com.xcompwiz.mystcraft.world.storage.IStorageObject;

public abstract class WeatherControllerBase implements IWeatherController {

	private Random			random		= new Random();
	private int				updateLCG	= random.nextInt();
	private IStorageObject	infoObj;

	private double			rainingStrength;
	private double			thunderingStrength;

	protected int			rain_duration;
	protected int			rain_duration_base;
	protected int			rain_cooldown;
	protected int			rain_cooldown_base;
	protected int			thunder_duration;
	protected int			thunder_duration_base;
	protected int			thunder_cooldown;
	protected int			thunder_cooldown_base;

	@Override
	public void setDataObject(IStorageObject infoObj) {
		this.infoObj = infoObj;
		if (infoObj.getBoolean("raining")) {
			rainingStrength = 1.0D;
		}
		if (infoObj.getBoolean("thundering")) {
			thunderingStrength = 1.0D;
		}
	}

	@Override
	public void updateRaining() {
		int thunder_counter = infoObj.getInteger("thunder_counter");
		if (thunder_counter <= 0) {
			if (infoObj.getBoolean("thundering")) {
				thunder_counter = thunder_duration_base;
				if (thunder_duration > 0) {
					thunder_counter += random.nextInt(thunder_duration);
				}
				infoObj.setInteger("thunder_counter", thunder_counter);
			} else {
				thunder_counter = thunder_cooldown_base;
				if (thunder_cooldown > 0) {
					thunder_counter += random.nextInt(thunder_cooldown);
				}
				infoObj.setInteger("thunder_counter", thunder_counter);
			}
		} else {
			infoObj.setInteger("thunder_counter", --thunder_counter);
			if (thunder_counter <= 0) {
				infoObj.setBoolean("thundering", !infoObj.getBoolean("thundering"));
			}
		}

		int rain_counter = infoObj.getInteger("rain_counter");
		if (rain_counter <= 0) {
			if (infoObj.getBoolean("raining")) {
				rain_counter = rain_duration_base;
				if (rain_duration > 0) {
					rain_counter += random.nextInt(rain_duration);
				}
				infoObj.setInteger("rain_counter", rain_counter);
			} else {
				rain_counter = rain_cooldown_base;
				if (rain_cooldown > 0) {
					rain_counter += random.nextInt(rain_cooldown);
				}
				infoObj.setInteger("rain_counter", rain_counter);
			}
		} else {
			infoObj.setInteger("rain_counter", --rain_counter);
			if (rain_counter <= 0) {
				infoObj.setBoolean("raining", !infoObj.getBoolean("raining"));
			}
		}

		if (infoObj.getBoolean("raining")) {
			rainingStrength = rainingStrength + 0.01D;
		} else {
			rainingStrength = rainingStrength - 0.01D;
		}
		if (rainingStrength < 0.0D) {
			rainingStrength = 0.0D;
		}

		if (rainingStrength > 1.0D) {
			rainingStrength = 1.0D;
		}

		if (infoObj.getBoolean("thundering")) {
			thunderingStrength += 0.01D;
		} else {
			thunderingStrength -= 0.01D;
		}
		if (thunderingStrength < 0.0D) {
			thunderingStrength = 0.0D;
		}
		if (thunderingStrength > 1.0D) {
			thunderingStrength = 1.0D;
		}
	}

	@Override
	public void tick(World worldObj, Chunk chunk) {
		if (worldObj.isRaining() && worldObj.isThundering() && worldObj.rand.nextInt(100000) == 0) {
			int xBase = chunk.xPosition * 16;
			int zBase = chunk.zPosition * 16;
			updateLCG = updateLCG * 3 + 1013904223;
			int coords = updateLCG >> 2;
			int x = xBase + (coords & 15);
			int z = zBase + (coords >> 8 & 15);
			int y = worldObj.getPrecipitationHeight(x, z);

			if (worldObj.canLightningStrikeAt(x, y, z)) {
				worldObj.addWeatherEffect(new EntityLightningBolt(worldObj, x, y, z));
			}
		}
	}

	@Override
	public void reset() {
		infoObj.setInteger("rain_counter", 0);
		infoObj.setBoolean("raining", false);
		infoObj.setInteger("thunder_counter", 0);
		infoObj.setBoolean("thundering", false);
	}

	@Override
	public void togglePrecipitation() {
		infoObj.setInteger("rain_counter", 1);
	}

	@Override
	public float getRainingStrength() {
		return (float) rainingStrength;
	}

	@Override
	public float getStormStrength() {
		return (float) thunderingStrength;
	}

	@Override
	public float getTemperature(float current, int biomeId) {
		return current;
	}

	@Override
	public float getRainfall(float current, int biomeId) {
		return current;
	}

	@Override
	public boolean getEnableSnow(boolean current, int biomeId) {
		return current;
	}

	@Override
	public boolean getEnableRain(boolean current, int biomeId) {
		return current;
	}
}
