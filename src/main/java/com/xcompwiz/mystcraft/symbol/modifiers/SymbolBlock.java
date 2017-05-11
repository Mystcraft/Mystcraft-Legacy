package com.xcompwiz.mystcraft.symbol.modifiers;

import com.xcompwiz.mystcraft.api.symbol.BlockDescriptor;
import com.xcompwiz.mystcraft.api.symbol.ModifierUtils;
import com.xcompwiz.mystcraft.api.word.WordData;
import com.xcompwiz.mystcraft.api.world.AgeDirector;
import com.xcompwiz.mystcraft.symbol.SymbolBase;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;

public class SymbolBlock extends SymbolBase {

	private BlockDescriptor	blockDescriptor;
	private String			displayName;

	public SymbolBlock(BlockDescriptor block, String word) {
		super("ModMat_" + getBlockStateKey(block.blockstate));
		this.blockDescriptor = block;
		this.setWords(new String[] { WordData.Modifier, WordData.Constraint, word, identifier });
		this.displayName = formatted(block);
	}

	private static String getBlockStateKey(IBlockState blockstate) {
		return blockstate.getBlock().getRegistryName().toString() + "_" + blockstate.getBlock().getMetaFromState(blockstate);
	}

	//TODO: Make into a helper somewhere
	private static String formatted(BlockDescriptor blockDescriptor) {
		IBlockState blockstate = blockDescriptor.blockstate;
		String name = I18n.format(blockstate);
		if (name.endsWith("Block")) {
			name = name.substring(0, name.length() - "Block".length()).trim();
		}
		name = I18n.format("myst.symbol.block.wrapper", name);
		return name;
	}

	@Override
	public void registerLogic(AgeDirector controller, long seed) {
		ModifierUtils.pushBlock(controller, blockDescriptor);
	}

	@Override
	public String displayName() {
		return displayName;
	}
}
