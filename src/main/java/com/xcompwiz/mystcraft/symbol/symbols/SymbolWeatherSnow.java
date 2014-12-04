package com.xcompwiz.mystcraft.symbol.symbols;

import com.xcompwiz.mystcraft.api.world.IAgeController;
import com.xcompwiz.mystcraft.symbol.SymbolBase;
import com.xcompwiz.mystcraft.symbol.WeatherControllerToggleable;

public class SymbolWeatherSnow extends SymbolBase {

	public SymbolWeatherSnow() {}

	@Override
	public void registerLogic(IAgeController controller, long seed) {
		controller.registerInterface(new WeatherController());
	}

	@Override
	public String identifier() {
		return "WeatherSnow";
	}

	private static class WeatherController extends WeatherControllerToggleable {
		@Override
		protected void onEnable() {
			this.rainingStrength = 1;
			this.rainEnabled = true;
			this.snowEnabled = true;
		}

		@Override
		protected void onDisable() {
			this.rainingStrength = 0;
		}

		@Override
		public float getTemperature(float current, int biomeId) {
			if (current > 0.10F) return 0.10F;
			return current;
		}
	}
}
