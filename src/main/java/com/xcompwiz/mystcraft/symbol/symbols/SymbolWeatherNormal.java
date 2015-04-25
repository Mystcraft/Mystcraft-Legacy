package com.xcompwiz.mystcraft.symbol.symbols;

import com.xcompwiz.mystcraft.api.world.AgeDirector;
import com.xcompwiz.mystcraft.symbol.SymbolBase;
import com.xcompwiz.mystcraft.symbol.WeatherControllerBase;

public class SymbolWeatherNormal extends SymbolBase {

	public SymbolWeatherNormal() {}

	@Override
	public void registerLogic(AgeDirector controller, long seed) {
		controller.registerInterface(new WeatherController());
	}

	@Override
	public String identifier() {
		return "WeatherNorm";
	}

	private class WeatherController extends WeatherControllerBase {
		public WeatherController() {
			rain_duration = 12000;
			rain_duration_base = 12000;
			rain_cooldown = 168000;
			rain_cooldown_base = 12000;
			thunder_duration = 12000;
			thunder_duration_base = 3600;
			thunder_cooldown = 168000;
			thunder_cooldown_base = 12000;
		}
	}
}
