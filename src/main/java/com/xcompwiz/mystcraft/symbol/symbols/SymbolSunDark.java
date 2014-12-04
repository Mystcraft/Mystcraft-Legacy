package com.xcompwiz.mystcraft.symbol.symbols;

import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.world.World;

import com.xcompwiz.mystcraft.api.world.IAgeController;
import com.xcompwiz.mystcraft.api.world.logic.ISun;
import com.xcompwiz.mystcraft.symbol.SymbolBase;

public class SymbolSunDark extends SymbolBase {

	@Override
	public void registerLogic(IAgeController controller, long seed) {
		controller.registerInterface(new CelestialObject());
	}

	@Override
	public String identifier() {
		return "SunDark";
	}

	private class CelestialObject implements ISun {

		@Override
		public void render(TextureManager eng, World worldObj, float partial) {}

		@Override
		public float getCelestialPeriod(long time, float partialTime) {
			return 0.5F;
		}

		@Override
		public Long getTimeToSunrise(long time) {
			return null;
		}
	}
}
