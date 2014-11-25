package com.xcompwiz.mystcraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotFolder extends Slot {

	private ItemStack	item;
	protected int		slotIndex;

	public SlotFolder(IInventory inventory, int slot, int x, int y) {
		super(inventory, slot, x, y);
		this.slotIndex = slot;
	}

	/**
	 * Check if the stack is a valid item for this slot. Always true beside for the armor slots.
	 */
	@Override
	public boolean isItemValid(ItemStack itemstack) {
		return InventoryFolder.isItemValid(itemstack);//TODO: Reference to InventoryFolder outside folder
	}

	/**
	 * Helper function to get the stack in the slot.
	 */
	@Override
	public ItemStack getStack() {
		item = this.inventory.getStackInSlot(this.slotIndex);
		return item;
	}

	/**
	 * Helper method to put a stack in the slot.
	 */
	@Override
	public void putStack(ItemStack itemstack) {
		item = itemstack;
		this.inventory.setInventorySlotContents(this.slotIndex, itemstack);
	}

	/**
	 * Decrease the size of the stack in slot (first int arg) by the amount of the second int arg. Returns the new
	 * stack.
	 */
	@Override
	public ItemStack decrStackSize(int par1) {
		ItemStack result = this.inventory.decrStackSize(this.slotIndex, par1);
		item = this.inventory.getStackInSlot(this.slotIndex);
		return result;
	}

	/**
	 * returns true if this slot is in par2 of par1
	 */
	@Override
	public boolean isSlotInInventory(IInventory par1IInventory, int slot) {
		return par1IInventory == this.inventory && slot == this.slotIndex;
	}

	@Override
	public void onPickupFromSlot(EntityPlayer entityplayer, ItemStack removed) {
		this.onSlotChanged();
	}

	/**
	 * Called when the stack in a Slot changes
	 */
	@Override
	public void onSlotChanged() {
		this.inventory.markDirty();
		this.putStack(item);
	}
}
