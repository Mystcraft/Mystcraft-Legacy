package com.xcompwiz.mystcraft.api.impl.symbol;

import net.minecraft.block.Block;

import com.xcompwiz.mystcraft.api.hook.SymbolFactory;
import com.xcompwiz.mystcraft.api.impl.APIWrapper;
import com.xcompwiz.mystcraft.api.impl.InternalAPI;
import com.xcompwiz.mystcraft.api.symbol.IAgeSymbol;

public class SymbolFactoryAPIWrapper extends APIWrapper implements SymbolFactory {

	public SymbolFactoryAPIWrapper(String modname) {
		super(modname);
	}

	@Override
	public IAgeSymbol createSymbol(Block block, int metadata, String thirdword, int rank, CategoryPair... pairs) {
		return InternalAPI.symbolFact.createSymbol(block, metadata, thirdword, rank, pairs);
	}

}
