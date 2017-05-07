package com.xcompwiz.mystcraft.inventory;

import com.xcompwiz.mystcraft.tileentity.TileEntityBookBinder;

import net.minecraft.item.ItemStack;

public class BinderPageReceiver implements ITargetInventory {

	private TileEntityBookBinder	tileentity;

	public BinderPageReceiver(TileEntityBookBinder tileentity) {
		this.tileentity = tileentity;
	}

	@Override
	public boolean merge(ItemStack itemstack) {
		boolean success = false;

		ItemStack result = tileentity.insertPage(itemstack, tileentity.getPageList().size());
		if (result == null) {
			success = true;
		}
		return success;
	}
}
