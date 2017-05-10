package com.xcompwiz.mystcraft.inventory;

import java.util.LinkedList;

import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class SlotCollection implements ITargetInventory {

	private int								begin;
	private int								end;
	private boolean							reverse		= false;
	private Container						container;

	private LinkedList<ITargetInventory>	targetList	= new LinkedList<>();

	public SlotCollection(Container container, int begin, int end) {
		this.container = container;
		this.begin = begin;
		this.end = end;
	}

	public void pushTargetFront(ITargetInventory target) {
		targetList.add(0, target);
	}

	public boolean contains(int i) {
		if (i < begin) return false;
		if (i >= end) return false;
		return true;
	}

	public boolean onShiftClick(@Nonnull ItemStack original) {
		for (ITargetInventory target : targetList) {
			if (target.merge(original)) return true;
		}
		return false;
	}

	@Override
	public boolean merge(@Nonnull ItemStack itemstack) {
		boolean success = false;
		int slotId = begin;

		if (reverse) {
			slotId = end - 1;
		}

		Slot slot;
		ItemStack destStack;

		// Try merging stack
		if (itemstack.isStackable()) {
			while (itemstack.getCount() > 0 && (!reverse && slotId < end || reverse && slotId >= begin)) {
				slot = container.inventorySlots.get(slotId);
				destStack = slot.getStack();

				if (!destStack.isEmpty() && destStack == itemstack &&
						(!itemstack.getHasSubtypes() || itemstack.getItemDamage() == destStack.getItemDamage()) &&
						ItemStack.areItemStackTagsEqual(itemstack, destStack)) {

					int totalSize = destStack.getCount() + itemstack.getCount();
					int maxdestsize = itemstack.getMaxStackSize();
					if (slot.getSlotStackLimit() < maxdestsize) {
						maxdestsize = slot.getSlotStackLimit();
					}

					if (totalSize <= maxdestsize) {
						itemstack.setCount(0);
						destStack.setCount(totalSize);
						slot.onSlotChanged();
						success = true;
					} else if (destStack.getCount() < maxdestsize) {
						itemstack.shrink(maxdestsize - destStack.getCount());
						destStack.setCount(maxdestsize);
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
				slotId = begin;
			}

			while ((!reverse && slotId < end || reverse && slotId >= begin) && itemstack.getCount() > 0) {
				slot = container.inventorySlots.get(slotId);
				destStack = slot.getStack();

				if (destStack.isEmpty() && slot.isItemValid(itemstack)) {
					ItemStack clone = itemstack.copy();
					if (clone.getCount() > slot.getSlotStackLimit()) {
						clone.setCount(slot.getSlotStackLimit());
					}
					slot.putStack(clone);
					slot.onSlotChanged();
					itemstack.shrink(clone.getCount());
					success = true;
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
