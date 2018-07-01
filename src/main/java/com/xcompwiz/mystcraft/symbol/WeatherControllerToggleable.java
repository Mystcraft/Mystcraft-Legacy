package com.xcompwiz.mystcraft.symbol;

import java.util.Random;

import com.xcompwiz.mystcraft.api.world.logic.IWeatherController;
import com.xcompwiz.mystcraft.api.world.storage.StorageObject;

import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public abstract class WeatherControllerToggleable implements IWeatherController {
	private StorageObject infoObj;
	private Random random = new Random();
	private int updateLCG = random.nextInt();

	protected double rainingStrength;
	protected double thunderingStrength;
	protected int reset_cooldown = 12000;
	protected Float fixedtemp = null;
	protected Boolean snowEnabled = null;
	protected Boolean rainEnabled = null;

	@Override
	public void setDataObject(StorageObject infoObj) {
		this.infoObj = infoObj;
		rainingStrength = 0.0D;
		thunderingStrength = 0.0D;
		if (infoObj.getBoolean("disabled")) {
			disable();
		} else {
			enable();
		}
	}

	@Override
	public void updateRaining() {
		int reset_counter = infoObj.getInteger("reset_counter");
		infoObj.setInteger("reset_counter", --reset_counter);
		if (reset_counter == 0) {
			enable();
		}
	}

	@Override
	public void tick(World worldObj, Chunk chunk) {
		if (worldObj.isRaining() && worldObj.isThundering() && worldObj.rand.nextInt(100000) == 0) {
			int xBase = chunk.x * 16;
			int zBase = chunk.z * 16;
			updateLCG = updateLCG * 3 + 1013904223;
			int coords = updateLCG >> 2;
			int x = xBase + (coords & 15);
			int z = zBase + (coords >> 8 & 15);
			BlockPos precip = worldObj.getPrecipitationHeight(new BlockPos(x, 0, z));

			if (worldObj.isRainingAt(precip)) {
				worldObj.addWeatherEffect(new EntityLightningBolt(worldObj, precip.getX(), precip.getY(), precip.getZ(), false));
			}
		}
	}

	@Override
	public void reset() {
		enable();
	}

	@Override
	public void togglePrecipitation() {
		if (isDisabled()) {
			enable();
		} else {
			disable();
		}
	}

	final protected boolean isDisabled() {
		return infoObj.getBoolean("disabled");
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
	public float getTemperature(float current, ResourceLocation biomeId) {
		if (this.fixedtemp != null)
			return fixedtemp;
		return current;
	}

	@Override
	public float getRainfall(float current, ResourceLocation biomeId) {
		return current;
	}

	final private void enable() {
		infoObj.setInteger("reset_counter", 0);
		infoObj.setBoolean("disabled", false);
		onEnable();
	}

	final private void disable() {
		infoObj.setInteger("reset_counter", reset_cooldown);
		infoObj.setBoolean("disabled", true);
		fixedtemp = null;
		snowEnabled = null;
		rainEnabled = null;
		onDisable();
	}

	@Override
	public boolean getEnableSnow(boolean current, ResourceLocation biomeId) {
		if (this.snowEnabled != null)
			return snowEnabled;
		return current;
	}

	@Override
	public boolean getEnableRain(boolean current, ResourceLocation biomeId) {
		if (this.rainEnabled != null)
			return rainEnabled;
		return current;
	}

	protected abstract void onEnable();

	protected abstract void onDisable();
}
