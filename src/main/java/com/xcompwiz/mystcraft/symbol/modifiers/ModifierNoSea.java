package com.xcompwiz.mystcraft.symbol.modifiers;

import net.minecraft.init.Blocks;

import com.xcompwiz.mystcraft.api.symbol.BlockCategory;
import com.xcompwiz.mystcraft.api.symbol.BlockDescriptor;
import com.xcompwiz.mystcraft.api.symbol.ModifierUtils;
import com.xcompwiz.mystcraft.api.world.IAgeController;
import com.xcompwiz.mystcraft.symbol.SymbolBase;

public class ModifierNoSea extends SymbolBase {

	private BlockDescriptor	blockDescriptor;

	public ModifierNoSea() {
		blockDescriptor = new BlockDescriptor(Blocks.air);
		blockDescriptor.setUsable(BlockCategory.SEA, true);
	}

	@Override
	public void registerLogic(IAgeController controller, long seed) {
		ModifierUtils.pushBlock(controller, blockDescriptor);
	}

	@Override
	public String identifier() {
		return "NoSea";
	}
}
