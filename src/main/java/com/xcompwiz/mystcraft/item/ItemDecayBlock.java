package com.xcompwiz.mystcraft.item;

import com.xcompwiz.mystcraft.instability.decay.DecayHandler;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class ItemDecayBlock extends ItemBlock {

	public ItemDecayBlock(Block block) {
		super(block);
		this.setMaxDamage(0);
		this.setHasSubtypes(true);
	}

	@Override
	public int getMetadata(int damage) {
		return damage;
	}

	@Override
	@Nonnull
	public String getUnlocalizedName(@Nonnull ItemStack stack) {
		int meta = stack.getItemDamage();
		DecayHandler handler = DecayHandler.getHandler(meta);
		if (handler == null) {
			return super.getUnlocalizedName() + "." + "unknown";
		}
		return super.getUnlocalizedName() + "." + handler.getIdentifier();
	}
}
