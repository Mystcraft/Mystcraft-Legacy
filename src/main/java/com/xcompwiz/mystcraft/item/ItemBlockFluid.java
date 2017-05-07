package com.xcompwiz.mystcraft.item;

import com.xcompwiz.mystcraft.block.BlockFluidWrapper;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemBlockFluid extends ItemBlock {
	/** Instance of the Blocks. */
	private final BlockFluidBase	theBlock;

	public ItemBlockFluid(Block block, BlockFluidWrapper par2Block) {
		super(block);
		this.theBlock = par2Block;
		this.setMaxDamage(0);
		this.setHasSubtypes(false);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getColorFromItemStack(ItemStack par1ItemStack, int par2) {
		return this.theBlock.getRenderColor(par1ItemStack.getItemDamage());
	}

	@Override
	@SideOnly(Side.CLIENT)
	/**
	 * Gets an icon index based on an item's damage value
	 */
	public IIcon getIconFromDamage(int par1) {
		return this.theBlock.getIcon(0, par1);
	}

	/**
	 * Returns the metadata of the block which this Item (ItemBlock) can place
	 */
	@Override
	public int getMetadata(int par1) {
		return par1;
	}

	@Override
	public String getUnlocalizedName(ItemStack par1ItemStack) {
		Fluid fluid = this.theBlock.getFluid();
		return super.getUnlocalizedName() + "." + fluid.getName();
	}
}
