package com.xcompwiz.mystcraft.inventory;

import com.xcompwiz.mystcraft.api.item.IItemOrderablePageProvider;
import com.xcompwiz.mystcraft.api.item.IItemPageCollection;
import com.xcompwiz.mystcraft.api.item.IItemRenameable;
import com.xcompwiz.mystcraft.inventory.PageCollectionPageReceiver.IItemProvider;
import com.xcompwiz.mystcraft.network.IGuiMessageHandler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ContainerFolder extends ContainerBase implements IGuiMessageHandler, IItemProvider {

	public static class Messages {

		public static final String AddToSurface = "AddToSurface";
		public static final String RemoveFromOrderedCollection = "RemoveFromOrderedCollection";
		public static final String RemoveFromCollection = "RemoveFromCollection";

	}

	private InventoryPlayer playerinv;
	private int slot;

	public ContainerFolder(InventoryPlayer inventoryplayer, int slot) {
		this.playerinv = inventoryplayer;
		this.slot = slot;

		for (int i = 0; i < 3; i++) {
			for (int k = 0; k < 9; k++) {
				addSlotToContainer(new Slot(inventoryplayer, k + i * 9 + 9, 8 + k * 18, 135 + i * 18));
			}
		}

		for (int j = 0; j < 9; j++) {
			if (slot == j) {
				addSlotToContainer(new SlotBanned(inventoryplayer, j, 8 + j * 18, 135 + 58));
			} else {
				addSlotToContainer(new Slot(inventoryplayer, j, 8 + j * 18, 135 + 58));
			}
		}

		SlotCollection maininv = new SlotCollection(this, 0, 27);
		SlotCollection hotbar = new SlotCollection(this, 27, 27 + 9);

		ITargetInventory pagecollectionreceiver = new PageCollectionPageReceiver(this, playerinv.player);

		maininv.pushTargetFront(hotbar);
		maininv.pushTargetFront(pagecollectionreceiver);
		hotbar.pushTargetFront(maininv);
		hotbar.pushTargetFront(pagecollectionreceiver);

		collections.add(maininv);
		collections.add(hotbar);
	}

	@Override
	@Nonnull
	public ItemStack getPageCollection() {
		return getInventoryItem();
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return true;
	}

	@Nullable
	public String getTabItemName() {
		ItemStack itemstack = getInventoryItem();
		if (itemstack.isEmpty())
			return null;
		if (itemstack.getItem() instanceof IItemRenameable)
			return ((IItemRenameable) itemstack.getItem()).getDisplayName(playerinv.player, itemstack);
		return null;
	}

	@Nonnull
	public ItemStack getInventoryItem() {
		return playerinv.getStackInSlot(slot);
	}

	@Override
	public void processMessage(@Nonnull EntityPlayer player, @Nonnull NBTTagCompound data) {
		if (data.hasKey(Messages.RemoveFromCollection)) {
			if (!player.inventory.getItemStack().isEmpty())
				return;
			ItemStack page = new ItemStack(data.getCompoundTag(Messages.RemoveFromCollection));
			ItemStack itemstack = removePageFromSurface(player, this.getInventoryItem(), page);
			player.inventory.setItemStack(itemstack);
		}
		if (data.hasKey(Messages.RemoveFromOrderedCollection)) {
			if (!player.inventory.getItemStack().isEmpty())
				return;
			int index = data.getInteger(Messages.RemoveFromOrderedCollection);
			player.inventory.setItemStack(removePageFromSurface(player, this.getInventoryItem(), index));
		}
		if (data.hasKey(Messages.AddToSurface)) {
			if (player.inventory.getItemStack().isEmpty())
				return;
			if (!data.hasKey("Index"))
				return;
			if (this.getInventoryItem().isEmpty())
				return;
			boolean single = data.getBoolean("Single");
			int index = data.getInteger("Index");
			if (single) {
				ItemStack stack = player.inventory.getItemStack();
				ItemStack clone = stack.copy();
				clone.setCount(1);
				ItemStack returned = placePageOnSurface(player, this.getInventoryItem(), clone, index);
				if (returned.isEmpty() || stack.getCount() == 1) {
					stack.shrink(1);
					if (stack.getCount() <= 0) {
						stack = returned;
					}
					player.inventory.setItemStack(stack);
				} else {
					placePageOnSurface(player, this.getInventoryItem(), returned, index);
				}
			} else {
				player.inventory.setItemStack(placePageOnSurface(player, this.getInventoryItem(), player.inventory.getItemStack(), index));
			}
		}
	}

	//XXX: These functions are identical to the ones in TileEntityDesk
	@Nonnull
	private ItemStack placePageOnSurface(EntityPlayer player, @Nonnull ItemStack itemstack, @Nonnull ItemStack page, int index) {
		if (itemstack.isEmpty())
			return page;
		ItemStack result = page;
		if (itemstack.getItem() instanceof IItemPageCollection)
			result = ((IItemPageCollection) itemstack.getItem()).addPage(player, itemstack, page);
		if (itemstack.getItem() instanceof IItemOrderablePageProvider)
			result = ((IItemOrderablePageProvider) itemstack.getItem()).setPage(player, itemstack, page, index);
		if (result == page)
			return result;
		return result;
	}

	@Nonnull
	private ItemStack removePageFromSurface(EntityPlayer player, @Nonnull ItemStack itemstack, int index) {
		if (itemstack.isEmpty())
			return ItemStack.EMPTY;
		ItemStack result = ItemStack.EMPTY;
		if (itemstack.getItem() instanceof IItemOrderablePageProvider)
			result = ((IItemOrderablePageProvider) itemstack.getItem()).removePage(player, itemstack, index);
		if (result.isEmpty())
			return result;
		return result;
	}

	@Nonnull
	private ItemStack removePageFromSurface(EntityPlayer player, @Nonnull ItemStack itemstack, @Nonnull ItemStack page) {
		if (itemstack.isEmpty())
			return ItemStack.EMPTY;
		ItemStack result = ItemStack.EMPTY;
		if (itemstack.getItem() instanceof IItemPageCollection)
			result = ((IItemPageCollection) itemstack.getItem()).remove(player, itemstack, page);
		if (result.isEmpty())
			return result;
		return result;
	}
}
