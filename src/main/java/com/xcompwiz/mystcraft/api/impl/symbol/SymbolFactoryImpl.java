package com.xcompwiz.mystcraft.api.impl.symbol;

import com.xcompwiz.mystcraft.api.hook.SymbolFactory.CategoryPair;
import com.xcompwiz.mystcraft.api.symbol.BlockCategory;
import com.xcompwiz.mystcraft.api.symbol.IAgeSymbol;
import com.xcompwiz.mystcraft.data.ModSymbolsModifiers.BlockModifierContainerObject;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;

public class SymbolFactoryImpl {

	public IAgeSymbol createSymbol(String ownerModid, Block block, String thirdword, int rank, CategoryPair... pairs) {
		return createSymbol(ownerModid, block.getDefaultState(), thirdword, rank, pairs);
	}

	public IAgeSymbol createSymbol(String ownerModid, IBlockState blockstate, String thirdword, int rank, CategoryPair... pairs) {
		return createSymbol(blockstate, thirdword, rank, pairs);
	}

	public IAgeSymbol createSymbol(IBlockState blockstate, String thirdword, int rank, CategoryPair... pairs) {
		BlockModifierContainerObject container = BlockModifierContainerObject.create(thirdword, rank, blockstate);
		if (pairs != null)
			for (CategoryPair pair : pairs) {
				BlockCategory cat = pair.category;
				Integer c_rank = pair.rank;
				container.add(cat, c_rank);
			}
		return container.getSymbol();
	}

}
