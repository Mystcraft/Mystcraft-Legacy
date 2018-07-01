package com.xcompwiz.mystcraft.symbol.symbols;

import java.util.Random;

import com.xcompwiz.mystcraft.api.world.AgeDirector;
import com.xcompwiz.mystcraft.symbol.SymbolBase;
import com.xcompwiz.mystcraft.symbol.WeatherControllerToggleable;

import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class SymbolWeatherStorm extends SymbolBase {

	public SymbolWeatherStorm(ResourceLocation identifier) {
		super(identifier);
	}

	@Override
	public void registerLogic(AgeDirector controller, long seed) {
		controller.registerInterface(new WeatherController());
	}

	@Override
	public boolean generatesConfigOption() {
		return true;
	}

	private static class WeatherController extends WeatherControllerToggleable {

		private Random random = new Random();
		private int updateLCG = (random).nextInt();

		@Override
		protected void onEnable() {
			this.rainingStrength = 1;
			this.thunderingStrength = 1;
			this.rainEnabled = true;
			this.snowEnabled = false;
		}

		@Override
		protected void onDisable() {
			this.rainingStrength = 0;
			this.thunderingStrength = 0;
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

				if (canLightningStrikeAt(worldObj, precip)) {
					worldObj.addWeatherEffect(new EntityLightningBolt(worldObj, precip.getX(), precip.getY(), precip.getZ(), false));
				}
			}
		}

		@Override
		public float getTemperature(float current, ResourceLocation biomeId) {
			if (current < 0.20F)
				return 0.20F;
			return current;
		}

		public boolean canLightningStrikeAt(World worldObj, BlockPos pos) {
			if (!worldObj.isRaining()) {
				return false;
			} else if (!worldObj.canSeeSky(pos)) {
				return false;
			} else if (worldObj.getPrecipitationHeight(pos).getY() > pos.getY()) {
				return false;
			}
			return true;
		}
	}
}
