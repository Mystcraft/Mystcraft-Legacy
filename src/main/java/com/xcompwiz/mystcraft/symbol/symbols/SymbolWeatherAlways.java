package com.xcompwiz.mystcraft.symbol.symbols;

import com.xcompwiz.mystcraft.api.world.AgeDirector;
import com.xcompwiz.mystcraft.symbol.SymbolBase;
import com.xcompwiz.mystcraft.symbol.WeatherControllerToggleable;

public class SymbolWeatherAlways extends SymbolBase {

	public SymbolWeatherAlways() {}

	@Override
	public void registerLogic(AgeDirector controller, long seed) {
		controller.registerInterface(new WeatherController());
	}

	@Override
	public String identifier() {
		return "WeatherOn";
	}

	private static class WeatherController extends WeatherControllerToggleable {
		@Override
		protected void onEnable() {
			this.rainingStrength = 1;
		}

		@Override
		protected void onDisable() {
			this.rainingStrength = 0;
		}
	}
}
