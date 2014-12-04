package com.xcompwiz.mystcraft.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class SlotFiltered extends Slot {
	private int			slotIndex;
	private IInventory	filter;
	private Integer		maxstack;

	public SlotFiltered(IInventory inventory, int slot, int x, int y) {
		super(inventory, slot, x, y);
		this.filter = inventory;
		this.slotIndex = slot;
	}

	public SlotFiltered(IInventory inventory, int slot, int x, int y, IIcon icon) {
		this(inventory, slot, x, y);
		this.setBackgroundIcon(icon);
	}

	@Override
	public boolean isItemValid(ItemStack itemstack) {
		return filter.isItemValidForSlot(this.slotIndex, itemstack);
	}

	@Override
	public int getSlotStackLimit() {
		if (this.maxstack != null) return this.maxstack;
		return super.getSlotStackLimit();
	}

	public void setSlotStackLimit(int max) {
		this.maxstack = max;
	}

	public void setSlotIndex(int i) {
		slotIndex = i;
	}

	/**
	 * Helper function to get the stack in the slot.
	 */
	@Override
	public ItemStack getStack() {
		return this.inventory.getStackInSlot(this.slotIndex);
	}

	/**
	 * Helper method to put a stack in the slot.
	 */
	@Override
	public void putStack(ItemStack itemstack) {
		this.inventory.setInventorySlotContents(this.slotIndex, itemstack);
	}

	/**
	 * Decrease the size of the stack in slot (first int arg) by the amount of the second int arg. Returns the new stack.
	 */
	@Override
	public ItemStack decrStackSize(int par1) {
		return this.inventory.decrStackSize(this.slotIndex, par1);
	}

	/**
	 * returns true if this slot is in par2 of par1
	 */
	@Override
	public boolean isSlotInInventory(IInventory par1IInventory, int slot) {
		return par1IInventory == this.inventory && slot == this.slotIndex;
	}
}
