package com.xcompwiz.mystcraft.symbol.modifiers;

import com.xcompwiz.mystcraft.api.symbol.BlockDescriptor;
import com.xcompwiz.mystcraft.api.symbol.ModifierUtils;
import com.xcompwiz.mystcraft.api.word.WordData;
import com.xcompwiz.mystcraft.api.world.AgeDirector;
import com.xcompwiz.mystcraft.symbol.SymbolBase;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

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
		ItemStack attempt = ItemStack.EMPTY;
		try {
			attempt = blockDescriptor.blockstate.getBlock().getPickBlock(blockDescriptor.blockstate, null, null, BlockPos.ORIGIN, null);
		} catch (Exception ignored) {}
		if(attempt.isEmpty()) {
			Item i = Item.getItemFromBlock(blockDescriptor.blockstate.getBlock());
			if (i != Items.AIR) {
				int meta = blockDescriptor.blockstate.getBlock().getMetaFromState(blockDescriptor.blockstate);
				attempt = new ItemStack(i, 1, meta);
			}
		}
		String name;
		if(attempt.isEmpty()) {
			name = blockDescriptor.blockstate.getBlock().getUnlocalizedName() + ".name";
		} else {
			name = attempt.getUnlocalizedName() + ".name";
		}
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
