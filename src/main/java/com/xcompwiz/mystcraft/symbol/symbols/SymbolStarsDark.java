package com.xcompwiz.mystcraft.symbol.symbols;

import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.world.World;

import com.xcompwiz.mystcraft.api.world.IAgeController;
import com.xcompwiz.mystcraft.api.world.logic.IStarfield;
import com.xcompwiz.mystcraft.symbol.SymbolBase;

public class SymbolStarsDark extends SymbolBase {

	@Override
	public void registerLogic(IAgeController controller, long seed) {
		controller.registerInterface(new CelestialObject());
	}

	@Override
	public String identifier() {
		return "StarsDark";
	}

	private class CelestialObject implements IStarfield {

		@Override
		public void render(TextureManager eng, World worldObj, float partial) {}
	}
}
