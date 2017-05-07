package com.xcompwiz.mystcraft.symbol.modifiers;

import com.xcompwiz.mystcraft.api.symbol.BlockCategory;
import com.xcompwiz.mystcraft.api.symbol.BlockDescriptor;
import com.xcompwiz.mystcraft.api.symbol.ModifierUtils;
import com.xcompwiz.mystcraft.api.world.AgeDirector;
import com.xcompwiz.mystcraft.symbol.SymbolBase;

import net.minecraft.init.Blocks;

public class SymbolNoSea extends SymbolBase {

	private BlockDescriptor	blockDescriptor;

	public SymbolNoSea(String identifier) {
		super(identifier);
		blockDescriptor = new BlockDescriptor(Blocks.air);
		blockDescriptor.setUsable(BlockCategory.SEA, true);
	}

	@Override
	public void registerLogic(AgeDirector controller, long seed) {
		ModifierUtils.pushBlock(controller, blockDescriptor);
	}
}
