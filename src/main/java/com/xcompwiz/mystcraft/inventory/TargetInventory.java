package com.xcompwiz.mystcraft.inventory;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class TargetInventory implements ITargetInventory {

	private IInventory	inventory;
	private int			begin;
	private int			end;
	private boolean		reverse	= false;

	public TargetInventory(InventoryPlayer inventory) {
		this.inventory = inventory;
		begin = 0;
		end = inventory.mainInventory.length;
	}

	@Override
	public boolean merge(ItemStack itemstack) {
		boolean success = false;
		int slotId = begin;

		if (reverse) {
			slotId = end - 1;
		}

		ItemStack destStack;

		// Try merging stack
		if (itemstack.isStackable()) {
			while (itemstack.stackSize > 0 && (!reverse && slotId < end || reverse && slotId >= begin)) {
				destStack = inventory.getStackInSlot(slotId);

				if (destStack != null && destStack == itemstack && (!itemstack.getHasSubtypes() || itemstack.getItemDamage() == destStack.getItemDamage()) && ItemStack.areItemStackTagsEqual(itemstack, destStack)) {
					int totalSize = destStack.stackSize + itemstack.stackSize;

					if (totalSize <= itemstack.getMaxStackSize()) {
						itemstack.stackSize = 0;
						destStack.stackSize = totalSize;
						inventory.setInventorySlotContents(slotId, destStack);
						success = true;
					} else if (destStack.stackSize < itemstack.getMaxStackSize()) {
						itemstack.stackSize -= itemstack.getMaxStackSize() - destStack.stackSize;
						destStack.stackSize = itemstack.getMaxStackSize();
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
		if (itemstack.stackSize > 0) {
			if (reverse) {
				slotId = end - 1;
			} else {
				slotId = begin;
			}

			while (!reverse && slotId < end || reverse && slotId >= begin) {
				destStack = inventory.getStackInSlot(slotId);

				if (destStack == null && inventory.isItemValidForSlot(slotId, itemstack)) {
					inventory.setInventorySlotContents(slotId, itemstack.copy());
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
