package com.xcompwiz.mystcraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public interface IItemBuilder {

	void buildItem(@Nonnull ItemStack itemstack, @Nonnull EntityPlayer player);

}
