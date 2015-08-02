package com.xcompwiz.mystcraft.item;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ItemStackUtils {

	public static ItemStack loadItemStackFromNBT(NBTTagCompound item) {
		ItemStack itemstack = ItemStack.loadItemStackFromNBT(item);
		if (itemstack != null && itemstack.getItem() instanceof IItemOnLoadable) {
			itemstack = ((IItemOnLoadable)itemstack.getItem()).onLoad(itemstack);
		}
		return itemstack;
	}

}
