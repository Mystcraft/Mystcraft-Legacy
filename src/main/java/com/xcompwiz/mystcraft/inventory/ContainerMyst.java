package com.xcompwiz.mystcraft.inventory;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public abstract class ContainerMyst extends Container {
	protected List<SlotCollection>	collections	= new ArrayList<SlotCollection>();

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int i) {
		ItemStack clone = null;
		Slot slot = (Slot) inventorySlots.get(i);
		if (slot != null && slot.getHasStack()) {
			ItemStack original = slot.getStack();
			clone = original.copy();

			for (SlotCollection collection : collections) {
				if (!collection.contains(i)) continue;
				collection.onShiftClick(original);
				break;
			}

			if (original.stackSize == 0) {
				slot.putStack(null);
			} else {
				slot.onSlotChanged();
			}
			if (original.stackSize != clone.stackSize) {
				slot.onPickupFromSlot(player, original);
			} else {
				return null;
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
			while (itemstack.stackSize > 0 && (!reverse && slotId < end || reverse && slotId >= start)) {
				slot = (Slot) this.inventorySlots.get(slotId);
				destStack = slot.getStack();

				if (destStack != null && destStack == itemstack && (!itemstack.getHasSubtypes() || itemstack.getItemDamage() == destStack.getItemDamage()) && ItemStack.areItemStackTagsEqual(itemstack, destStack)) {
					int totalSize = destStack.stackSize + itemstack.stackSize;

					if (totalSize <= itemstack.getMaxStackSize()) {
						itemstack.stackSize = 0;
						destStack.stackSize = totalSize;
						slot.onSlotChanged();
						success = true;
					} else if (destStack.stackSize < itemstack.getMaxStackSize()) {
						itemstack.stackSize -= itemstack.getMaxStackSize() - destStack.stackSize;
						destStack.stackSize = itemstack.getMaxStackSize();
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
		if (itemstack.stackSize > 0) {
			if (reverse) {
				slotId = end - 1;
			} else {
				slotId = start;
			}

			while (!reverse && slotId < end || reverse && slotId >= start) {
				slot = (Slot) this.inventorySlots.get(slotId);
				destStack = slot.getStack();

				if (destStack == null && slot.isItemValid(itemstack)) {
					slot.putStack(itemstack.copy());
					slot.onSlotChanged();
					itemstack.stackSize = 0;
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
