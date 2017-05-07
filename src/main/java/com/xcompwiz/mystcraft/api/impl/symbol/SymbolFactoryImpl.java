package com.xcompwiz.mystcraft.api.impl.symbol;

import com.xcompwiz.mystcraft.api.hook.SymbolFactory.CategoryPair;
import com.xcompwiz.mystcraft.api.symbol.BlockCategory;
import com.xcompwiz.mystcraft.api.symbol.IAgeSymbol;
import com.xcompwiz.mystcraft.data.ModSymbolsModifiers.BlockModifierContainerObject;

import net.minecraft.block.Block;

public class SymbolFactoryImpl {

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
