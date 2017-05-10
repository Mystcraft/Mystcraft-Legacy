package com.xcompwiz.mystcraft.inventory;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public interface ITargetInventory {

	boolean merge(@Nonnull ItemStack original);

}
