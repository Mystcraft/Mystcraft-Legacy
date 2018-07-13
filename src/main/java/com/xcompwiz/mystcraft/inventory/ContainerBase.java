package com.xcompwiz.mystcraft.inventory;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public abstract class ContainerBase extends Container {

	protected List<SlotCollection> collections = new ArrayList<>();

	@Override
	@Nonnull
	public ItemStack transferStackInSlot(EntityPlayer player, int i) {
		ItemStack clone = ItemStack.EMPTY;
		Slot slot = inventorySlots.get(i);
		if (slot != null && slot.getHasStack()) {
			ItemStack original = slot.getStack();
			clone = original.copy();

			for (SlotCollection collection : collections) {
				if (!collection.contains(i))
					continue;
				collection.onShiftClick(original);
				break;
			}

			if (original.getCount() == 0) {
				slot.putStack(ItemStack.EMPTY);
			} else {
				slot.onSlotChanged();
			}
			if (original.getCount() != clone.getCount()) {
				slot.onTake(player, original);
			} else {
				return ItemStack.EMPTY;
			}
		}
		return clone;
	}

	/**
	 * merges provided ItemStack with the first avaliable one in the container/player inventory
	 */
	@Override
	protected boolean mergeItemStack(ItemStack itemstack, int start, int end, boolean reverse) {
		boolean success = false;
		int slotId = start;

		if (reverse) {
			slotId = end - 1;
		}

		Slot slot;
		ItemStack destStack;

		// Try merging stack
		if (itemstack.isStackable()) {
			while (itemstack.getCount() > 0 && (!reverse && slotId < end || reverse && slotId >= start)) {
				slot = this.inventorySlots.get(slotId);
				destStack = slot.getStack();

				if (!destStack.isEmpty() && destStack == itemstack && (!itemstack.getHasSubtypes() || itemstack.getItemDamage() == destStack.getItemDamage()) && ItemStack.areItemStackTagsEqual(itemstack, destStack)) {
					int totalSize = destStack.getCount() + itemstack.getCount();

					if (totalSize <= itemstack.getMaxStackSize()) {
						itemstack.setCount(0);
						destStack.setCount(totalSize);
						slot.onSlotChanged();
						success = true;
					} else if (destStack.getCount() < itemstack.getMaxStackSize()) {
						itemstack.shrink(itemstack.getMaxStackSize() - destStack.getCount());
						destStack.setCount(itemstack.getMaxStackSize());
						slot.onSlotChanged();
						success = true;
					}
				}

				if (reverse) {
					--slotId;
				} else {
					++slotId;
				}
			}
		}

		// If left overs, put in a free slot
		if (itemstack.getCount() > 0) {
			if (reverse) {
				slotId = end - 1;
			} else {
				slotId = start;
			}

			while (!reverse && slotId < end || reverse && slotId >= start) {
				slot = this.inventorySlots.get(slotId);
				destStack = slot.getStack();
				if (destStack.isEmpty() && slot.isItemValid(itemstack)) {
					slot.putStack(itemstack.copy());
					slot.onSlotChanged();
					itemstack.setCount(0);
					success = true;
					break;
				}

				if (reverse) {
					--slotId;
				} else {
					++slotId;
				}
			}
		}

		return success;
	}
}
