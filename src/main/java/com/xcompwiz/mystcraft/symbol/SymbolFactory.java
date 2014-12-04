package com.xcompwiz.mystcraft.symbol;

import net.minecraft.block.Block;

import com.xcompwiz.mystcraft.api.symbol.BlockCategory;
import com.xcompwiz.mystcraft.api.symbol.IAgeSymbol;
import com.xcompwiz.mystcraft.api.symbol.ISymbolFactory;
import com.xcompwiz.mystcraft.data.ModSymbolsModifiers.BlockModifierContainerObject;

public class SymbolFactory implements ISymbolFactory {

	@Override
	public IAgeSymbol createSymbol(Block block, int metadata, String thirdword, int rank, CategoryPair... pairs) {
		BlockModifierContainerObject container = BlockModifierContainerObject.create(thirdword, rank, block, metadata);
		if (pairs != null) for (CategoryPair pair : pairs) {
			BlockCategory cat = pair.category;
			Integer c_rank = pair.rank;
			container.add(cat, c_rank);
		}
		return container.getSymbol();
	}

}
