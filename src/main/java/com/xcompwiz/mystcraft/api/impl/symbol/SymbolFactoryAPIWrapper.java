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
	@Deprecated
	public IAgeSymbol createSymbol(String ownerModid, IBlockState blockstate, String thirdword, int rank, CategoryPair... pairs) {
		return createSymbol(blockstate, thirdword, rank, pairs);
	}

	@Override
	public IAgeSymbol createSymbol(IBlockState blockState, String thirdword, int rank, CategoryPair... categories) {
		return InternalAPI.symbolFact.createSymbol(blockState, thirdword, rank, categories);
	}

}
