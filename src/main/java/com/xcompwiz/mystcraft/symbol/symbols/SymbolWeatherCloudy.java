package com.xcompwiz.mystcraft.symbol.symbols;

import com.xcompwiz.mystcraft.api.world.IAgeController;
import com.xcompwiz.mystcraft.symbol.SymbolBase;
import com.xcompwiz.mystcraft.symbol.WeatherControllerToggleable;

public class SymbolWeatherCloudy extends SymbolBase {

	public SymbolWeatherCloudy() {}

	@Override
	public void registerLogic(IAgeController controller, long seed) {
		controller.registerInterface(new WeatherController());
	}

	@Override
	public String identifier() {
		return "WeatherCloudy";
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
