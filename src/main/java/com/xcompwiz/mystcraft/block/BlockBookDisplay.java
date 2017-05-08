package com.xcompwiz.mystcraft.block;

import com.xcompwiz.mystcraft.Mystcraft;
import com.xcompwiz.mystcraft.data.ModGUIs;
import com.xcompwiz.mystcraft.tileentity.TileEntityBook;
import com.xcompwiz.mystcraft.tileentity.TileEntityBookRotateable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class BlockBookDisplay extends BlockContainer {

	protected BlockBookDisplay(Material material) {
		super(material);
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if(worldIn.isRemote) return true;
		TileEntityBookRotateable tileentity = (TileEntityBookRotateable) worldIn.getTileEntity(pos);
		if (tileentity == null) {
			return true;
		}
		if(tileentity.getBook().isEmpty()) {
			ItemStack stack = playerIn.getHeldItem(hand);
			if(!stack.isEmpty() && tileentity.isItemValidForSlot(0, stack)) {
				ItemStack copy = stack.copy();
				copy.setCount(1);
				stack.setCount(1);
				tileentity.setBook(copy);
				playerIn.setHeldItem(hand, stack);
			} else {
				playerIn.openGui(Mystcraft.instance, ModGUIs.BOOK_DISPLAY.ordinal(), worldIn, pos.getX(), pos.getY(), pos.getZ());
				return true;
			}
		} else {
			if(playerIn.isSneaking() && playerIn.getHeldItem(hand).isEmpty()) {
				playerIn.setHeldItem(hand, tileentity.getBook());
				tileentity.setBook(ItemStack.EMPTY);
				return true;
			}
			playerIn.openGui(Mystcraft.instance, ModGUIs.BOOK_DISPLAY.ordinal(), worldIn, pos.getX(), pos.getY(), pos.getZ());
			return true;
		}

		return true;
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		if(!worldIn.isRemote) {
			TileEntityBookRotateable book = (TileEntityBookRotateable) worldIn.getTileEntity(pos);
			if(book != null) {
				ItemStack item = book.getBook();
				book.setBook(ItemStack.EMPTY);
				if(!item.isEmpty()) {
					EntityItem drop = new EntityItem(worldIn, pos.getX(), pos.getY(), pos.getZ(), item);
					worldIn.spawnEntity(drop);
				}
			}
		}

		super.breakBlock(worldIn, pos, state);
	}

	@Override
	public boolean hasComparatorInputOverride(IBlockState state) {
		return true;
	}

	@Override
	public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos) {
		return Container.calcRedstoneFromInventory(getInventory(worldIn, pos));
	}

	private IInventory getInventory(World worldObj, BlockPos pos) {
		return (TileEntityBook) worldObj.getTileEntity(pos);
	}
}
