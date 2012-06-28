package com.xcompwiz.mystcraft.tileentity;

import java.util.Random;

import net.minecraft.item.ItemStack;

import com.xcompwiz.mystcraft.api.linking.ILinkInfo;
import com.xcompwiz.mystcraft.item.ItemLinking;
import com.xcompwiz.mystcraft.network.IMessageReceiver;
import com.xcompwiz.mystcraft.portal.PortalUtils;

public class TileEntityBookReceptacle extends TileEntityBookDisplay implements IMessageReceiver {

	public TileEntityBookReceptacle() {
		tileEntityInvalid = false;
	}

	@Override
	public String getInventoryName() {
		return "Book Receptacle";
	}

	public int getPortalColor() {
		Random rand = new Random();
		ItemStack book = getBook();
		if (book == null) return 0xFFFFFF;
		ILinkInfo info = ((ItemLinking)book.getItem()).getLinkInfo(book);
		if (info == null) return 0x000000;
		rand.setSeed(info.getDisplayName().hashCode());
		int color = 0;
		color += (rand.nextInt(256));
		color += (rand.nextInt(256) << 8);
		color += (rand.nextInt(256) << 16);
		return color;
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
