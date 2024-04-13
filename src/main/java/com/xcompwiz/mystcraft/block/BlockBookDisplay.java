package com.xcompwiz.mystcraft.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.xcompwiz.mystcraft.Mystcraft;
import com.xcompwiz.mystcraft.data.ModGUIs;
import com.xcompwiz.mystcraft.tileentity.TileEntityBook;
import com.xcompwiz.mystcraft.tileentity.TileEntityBookRotateable;

public abstract class BlockBookDisplay extends BlockContainer {

	protected BlockBookDisplay(Material material) {
		super(material);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityplayer, int side, float posX, float pozY, float posZ) {
		if (world.isRemote) return true;
		TileEntityBookRotateable tileentity = (TileEntityBookRotateable) world.getTileEntity(x, y, z);
		if (tileentity == null) { return true; }
		if (tileentity.getBook() == null) {
			ItemStack itemstack = entityplayer.inventory.getCurrentItem();
			if (itemstack != null && tileentity.isItemValidForSlot(0, itemstack)) {
				ItemStack copy = itemstack.copy();
				copy.stackSize = 1;
				itemstack.stackSize -= 1;
				tileentity.setBook(copy);
				entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, itemstack);
			} else {
				entityplayer.openGui(Mystcraft.instance, ModGUIs.BOOK_DISPLAY.ordinal(), world, x, y, z);
				return true;
			}
		} else {
			if (entityplayer.isSneaking() && entityplayer.inventory.getCurrentItem() == null) {
				entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, tileentity.getBook());
				entityplayer.inventory.markDirty();
				tileentity.setBook(null);
				return true;
			}
			entityplayer.openGui(Mystcraft.instance, ModGUIs.BOOK_DISPLAY.ordinal(), world, x, y, z);
			return true;
		}
		return true;
	}

	@Override
	public void breakBlock(World world, int i, int j, int k, Block block, int meta) {
		if (!world.isRemote) {
			TileEntityBookRotateable book = (TileEntityBookRotateable) world.getTileEntity(i, j, k);
			if (book == null) {
				// System.out.println("No tile entity?");
				return;
			}
			ItemStack itemstack = book.getBook();
			book.setBook(null);
			if (itemstack != null) {
				EntityItem entity = new EntityItem(world, i, j, k, itemstack);
				world.spawnEntityInWorld(entity);
			}
		}
		super.breakBlock(world, i, j, k, block, meta);
	}

	/**
	 * If this returns true, then comparators facing away from this block will use the value from getComparatorInputOverride instead of the actual redstone
	 * signal strength.
	 */
	@Override
	public boolean hasComparatorInputOverride() {
		return true;
	}

	/**
	 * If hasComparatorInputOverride returns true, the return value from this is used instead of the redstone signal strength when this block inputs to a
	 * comparator.
	 */
	@Override
	public int getComparatorInputOverride(World par1World, int par2, int par3, int par4, int par5) {
		return Container.calcRedstoneFromInventory(this.getInventory(par1World, par2, par3, par4));
	}

	private IInventory getInventory(World worldObj, int i, int j, int k) {
		return (TileEntityBook) worldObj.getTileEntity(i, j, k);
	}
}
