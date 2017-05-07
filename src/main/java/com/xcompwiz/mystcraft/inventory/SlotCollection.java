package com.xcompwiz.mystcraft.inventory;

import java.util.LinkedList;

import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotCollection implements ITargetInventory {
	private int								begin;
	private int								end;
	private boolean							reverse		= false;
	private Container						container;

	private LinkedList<ITargetInventory>	targetList	= new LinkedList<ITargetInventory>();

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

	public boolean onShiftClick(ItemStack original) {
		for (ITargetInventory target : targetList) {
			if (target.merge(original)) return true;
		}
		return false;
	}

	@Override
	public boolean merge(ItemStack itemstack) {
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
				slot = (Slot) container.inventorySlots.get(slotId);
				destStack = slot.getStack();

				if (destStack != null && destStack == itemstack && (!itemstack.getHasSubtypes() || itemstack.getItemDamage() == destStack.getItemDamage()) && ItemStack.areItemStackTagsEqual(itemstack, destStack)) {
					int totalSize = destStack.getCount() + itemstack.getCount();
					int maxdestsize = itemstack.getMaxStackSize();
					if (slot.getSlotStackLimit() < maxdestsize) maxdestsize = slot.getSlotStackLimit();

					if (totalSize <= maxdestsize) {
						itemstack.stackSize = 0;
						destStack.stackSize = totalSize;
						slot.onSlotChanged();
						success = true;
					} else if (destStack.getCount() < maxdestsize) {
						itemstack.stackSize -= maxdestsize - destStack.getCount();
						destStack.stackSize = maxdestsize;
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
				slot = (Slot) container.inventorySlots.get(slotId);
				destStack = slot.getStack();

				if (destStack == null && slot.isItemValid(itemstack)) {
					ItemStack clone = itemstack.copy();
					if (clone.getCount() > slot.getSlotStackLimit()) clone.stackSize = slot.getSlotStackLimit();
					slot.putStack(clone);
					slot.onSlotChanged();
					itemstack.stackSize -= clone.getCount();
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
