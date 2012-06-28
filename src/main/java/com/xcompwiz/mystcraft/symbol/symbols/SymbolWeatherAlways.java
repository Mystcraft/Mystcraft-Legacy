package com.xcompwiz.mystcraft.symbol.symbols;

import com.xcompwiz.mystcraft.symbol.SymbolBase;
import com.xcompwiz.mystcraft.symbol.WeatherControllerToggleable;
import com.xcompwiz.mystcraft.world.IAgeController;

public class SymbolWeatherAlways extends SymbolBase {

	public SymbolWeatherAlways() {}

	@Override
	public void registerLogic(IAgeController controller, long seed) {
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
