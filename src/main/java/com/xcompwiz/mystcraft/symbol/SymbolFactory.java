package com.xcompwiz.mystcraft.symbol;

import net.minecraft.block.Block;

import com.xcompwiz.mystcraft.api.symbol.BlockCategory;
import com.xcompwiz.mystcraft.data.ModSymbolsModifiers.BlockModifierContainerObject;
import com.xcompwiz.mystcraft.logging.LoggerUtils;

public class SymbolFactory implements ISymbolFactory {

	// TODO: (API) symbol factory method for block modifier symbols
	public IAgeSymbol createSymbol(Block block, int metadata, String thirdword, int rank, Object[]... objects) {
		BlockModifierContainerObject container = BlockModifierContainerObject.create(thirdword, rank, block, metadata);
		if (objects != null) for (Object[] obj : objects) {
			BlockCategory cat = null;
			Integer c_rank = null;
			if (obj.length != 2) {
				LoggerUtils.warn("Incorrect number of parameters provided in block grammar category set for block %s.", block.getLocalizedName());
				continue;
			}
			if (obj[0] instanceof String) cat = BlockCategory.getBlockCategory((String) obj[1]);
			else if (obj[0] instanceof BlockCategory) cat = (BlockCategory) obj[0];
			if (obj[1] instanceof Integer) c_rank = (Integer) obj[1];
			if (cat == null || c_rank == null) {
				LoggerUtils.warn("Incorrect types of parameters provided in block grammar category set for block %s.", block.getLocalizedName());
				continue;
			}
			container.add(cat, c_rank);
		}
		return container.getSymbol();
	}

}
