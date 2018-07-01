package com.xcompwiz.mystcraft.item;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;

public class ItemStackUtils {

	@Nonnull
	public static ItemStack loadItemStackFromNBT(NBTTagCompound item) {
		ItemStack itemstack = new ItemStack(item);
		if (!itemstack.isEmpty() && itemstack.getItem() instanceof IItemOnLoadable) {
			itemstack = ((IItemOnLoadable) itemstack.getItem()).onLoad(itemstack);
		}
		return itemstack;
	}

}
