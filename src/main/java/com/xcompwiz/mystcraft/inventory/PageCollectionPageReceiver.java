package com.xcompwiz.mystcraft.inventory;

import com.xcompwiz.mystcraft.api.item.IItemPageCollection;
import com.xcompwiz.mystcraft.data.ModItems;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class PageCollectionPageReceiver implements ITargetInventory {

	public interface IItemProvider {
		ItemStack getPageCollection();
	}

	private IItemProvider	provider;
	private EntityPlayer	player;

	public PageCollectionPageReceiver(IItemProvider provider, EntityPlayer player) {
		this.provider = provider;
		this.player = player;
	}

	@Override
	public boolean merge(ItemStack itemstack) {
		boolean success = false;
		if (itemstack.getItem() != ModItems.page) return false;

		ItemStack collection = provider.getPageCollection();
		if (collection != null && collection.getItem() instanceof IItemPageCollection) {
			ItemStack result = ((IItemPageCollection) collection.getItem()).addPage(player, collection, itemstack);
			//FIXME: Handle scenarios of partial add or alternate item return
			//Problem: Competing conventions; Minecraft (typically) passes the itemstack in and assumes it retains ownership of that itemstack (the itemstack will be cloned and the original edited)
			// Mystcraft allows for some things to return the "new" itemstack state.  Merging the two is problematic.
			if (result == null) {
				itemstack.stackSize = 0;
				success = true;
			}
		}
		return success;
	}
}
