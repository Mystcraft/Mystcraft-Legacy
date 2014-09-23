package com.xcompwiz.mystcraft.block;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import com.xcompwiz.mystcraft.client.gui.GuiHandlerManager;
import com.xcompwiz.mystcraft.client.gui.GuiLinkModifier;
import com.xcompwiz.mystcraft.inventory.ContainerLinkModifier;
import com.xcompwiz.mystcraft.network.NetworkUtils;
import com.xcompwiz.mystcraft.tileentity.TileEntityLinkModifier;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockLinkModifier extends BlockContainer {

	public static class GuiHandlerModifier extends GuiHandlerManager.GuiHandler {
		@Override
		public TileEntity getTileEntity(EntityPlayerMP player, World worldObj, int i, int j, int k) {
			return player.worldObj.getTileEntity(i, j, k);
		}

		@Override
		public Container getContainer(EntityPlayerMP player, World worldObj, TileEntity tileentity, int i, int j, int k) {
			return new ContainerLinkModifier(player.inventory, (TileEntityLinkModifier) tileentity);
		}

		@Override
		@SideOnly(Side.CLIENT)
		public GuiScreen getGuiScreen(EntityPlayer player, ByteBuf data) {
			int x = data.readInt();
			int y = data.readInt();
			int z = data.readInt();
			TileEntityLinkModifier tileentity = (TileEntityLinkModifier) player.worldObj.getTileEntity(x, y, z);
			return new GuiLinkModifier(player.inventory, tileentity);
		}
	}

	private static final int	GuiID	= GuiHandlerManager.registerGuiNetHandler(new GuiHandlerModifier());

	private IIcon				iconTop;
	private IIcon				iconSide2;
	private IIcon				iconBottom;

	public BlockLinkModifier(Material material) {
		super(material);
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

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int i, int j) {
		if (i == 1) {
			return iconTop;
		} else if (i == 0) {
			return iconBottom;
		} else if (i == 2 || i == 4) {
			return iconSide2;
		} else {
			return blockIcon;
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister par1IconRegister) {
		this.blockIcon = par1IconRegister.registerIcon("mystcraft:linkmodifier_side1");
		this.iconSide2 = par1IconRegister.registerIcon("mystcraft:linkmodifier_side2");
		this.iconTop = par1IconRegister.registerIcon("mystcraft:linkmodifier_top");
		this.iconBottom = par1IconRegister.registerIcon("mystcraft:linkmodifier_bottom");
	}

	@Override
	// world, x, y, z, player, side, origin?
	public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer entityplayer, int side, float posX, float pozY, float posZ) {
		if (world.isRemote) return true;
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
	public TileEntity createNewTileEntity(World world, int metadata) {
		return new TileEntityLinkModifier();
	}
}
