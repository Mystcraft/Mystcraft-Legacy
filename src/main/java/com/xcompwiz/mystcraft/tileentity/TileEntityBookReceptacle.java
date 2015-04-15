package com.xcompwiz.mystcraft.tileentity;

import net.minecraft.item.ItemStack;

import com.xcompwiz.mystcraft.api.linking.ILinkInfo;
import com.xcompwiz.mystcraft.item.ItemLinking;
import com.xcompwiz.mystcraft.linking.DimensionUtils;
import com.xcompwiz.mystcraft.network.IMessageReceiver;
import com.xcompwiz.mystcraft.portal.PortalUtils;

public class TileEntityBookReceptacle extends TileEntityBookRotateable implements IMessageReceiver {

	public TileEntityBookReceptacle() {
		tileEntityInvalid = false;
	}

	@Override
	public String getInventoryName() {
		return "Book Receptacle";
	}

	public int getPortalColor() {
		ItemStack book = getBook();
		if (book == null) return 0xFFFFFF;
		ILinkInfo info = ((ItemLinking) book.getItem()).getLinkInfo(book);
		return DimensionUtils.getLinkColor(info);
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
		} else if (book.getItem() instanceof ItemLinking) {
			PortalUtils.firePortal(worldObj, xCoord, yCoord, zCoord);
		}
	}
}
