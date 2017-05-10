package com.xcompwiz.mystcraft.inventory;

import com.xcompwiz.mystcraft.tileentity.TileEntityBookBinder;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class BinderPageReceiver implements ITargetInventory {

	private TileEntityBookBinder tileentity;

	public BinderPageReceiver(TileEntityBookBinder tileentity) {
		this.tileentity = tileentity;
	}

	@Override
	public boolean merge(@Nonnull ItemStack itemstack) {
		boolean success = false;

		ItemStack result = tileentity.insertPage(itemstack, tileentity.getPageList().size());
		if (result.isEmpty()) {
			success = true;
		}
		return success;
	}
}
