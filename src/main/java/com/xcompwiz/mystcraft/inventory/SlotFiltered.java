package com.xcompwiz.mystcraft.inventory;

import com.xcompwiz.mystcraft.tileentity.InventoryFilter;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SlotFiltered extends SlotItemHandler {

	private int	            slotIndex;
	private InventoryFilter filter;
	private Integer		    maxstack;

	public SlotFiltered(IItemHandlerModifiable inventory, @Nullable InventoryFilter filter, int slot, int x, int y) {
		super(inventory, slot, x, y);
		this.filter = filter;
		this.slotIndex = slot;
	}

	@Override
	public boolean isItemValid(@Nonnull ItemStack itemstack) {
		return filter == null || filter.canAcceptItem(this.slotIndex, itemstack);
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

}
