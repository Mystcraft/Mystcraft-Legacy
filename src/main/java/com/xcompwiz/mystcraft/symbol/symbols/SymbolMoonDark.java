package com.xcompwiz.mystcraft.symbol.symbols;

import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.world.World;

import com.xcompwiz.mystcraft.api.world.AgeDirector;
import com.xcompwiz.mystcraft.api.world.logic.IMoon;
import com.xcompwiz.mystcraft.symbol.SymbolBase;

public class SymbolMoonDark extends SymbolBase {

	@Override
	public void registerLogic(AgeDirector controller, long seed) {
		controller.registerInterface(new CelestialObject());
	}

	@Override
	public String identifier() {
		return "MoonDark";
	}

	private class CelestialObject implements IMoon {

		@Override
		public void render(TextureManager eng, World worldObj, float partial) {}
	}
}
