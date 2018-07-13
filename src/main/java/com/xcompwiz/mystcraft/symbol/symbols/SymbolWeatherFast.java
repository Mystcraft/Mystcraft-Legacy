package com.xcompwiz.mystcraft.symbol.symbols;

import com.xcompwiz.mystcraft.api.world.AgeDirector;
import com.xcompwiz.mystcraft.symbol.SymbolBase;
import com.xcompwiz.mystcraft.symbol.WeatherControllerBase;

import net.minecraft.util.ResourceLocation;

public class SymbolWeatherFast extends SymbolBase {

	public SymbolWeatherFast(ResourceLocation identifier) {
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

	private class WeatherController extends WeatherControllerBase {
		public WeatherController() {
			rain_duration = 6000;
			rain_duration_base = 6000;
			rain_cooldown = 84000;
			rain_cooldown_base = 6000;
			thunder_duration = 6000;
			thunder_duration_base = 1800;
			thunder_cooldown = 84000;
			thunder_cooldown_base = 6000;
		}
	}
}
