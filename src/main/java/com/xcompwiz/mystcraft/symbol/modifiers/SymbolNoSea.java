package com.xcompwiz.mystcraft.symbol.modifiers;

import net.minecraft.init.Blocks;

import com.xcompwiz.mystcraft.api.symbol.BlockCategory;
import com.xcompwiz.mystcraft.api.symbol.BlockDescriptor;
import com.xcompwiz.mystcraft.api.symbol.ModifierUtils;
import com.xcompwiz.mystcraft.api.world.AgeDirector;
import com.xcompwiz.mystcraft.symbol.SymbolBase;

public class SymbolNoSea extends SymbolBase {

	private BlockDescriptor	blockDescriptor;

	public SymbolNoSea() {
		blockDescriptor = new BlockDescriptor(Blocks.air);
		blockDescriptor.setUsable(BlockCategory.SEA, true);
	}

	@Override
	public void registerLogic(AgeDirector controller, long seed) {
		ModifierUtils.pushBlock(controller, blockDescriptor);
	}

	@Override
	public String identifier() {
		return "NoSea";
	}
}
