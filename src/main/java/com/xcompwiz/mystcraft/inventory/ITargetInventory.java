package com.xcompwiz.mystcraft.inventory;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;

public interface ITargetInventory {

	boolean merge(@Nonnull ItemStack original);

}
