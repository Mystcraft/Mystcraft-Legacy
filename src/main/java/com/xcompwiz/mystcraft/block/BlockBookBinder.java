package com.xcompwiz.mystcraft.block;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.xcompwiz.mystcraft.client.gui.GuiHandlerManager;
import com.xcompwiz.mystcraft.client.gui.GuiBookBinder;
import com.xcompwiz.mystcraft.inventory.ContainerBookBinder;
import com.xcompwiz.mystcraft.network.NetworkUtils;
import com.xcompwiz.mystcraft.tileentity.TileEntityBookBinder;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockBookBinder extends BlockContainer {

	public static class GuiHandlerBinder extends GuiHandlerManager.GuiHandler {
		@Override
		public TileEntity getTileEntity(EntityPlayerMP player, World worldObj, int i, int j, int k) {
			return player.worldObj.getTileEntity(i, j, k);
		}

		@Override
		public Container getContainer(EntityPlayerMP player, World worldObj, TileEntity tileentity, int i, int j, int k) {
			return new ContainerBookBinder(player.inventory, (TileEntityBookBinder) tileentity);
		}

		@Override
		@SideOnly(Side.CLIENT)
		public GuiScreen getGuiScreen(EntityPlayer player, ByteBuf data) {
			int x = data.readInt();
			int y = data.readInt();
			int z = data.readInt();
			TileEntityBookBinder tileentity = (TileEntityBookBinder) player.worldObj.getTileEntity(x, y, z);
			return new GuiBookBinder(player.inventory, tileentity);
		}
	}

	private static final int	GuiID	= GuiHandlerManager.registerGuiNetHandler(new GuiHandlerBinder());

	private IIcon				iconTop;
	private IIcon				iconBottom;

	public BlockBookBinder(Material material) {
		super(material);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int i, int j) {
		if (i == 1) {
			return iconTop;
		} else if (i == 0) {
			return iconBottom;
		} else {
			return blockIcon;
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister par1IconRegister) {
		this.blockIcon = par1IconRegister.registerIcon("mystcraft:bookbinder_side");
		this.iconTop = par1IconRegister.registerIcon("mystcraft:bookbinder_top");
		this.iconBottom = par1IconRegister.registerIcon("mystcraft:bookbinder_bottom");
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public int getRenderType() {
		return -1;
	}

	@Override
	// world, x, y, z, player, side, origin?
	public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer entityplayer, int side, float posX, float posY, float posZ) {
		if (world.isRemote) { return true; }
		NetworkUtils.displayGui(entityplayer, world, GuiID, i, j, k);
		return true;

	}

	@Override
	public void breakBlock(World world, int i, int j, int k, Block block, int meta) {
		IInventory tileentity = (IInventory) world.getTileEntity(i, j, k);
		if (tileentity != null) {
			for (int l = 0; l < tileentity.getSizeInventory(); l++) {
				ItemStack itemstack = tileentity.getStackInSlot(l);
				if (itemstack == null) {
					continue;
				}
				float f = world.rand.nextFloat() * 0.8F + 0.1F;
				float f1 = world.rand.nextFloat() * 0.8F + 0.1F;
				float f2 = world.rand.nextFloat() * 0.8F + 0.1F;
				EntityItem entityitem = new EntityItem(world, i + f, j + f1, k + f2, itemstack);
				float f3 = 0.05F;
				entityitem.motionX = (float) world.rand.nextGaussian() * f3;
				entityitem.motionY = (float) world.rand.nextGaussian() * f3 + 0.2F;
				entityitem.motionZ = (float) world.rand.nextGaussian() * f3;
				world.spawnEntityInWorld(entityitem);
			}
		}
		super.breakBlock(world, i, j, k, block, meta);
	}

	@Override
	public void onBlockPlacedBy(World world, int i, int j, int k, EntityLivingBase entityliving, ItemStack itemstack) {
		int l = MathHelper.floor_double(((entityliving.rotationYaw * 4F) / 360F) + 0.5D) & 3;
		TileEntityBookBinder tileentity = (TileEntityBookBinder) world.getTileEntity(i, j, k);
		int rotation = 0;
		if (l == 3) {
			rotation = 90;
		} else if (l == 0) {
			rotation = 180;
		} else if (l == 1) {
			rotation = 270;
		} else if (l == 2) {
			rotation = 0;
		}
		tileentity.setYaw(rotation);
		tileentity.markDirty();
	}

	@Override
	public boolean rotateBlock(World worldObj, int x, int y, int z, ForgeDirection axis) {
		TileEntityBookBinder tileentity = (TileEntityBookBinder) worldObj.getTileEntity(x, y, z);
		if (tileentity == null) return false;
		tileentity.setYaw(tileentity.getYaw() + 90);
		tileentity.markDirty();
		return true;
	}

	@Override
	public ForgeDirection[] getValidRotations(World worldObj, int x, int y, int z) {
		return ForgeDirection.VALID_DIRECTIONS;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {
		return new TileEntityBookBinder();
	}
}
