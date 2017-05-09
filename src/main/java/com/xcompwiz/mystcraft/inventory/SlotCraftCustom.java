package com.xcompwiz.mystcraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLCommonHandler;

import javax.annotation.Nonnull;

public class SlotCraftCustom extends Slot {

	private final IItemBuilder builderTile;
	private EntityPlayer thePlayer;
	private int	amountCrafted;

	public SlotCraftCustom(EntityPlayer player, IItemBuilder builder, IInventory inventory, int index, int guiPosX, int guiPosY) {
		super(inventory, index, guiPosX, guiPosY);
		this.thePlayer = player;
		this.builderTile = builder;
	}

	@Override
	public boolean isItemValid(@Nonnull ItemStack stack) {
		return false;
	}

	@Override
	@Nonnull
	public ItemStack decrStackSize(int amount) {
		if (this.getHasStack()) {
			this.amountCrafted += Math.min(amount, this.getStack().getCount());
		}
		return super.decrStackSize(amount);
	}

	@Override
	protected void onCrafting(@Nonnull ItemStack stack, int amount) {
		this.amountCrafted += amount;
		this.onCrafting(stack);
	}

	@Override
	protected void onCrafting(@Nonnull ItemStack stack) {
		stack.onCrafting(this.thePlayer.world, this.thePlayer, this.amountCrafted);
		this.amountCrafted = 0;
	}

	@Override
	@Nonnull
	public ItemStack onTake(@Nonnull EntityPlayer player, @Nonnull ItemStack stack) {
		this.builderTile.buildItem(stack, player);
		FMLCommonHandler.instance().firePlayerCraftingEvent(player, stack, inventory);
		this.onCrafting(stack);
		return stack;
	}

}
