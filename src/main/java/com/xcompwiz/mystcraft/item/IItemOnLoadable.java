package com.xcompwiz.mystcraft.item;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;

public interface IItemOnLoadable {

	@Nonnull
	ItemStack onLoad(@Nonnull ItemStack itemstack);

}
