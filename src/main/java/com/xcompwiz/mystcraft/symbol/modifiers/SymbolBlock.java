package com.xcompwiz.mystcraft.symbol.modifiers;

import com.xcompwiz.mystcraft.api.symbol.BlockDescriptor;
import com.xcompwiz.mystcraft.api.symbol.ModifierUtils;
import com.xcompwiz.mystcraft.api.word.WordData;
import com.xcompwiz.mystcraft.api.world.AgeDirector;
import com.xcompwiz.mystcraft.symbol.SymbolBase;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public class SymbolBlock extends SymbolBase {

	private BlockDescriptor blockDescriptor;
	private String unlocalizedBlockName;

	public SymbolBlock(BlockDescriptor block, String word) {
		super("ModMat_" + getBlockStateKey(block.blockstate));
		this.blockDescriptor = block;
		this.setWords(new String[] { WordData.Modifier, WordData.Constraint, word, identifier });
		this.unlocalizedBlockName = getUnlocalizedName(block.blockstate);
	}

	private static String getBlockStateKey(IBlockState blockstate) {
		return blockstate.getBlock().getRegistryName().toString() + "_" + blockstate.getBlock().getMetaFromState(blockstate);
	}

	//TODO: Make into a helper somewhere
	private static String getUnlocalizedName(IBlockState blockstate) {
		ItemStack attempt = ItemStack.EMPTY;
		try {
			attempt = blockstate.getBlock().getPickBlock(blockstate, null, null, BlockPos.ORIGIN, null);
		} catch (Exception ignored) {
		}
		if (attempt.isEmpty()) {
			Item i = Item.getItemFromBlock(blockstate.getBlock());
			if (i != Items.AIR) {
				int meta = blockstate.getBlock().getMetaFromState(blockstate);
				attempt = new ItemStack(i, 1, meta);
			}
		}
		String name;
		if (attempt.isEmpty()) {
			name = blockstate.getBlock().getUnlocalizedName();
		} else {
			name = attempt.getUnlocalizedName();
		}
		return name;
	}

	@Override
	public void registerLogic(AgeDirector controller, long seed) {
		ModifierUtils.pushBlock(controller, blockDescriptor);
	}

	@Override
	public String generateLocalizedName() {
		String blockName = I18n.format(unlocalizedBlockName + ".name");
        if (blockName.endsWith(" Block")) {
            blockName = blockName.substring(0, blockName.length() - " Block".length()).trim();
        }
		return I18n.format("myst.symbol.block.wrapper", blockName);
	}
}
