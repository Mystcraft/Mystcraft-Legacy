package com.xcompwiz.mystcraft.inventory;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class TargetInventory implements ITargetInventory {

	private IInventory inventory;
	private int begin;
	private int end;
	private boolean reverse = false;

	public TargetInventory(InventoryPlayer inventory) {
		this.inventory = inventory;
		begin = 0;
		end = inventory.mainInventory.size();
	}

	@Override
	public boolean merge(@Nonnull ItemStack itemstack) {
		boolean success = false;
		int slotId = begin;

		if (reverse) {
			slotId = end - 1;
		}

		ItemStack destStack;

		// Try merging stack
		if (itemstack.isStackable()) {
			while (itemstack.getCount() > 0 && (!reverse && slotId < end || reverse && slotId >= begin)) {
				destStack = inventory.getStackInSlot(slotId);

				if (!destStack.isEmpty() && destStack == itemstack && (!itemstack.getHasSubtypes() || itemstack.getItemDamage() == destStack.getItemDamage()) && ItemStack.areItemStackTagsEqual(itemstack, destStack)) {
					int totalSize = destStack.getCount() + itemstack.getCount();

					if (totalSize <= itemstack.getMaxStackSize()) {
						itemstack.setCount(0);
						destStack.setCount(totalSize);
						inventory.setInventorySlotContents(slotId, destStack);
						success = true;
					} else if (destStack.getCount() < itemstack.getMaxStackSize()) {
						itemstack.shrink(itemstack.getMaxStackSize() - destStack.getCount());
						destStack.setCount(itemstack.getMaxStackSize());
						inventory.setInventorySlotContents(slotId, destStack);
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

			while (!reverse && slotId < end || reverse && slotId >= begin) {
				destStack = inventory.getStackInSlot(slotId);

				if (destStack.isEmpty() && inventory.isItemValidForSlot(slotId, itemstack)) {
					inventory.setInventorySlotContents(slotId, itemstack.copy());
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
