package com.xcompwiz.mystcraft.inventory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.xcompwiz.mystcraft.tileentity.IOInventory;
import com.xcompwiz.mystcraft.tileentity.InventoryFilter;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.SlotItemHandler;

public class SlotFiltered extends SlotItemHandler {

	private int filterOffset;
	private int slotIndex;
	private InventoryFilter filter;
	private Integer maxstack;

	public SlotFiltered(IItemHandlerModifiable inventory, @Nullable InventoryFilter filter, int slot, int x, int y) {
		super(inventory, slot, x, y);
		this.filter = filter;
		this.slotIndex = slot;
	}

	public SlotFiltered(IItemHandlerModifiable inventory, @Nullable InventoryFilter filter, int filterOffset, int slot, int x, int y) {
		this(inventory, filter, slot, x, y);
		this.filterOffset = filterOffset;
	}

	@Nonnull
	@Override
	public ItemStack getStack() {
		return this.getItemHandler().getStackInSlot(slotIndex);
	}

	@Override
	public void putStack(@Nonnull ItemStack stack) {
		((IItemHandlerModifiable) this.getItemHandler()).setStackInSlot(slotIndex, stack);
		this.onSlotChanged();
	}

	@Override
	public boolean canTakeStack(EntityPlayer playerIn) {
		if (getItemHandler() instanceof IOInventory) {
			boolean allowed = ((IOInventory) getItemHandler()).allowAnySlots;
			((IOInventory) getItemHandler()).allowAnySlots = true;
			boolean take = !this.getItemHandler().extractItem(slotIndex, 1, true).isEmpty();
			((IOInventory) getItemHandler()).allowAnySlots = allowed;
			return take;
		} else {
			return !this.getItemHandler().extractItem(slotIndex, 1, true).isEmpty();
		}
	}

	@Nonnull
	@Override
	public ItemStack decrStackSize(int amount) {
		if (getItemHandler() instanceof IOInventory) {
			boolean allowed = ((IOInventory) getItemHandler()).allowAnySlots;
			((IOInventory) getItemHandler()).allowAnySlots = true;
			ItemStack take = this.getItemHandler().extractItem(slotIndex, amount, false);
			((IOInventory) getItemHandler()).allowAnySlots = allowed;
			return take;
		} else {
			return this.getItemHandler().extractItem(slotIndex, amount, false);
		}
	}

	@Override
	public int getItemStackLimit(@Nonnull ItemStack stack) {
		if (getItemHandler() instanceof IOInventory) {
			boolean allowed = ((IOInventory) getItemHandler()).allowAnySlots;
			((IOInventory) getItemHandler()).allowAnySlots = true;
			int stackLimit = ovrGetItemStackLimit(stack);
			((IOInventory) getItemHandler()).allowAnySlots = allowed;
			return stackLimit;
		} else {
			return ovrGetItemStackLimit(stack);
		}
	}

	private int ovrGetItemStackLimit(@Nonnull ItemStack stack) {
		ItemStack maxAdd = stack.copy();
		int maxInput = stack.getMaxStackSize();
		maxAdd.setCount(maxInput);
		IItemHandler handler = this.getItemHandler();
		ItemStack currentStack = handler.getStackInSlot(slotIndex);
		if (handler instanceof IItemHandlerModifiable) {
			IItemHandlerModifiable handlerModifiable = (IItemHandlerModifiable) handler;

			handlerModifiable.setStackInSlot(slotIndex, ItemStack.EMPTY);

			ItemStack remainder = handlerModifiable.insertItem(slotIndex, maxAdd, true);

			handlerModifiable.setStackInSlot(slotIndex, currentStack);

			return maxInput - remainder.getCount();
		} else {
			ItemStack remainder = handler.insertItem(slotIndex, maxAdd, true);

			int current = currentStack.getCount();
			int added = maxInput - remainder.getCount();
			return current + added;
		}
	}

	@Override
	public boolean isItemValid(@Nonnull ItemStack itemstack) {
		return filter == null || filter.canAcceptItem(this.slotIndex + filterOffset, itemstack);
	}

	@Override
	public int getSlotStackLimit() {
		if (this.maxstack != null)
			return this.maxstack;
		return getItemHandler().getSlotLimit(this.slotIndex);
	}

	public void setSlotStackLimit(int max) {
		this.maxstack = max;
	}

	public void setSlotIndex(int i) {
		slotIndex = i;
	}

}
