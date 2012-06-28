package com.xcompwiz.mystcraft.symbol.symbols;

import com.xcompwiz.mystcraft.symbol.SymbolBase;
import com.xcompwiz.mystcraft.symbol.WeatherControllerToggleable;
import com.xcompwiz.mystcraft.world.IAgeController;

public class SymbolWeatherOff extends SymbolBase {

	public SymbolWeatherOff() {}

	@Override
	public void registerLogic(IAgeController controller, long seed) {
		controller.registerInterface(new WeatherController());
	}

	@Override
	public String identifier() {
		return "WeatherOff";
	}

	private static class WeatherController extends WeatherControllerToggleable {
		@Override
		protected void onEnable() {
			this.rainingStrength = 0;
		}

		@Override
		protected void onDisable() {
			this.rainingStrength = 1;
		}
	}
}
