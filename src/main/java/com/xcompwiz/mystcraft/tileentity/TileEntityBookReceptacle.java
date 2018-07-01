package com.xcompwiz.mystcraft.tileentity;

import com.xcompwiz.mystcraft.api.item.IItemPortalActivator;
import com.xcompwiz.mystcraft.portal.PortalUtils;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class TileEntityBookReceptacle extends TileEntityBookRotateable {

	public int getPortalColor() {
		ItemStack itemstack = getBook();
		if (itemstack.isEmpty())
			return 0xFFFFFF;
		Item itemdata = itemstack.getItem();
		if (itemdata instanceof IItemPortalActivator) {
			return ((IItemPortalActivator) itemdata).getPortalColor(itemstack, world);
		}
		return 0xFFFFFF;
	}

	@Override
	public boolean canAcceptItem(int slot, @Nonnull ItemStack stack) {
		return !stack.isEmpty() && stack.getItem() instanceof IItemPortalActivator;
	}

	@Override
	public void handleItemChange(int slot) {
		super.handleItemChange(slot);
		if (world == null || world.isRemote)
			return;
		PortalUtils.shutdownPortal(world, pos);
		ItemStack book = getBook();
		if (!book.isEmpty() && book.getItem() instanceof IItemPortalActivator) {
			PortalUtils.firePortal(world, pos);
		}
	}
}
