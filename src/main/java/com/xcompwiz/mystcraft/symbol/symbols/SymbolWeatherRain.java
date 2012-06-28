package com.xcompwiz.mystcraft.symbol.symbols;

import com.xcompwiz.mystcraft.symbol.SymbolBase;
import com.xcompwiz.mystcraft.symbol.WeatherControllerToggleable;
import com.xcompwiz.mystcraft.world.IAgeController;

public class SymbolWeatherRain extends SymbolBase {

	public SymbolWeatherRain() {}

	@Override
	public void registerLogic(IAgeController controller, long seed) {
		controller.registerInterface(new WeatherController());
	}

	@Override
	public String identifier() {
		return "WeatherRain";
	}

	private static class WeatherController extends WeatherControllerToggleable {
		@Override
		protected void onEnable() {
			this.rainingStrength = 1;
			this.rainEnabled = true;
			this.snowEnabled = false;
		}

		@Override
		protected void onDisable() {
			this.rainingStrength = 0;
		}

		@Override
		public float getTemperature(float current, int biomeId) {
			if (current < 0.20F) return 0.20F;
			return current;
		}
	}
}
