package com.xcompwiz.mystcraft.symbol.symbols;

import com.xcompwiz.mystcraft.api.world.AgeDirector;
import com.xcompwiz.mystcraft.symbol.SymbolBase;
import com.xcompwiz.mystcraft.symbol.WeatherControllerToggleable;

import net.minecraft.util.ResourceLocation;

public class SymbolWeatherCloudy extends SymbolBase {

	public SymbolWeatherCloudy(ResourceLocation identifier) {
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
		@Override
		protected void onEnable() {
			this.rainingStrength = 1;
			this.rainEnabled = false;
			this.snowEnabled = false;
		}

		@Override
		protected void onDisable() {
			this.rainingStrength = 0;
		}

	}
}
