package com.xcompwiz.mystcraft.item;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public interface IItemOnLoadable {

	@Nonnull
	ItemStack onLoad(@Nonnull ItemStack itemstack);

}
