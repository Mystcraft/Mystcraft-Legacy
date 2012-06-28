package com.xcompwiz.mystcraft.symbol;

import net.minecraft.world.gen.feature.WorldGenerator;


public class SymbolFactory implements ISymbolFactory {

	// TODO: symbol factory method for world populators
	public IAgeSymbol createSymbol(String id, Class<WorldGenerator> gen) {
		return (new SymbolWorldGen(id, gen));
	}

	// TODO: symbol factory method for block modifier symbols
	public IAgeSymbol createSymbol(BlockDescriptor block, String thirdword, float grammarWeight) {
		return null;//(new ModifierBlock(block, thirdword, grammarWeight));
	}

}
