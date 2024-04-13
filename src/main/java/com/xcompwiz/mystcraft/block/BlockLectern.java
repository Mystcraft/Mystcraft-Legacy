package com.xcompwiz.mystcraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.xcompwiz.mystcraft.tileentity.TileEntityLectern;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockLectern extends BlockBookDisplay {

	public BlockLectern(Material material) {
		super(material);
		setLightOpacity(255);
		canBlockGrass = true;
		useNeighborBrightness = true;
		setBlockBounds(0F, 0F, 0F, 1F, 0.4375F, 1F);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister par1IconRegister) {
		this.blockIcon = Blocks.crafting_table.getBlockTextureFromSide(0);
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
	public void onBlockPlacedBy(World world, int i, int j, int k, EntityLivingBase entityliving, ItemStack itemstack) {
		int l = MathHelper.floor_double(((entityliving.rotationYaw * 4F) / 360F) + 0.5D) & 3;
		TileEntityLectern book = (TileEntityLectern) world.getTileEntity(i, j, k);
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
		book.setYaw(rotation);
		book.markDirty();
	}

	@Override
	public boolean rotateBlock(World worldObj, int x, int y, int z, ForgeDirection axis) {
		TileEntityLectern book = (TileEntityLectern) worldObj.getTileEntity(x, y, z);
		if (book == null) return false;
		book.setYaw(book.getYaw() + 90);
		book.markDirty();
		return true;
	}

	@Override
	public ForgeDirection[] getValidRotations(World worldObj, int x, int y, int z) {
		return ForgeDirection.VALID_DIRECTIONS;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {
		return new TileEntityLectern();
	}
}
