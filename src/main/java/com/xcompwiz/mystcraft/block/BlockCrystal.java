package com.xcompwiz.mystcraft.block;

import com.xcompwiz.mystcraft.portal.PortalUtils;
import com.xcompwiz.mystcraft.tileentity.TileEntityBookReceptacle;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockCrystal extends Block {

	public BlockCrystal() {
		super(Material.GLASS);
		setTickRandomly(false);
		useNeighborBrightness = true;
		setHardness(1F);
		setSoundType(SoundType.GLASS);
		setLightLevel(0.5F);
		setUnlocalizedName("myst.crystal");
		setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
	}

	//@SideOnly(Side.CLIENT)
	//@Override
	//public void registerBlockIcons(IIconRegister par1IconRegister) {
	//	this.blockIcon = par1IconRegister.registerIcon("mystcraft:crystal");
	//}

	// /**
	// * Returns a integer with hex for 0xrrggbb with this color multiplied against the blocks color. Note only called
	// * when first determining what to render.
	// */
	// @SideOnly(Side.CLIENT)
	// @Override
	// public int colorMultiplier(IBlockAccess blockAccess, int i, int j, int k)
	// {
	// int metadata = blockAccess.getBlockMetadata(i, j, k);
	// if (metadata == 0) return 0x000000;
	// if (metadata == 1) return 0x0000FF;
	// if (metadata == 2) return 0xFF00FF;
	// if (metadata == 3) return 0x00FF00;
	// if (metadata == 4) return 0xFFFF00;
	// if (metadata == 5) return 0x00FFFF;
	// if (metadata == 6) return 0xFF0000;
	// return getBlockColor();
	// }

	/**
	 * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are their own) Args: x, y, z, neighbor
	 * blockID
	 */
	@Override
	public void onNeighborBlockChange(World world, int i, int j, int k, Block block) {
		if (world.isRemote) return;
		if (world.getBlockMetadata(i, j, k) == 0) return;
		TileEntity tileentity = PortalUtils.getTileEntity(world, i, j, k);
		if (tileentity == null || !(tileentity instanceof TileEntityBookReceptacle) || ((TileEntityBookReceptacle) tileentity).getBook() == null) {
			world.setBlockMetadataWithNotify(i, j, k, 0, 2);
			PortalUtils.shutdownPortal(world, i, j, k);
			return;
		}
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
	public int getComparatorInputOverride(World world, int i, int j, int k, int par5) {
		if (world.getBlockMetadata(i, j, k) != 0) return 15;
		return 0;
	}
}
