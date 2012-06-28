package com.xcompwiz.mystcraft.symbol.modifiers;

import net.minecraft.init.Blocks;

import com.xcompwiz.mystcraft.api.symbol.BlockCategory;
import com.xcompwiz.mystcraft.symbol.BlockDescriptor;
import com.xcompwiz.mystcraft.symbol.ModifierUtils;
import com.xcompwiz.mystcraft.symbol.SymbolBase;
import com.xcompwiz.mystcraft.world.IAgeController;

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
