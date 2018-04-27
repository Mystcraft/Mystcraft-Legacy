package com.xcompwiz.mystcraft.symbol.symbols;

import com.xcompwiz.mystcraft.api.world.AgeDirector;
import com.xcompwiz.mystcraft.symbol.SymbolBase;
import net.minecraft.util.ResourceLocation;

public class SymbolAntiPvP extends SymbolBase {

	public SymbolAntiPvP(ResourceLocation identifier) {
		super(identifier);
	}

	@Override
	public void registerLogic(AgeDirector controller, long seed) {
		controller.setPvPEnabled(false);
	}

	@Override
	public boolean generatesConfigOption() {
		return true;
	}
}
