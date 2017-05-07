package com.xcompwiz.mystcraft.symbol.symbols;

import java.util.Random;

import com.xcompwiz.mystcraft.api.world.AgeDirector;
import com.xcompwiz.mystcraft.symbol.SymbolBase;
import com.xcompwiz.mystcraft.symbol.WeatherControllerToggleable;

import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class SymbolWeatherStorm extends SymbolBase {

	public SymbolWeatherStorm(String identifier) {
		super(identifier);
	}

	@Override
	public void registerLogic(AgeDirector controller, long seed) {
		controller.registerInterface(new WeatherController());
	}

	private static class WeatherController extends WeatherControllerToggleable {

		private Random	random		= new Random();
		private int		updateLCG	= (random).nextInt();

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
			int var5 = chunk.xPosition * 16;
			int var6 = chunk.zPosition * 16;
			int var8;
			int var9;
			int var10;
			int var11;

			if (worldObj.isRaining() && worldObj.isThundering() && worldObj.rand.nextInt(100000) == 0) {
				updateLCG = updateLCG * 3 + 1013904223;
				var8 = updateLCG >> 2;
				var9 = var5 + (var8 & 15);
				var10 = var6 + (var8 >> 8 & 15);
				var11 = worldObj.getPrecipitationHeight(var9, var10);

				if (canLightningStrikeAt(worldObj, var9, var11, var10)) {
					worldObj.addWeatherEffect(new EntityLightningBolt(worldObj, var9, var11, var10));
				}
			}
		}

		@Override
		public float getTemperature(float current, int biomeId) {
			if (current < 0.20F) return 0.20F;
			return current;
		}

		public boolean canLightningStrikeAt(World worldObj, int par1, int par2, int par3) {
			if (!worldObj.isRaining()) {
				return false;
			} else if (!worldObj.canBlockSeeTheSky(par1, par2, par3)) {
				return false;
			} else if (worldObj.getPrecipitationHeight(par1, par3) > par2) { return false; }
			return true;
		}
	}
}
