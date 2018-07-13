package com.xcompwiz.mystcraft.inventory;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IItemBuilder {

	void buildItem(@Nonnull ItemStack itemstack, @Nonnull EntityPlayer player);

}
