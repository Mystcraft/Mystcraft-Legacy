package com.xcompwiz.mystcraft.api.impl.symbol;

import com.xcompwiz.mystcraft.api.hook.SymbolFactory;
import com.xcompwiz.mystcraft.api.impl.APIWrapper;
import com.xcompwiz.mystcraft.api.impl.InternalAPI;
import com.xcompwiz.mystcraft.api.symbol.IAgeSymbol;

import net.minecraft.block.state.IBlockState;

public class SymbolFactoryAPIWrapper extends APIWrapper implements SymbolFactory {

	public SymbolFactoryAPIWrapper(String modname) {
		super(modname);
	}

	@Override
	public IAgeSymbol createSymbol(IBlockState blockstate, String thirdword, int rank, CategoryPair... pairs) {
		return InternalAPI.symbolFact.createSymbol(blockstate, thirdword, rank, pairs);
	}

}
