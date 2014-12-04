package com.xcompwiz.mystcraft.block;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.xcompwiz.mystcraft.client.gui.GuiBook;
import com.xcompwiz.mystcraft.client.gui.GuiHandlerManager;
import com.xcompwiz.mystcraft.inventory.ContainerBook;
import com.xcompwiz.mystcraft.network.NetworkUtils;
import com.xcompwiz.mystcraft.tileentity.TileEntityBook;
import com.xcompwiz.mystcraft.tileentity.TileEntityBookDisplay;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class BlockBookDisplay extends BlockContainer {

	public static class GuiHandlerBookDisplay extends GuiHandlerManager.GuiHandler {
		@Override
		public TileEntity getTileEntity(EntityPlayerMP player, World worldObj, int i, int j, int k) {
			return player.worldObj.getTileEntity(i, j, k);
		}

		@Override
		public Container getContainer(EntityPlayerMP player, World worldObj, TileEntity tileentity, int i, int j, int k) {
			// Sends age data packet
			NetworkUtils.sendAgeData(worldObj, ((TileEntityBookDisplay) tileentity).getBook(), player);
			return new ContainerBook(player.inventory, (TileEntityBookDisplay) tileentity);
		}

		@Override
		@SideOnly(Side.CLIENT)
		public GuiScreen getGuiScreen(EntityPlayer player, ByteBuf data) {
			int x = data.readInt();
			int y = data.readInt();
			int z = data.readInt();
			TileEntityBookDisplay tileentity = (TileEntityBookDisplay) player.worldObj.getTileEntity(x, y, z);
			return new GuiBook(player.inventory, tileentity);
		}
	}

	static final int	GuiID	= GuiHandlerManager.registerGuiNetHandler(new GuiHandlerBookDisplay());

	protected BlockBookDisplay(Material material) {
		super(material);
	}

	@Override
	public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer entityplayer, int side, float posX, float pozY, float posZ) {
		if (world.isRemote) return true;
		TileEntityBookDisplay tileentity = (TileEntityBookDisplay) world.getTileEntity(i, j, k);
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
				NetworkUtils.displayGui(entityplayer, world, GuiID, i, j, k);
			}
		} else {
			if (entityplayer.isSneaking() && entityplayer.inventory.getCurrentItem() == null) {
				entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, tileentity.getBook());
				entityplayer.inventory.markDirty();
				tileentity.setBook(null);
				return true;
			}
			NetworkUtils.displayGui(entityplayer, world, GuiID, i, j, k);
		}
		return true;
	}

	@Override
	public void breakBlock(World world, int i, int j, int k, Block block, int meta) {
		if (!world.isRemote) {
			TileEntityBookDisplay book = (TileEntityBookDisplay) world.getTileEntity(i, j, k);
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
