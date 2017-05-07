package com.xcompwiz.mystcraft.tileentity;

import com.xcompwiz.mystcraft.api.item.IItemPortalActivator;
import com.xcompwiz.mystcraft.network.IMessageReceiver;
import com.xcompwiz.mystcraft.portal.PortalUtils;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class TileEntityBookReceptacle extends TileEntityBookRotateable implements IMessageReceiver {

	public TileEntityBookReceptacle() {
		tileEntityInvalid = false;
	}

	@Override
	public String getInventoryName() {
		return "Book Receptacle";
	}

	public int getPortalColor() {
		ItemStack itemstack = getBook();
		if (itemstack == null) return 0xFFFFFF;
		Item itemdata = itemstack.getItem();
		if (itemdata instanceof IItemPortalActivator) {
			return ((IItemPortalActivator)itemdata).getPortalColor(itemstack, worldObj);
		}
		return 0xFFFFFF;
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		if (itemstack == null) return false;
		return itemstack.getItem() instanceof IItemPortalActivator;
	}

	@Override
	public void handleItemChange(int slot) {
		super.handleItemChange(slot);
		if (worldObj == null) return;
		if (this.worldObj.isRemote) return;
		PortalUtils.shutdownPortal(worldObj, xCoord, yCoord, zCoord);
		ItemStack book = getBook();
		if (book == null) {
			return;
		} else if (book.getItem() instanceof IItemPortalActivator) {
			PortalUtils.firePortal(worldObj, xCoord, yCoord, zCoord);
		}
	}
}
