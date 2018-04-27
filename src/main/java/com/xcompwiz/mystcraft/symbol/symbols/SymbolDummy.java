package com.xcompwiz.mystcraft.symbol.symbols;

import com.xcompwiz.mystcraft.api.world.AgeDirector;
import com.xcompwiz.mystcraft.symbol.SymbolBase;
import net.minecraft.util.ResourceLocation;

public class SymbolDummy extends SymbolBase {

	private int	instability;

	public SymbolDummy(ResourceLocation identifier) {
		super(identifier);
	}

	public SymbolDummy(ResourceLocation identifier, int instability) {
		super(identifier);
		this.instability = instability;
	}

	@Override
	public int instabilityModifier(int count) {
		return instability;
	}

	@Override
	public void registerLogic(AgeDirector controller, long seed) {}

	@Override
	public boolean generatesConfigOption() {
		return true;
	}
}
