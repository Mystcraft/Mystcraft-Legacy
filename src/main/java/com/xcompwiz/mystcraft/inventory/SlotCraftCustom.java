package com.xcompwiz.mystcraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.FMLCommonHandler;

public class SlotCraftCustom extends Slot {
	/** The craft matrix inventory linked to this result slot. */
	private final IItemBuilder	craftMatrix;

	/** The player that is using the GUI where this slot resides. */
	private EntityPlayer		thePlayer;

	/**
	 * The number of items that have been crafted so far. Gets passed to ItemStack.onCrafting before being reset.
	 */
	private int					amountCrafted;

	public SlotCraftCustom(EntityPlayer player, IItemBuilder builder_inv, IInventory par3IInventory, int par4, int par5, int par6) {
		super(par3IInventory, par4, par5, par6);
		this.thePlayer = player;
		this.craftMatrix = builder_inv;
	}

	/**
	 * Check if the stack is a valid item for this slot. Always true beside for the armor slots.
	 */
	@Override
	public boolean isItemValid(ItemStack par1ItemStack) {
		return false;
	}

	/**
	 * Decrease the size of the stack in slot (first int arg) by the amount of the second int arg. Returns the new stack.
	 */
	@Override
	public ItemStack decrStackSize(int par1) {
		if (this.getHasStack()) {
			this.amountCrafted += Math.min(par1, this.getStack().stackSize);
		}
		return super.decrStackSize(par1);
	}

	/**
	 * the itemStack passed in is the output - ie, iron ingots, and pickaxes, not ore and wood. Typically increases an internal count then calls
	 * onCrafting(item).
	 */
	@Override
	protected void onCrafting(ItemStack par1ItemStack, int par2) {
		this.amountCrafted += par2;
		this.onCrafting(par1ItemStack);
	}

	/**
	 * the itemStack passed in is the output - ie, iron ingots, and pickaxes, not ore and wood.
	 */
	@Override
	protected void onCrafting(ItemStack par1ItemStack) {
		par1ItemStack.onCrafting(this.thePlayer.worldObj, this.thePlayer, this.amountCrafted);
		this.amountCrafted = 0;
	}

	@Override
	public void onPickupFromSlot(EntityPlayer player, ItemStack itemstack) {
		this.craftMatrix.buildItem(itemstack, player);
		FMLCommonHandler.instance().firePlayerCraftingEvent(player, itemstack, craftMatrix);
		this.onCrafting(itemstack);
	}
}
