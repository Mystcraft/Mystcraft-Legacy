package com.xcompwiz.mystcraft.symbol.modifiers;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import com.xcompwiz.mystcraft.api.word.WordData;
import com.xcompwiz.mystcraft.api.world.IAgeController;
import com.xcompwiz.mystcraft.symbol.BlockDescriptor;
import com.xcompwiz.mystcraft.symbol.ModifierUtils;
import com.xcompwiz.mystcraft.symbol.SymbolBase;

public class ModifierBlock extends SymbolBase {

	private BlockDescriptor	blockDescriptor;

	public ModifierBlock(BlockDescriptor block, String word) {
		this.blockDescriptor = block;
		this.setWords(new String[] { WordData.Modifier, WordData.Constraint, word, getBlockAsItem().getUnlocalizedName() });
	}

	@Override
	public void registerLogic(IAgeController controller, long seed) {
		ModifierUtils.pushBlock(controller, blockDescriptor);
	}

	@Override
	public String identifier() {
		return "ModMat_" + getBlockAsItem().getUnlocalizedName();
	}

	@Override
	public String displayName() { //TODO: Localization
		String str = getBlockAsItem().getDisplayName();
		if (str.endsWith("Block")) return str;
		return str + " Block";
	}

	private ItemStack getBlockAsItem() {
		Block block = blockDescriptor.block;
		if (block == null) throw new RuntimeException("Block id " + blockDescriptor.block + " is not a valid block!");
		ItemStack itemstack = new ItemStack(block, 1, blockDescriptor.metadata);
		if (itemstack.getItem() == null) { throw new RuntimeException("Invalid item form for block " + block.getUnlocalizedName() + "with metadata " + blockDescriptor.metadata); }
		return itemstack;
	}
}
