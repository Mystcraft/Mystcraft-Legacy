package com.xcompwiz.mystcraft.symbol.symbols;

import com.xcompwiz.mystcraft.api.world.AgeDirector;
import com.xcompwiz.mystcraft.symbol.SymbolBase;

public class SymbolDummy extends SymbolBase {

	private int	instability;

	public SymbolDummy(String identifier) {
		super(identifier);
	}

	public SymbolDummy(String identifier, int instability) {
		super(identifier);
		this.instability = instability;
	}

	@Override
	public int instabilityModifier(int count) {
		return instability;
	}

	@Override
	public void registerLogic(AgeDirector controller, long seed) {}
}
