package com.xcompwiz.mystcraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public interface IItemBuilder extends IInventory {

	public abstract void buildItem(ItemStack itemstack, EntityPlayer player);

}
