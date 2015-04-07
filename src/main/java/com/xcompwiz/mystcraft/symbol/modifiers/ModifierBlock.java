package com.xcompwiz.mystcraft.symbol.modifiers;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import com.xcompwiz.mystcraft.api.symbol.BlockDescriptor;
import com.xcompwiz.mystcraft.api.symbol.ModifierUtils;
import com.xcompwiz.mystcraft.api.word.WordData;
import com.xcompwiz.mystcraft.api.world.IAgeController;
import com.xcompwiz.mystcraft.symbol.SymbolBase;

public class ModifierBlock extends SymbolBase {

	private BlockDescriptor	blockDescriptor;
	private String			displayName;

	public ModifierBlock(BlockDescriptor block, String word) {
		this.blockDescriptor = block;
		this.setWords(new String[] { WordData.Modifier, WordData.Constraint, word, getBlockAsItem(block).getUnlocalizedName() });
		this.displayName = formatted(block);
	}

	//TODO: Make into a helper somewhere
	private static String formatted(BlockDescriptor blockDescriptor) {
		String name = getBlockAsItem(blockDescriptor).getDisplayName();
		if (name.endsWith("Block")) name = name.substring(0, name.length() - "Block".length()).trim();
		name = StatCollector.translateToLocalFormatted("myst.symbol.block.wrapper", name);
		return name;
	}

	//TODO: Make into a helper somewhere
	private static ItemStack getBlockAsItem(BlockDescriptor blockDescriptor) {
		Block block = blockDescriptor.block;
		if (block == null) throw new RuntimeException("Block id " + blockDescriptor.block + " is not a valid block!");
		ItemStack itemstack = new ItemStack(block, 1, blockDescriptor.metadata);
		if (itemstack.getItem() == null) { throw new RuntimeException("Invalid item form for block " + block.getUnlocalizedName() + "with metadata " + blockDescriptor.metadata); }
		return itemstack;
	}

	@Override
	public void registerLogic(IAgeController controller, long seed) {
		ModifierUtils.pushBlock(controller, blockDescriptor);
	}

	@Override
	public String identifier() {
		return "ModMat_" + getBlockAsItem(blockDescriptor).getUnlocalizedName();
	}

	@Override
	public String displayName() {
		return displayName;
	}
}
