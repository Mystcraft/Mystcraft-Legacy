package com.xcompwiz.mystcraft.block;

import com.xcompwiz.mystcraft.tileentity.TileEntityBookstand;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class BlockBookstand extends BlockBookDisplay {

	private static float[][]	boxes	= new float[3][6];

	static {
		boxes[0] = new float[] { 0.35F, 0.0F, 0.35F, 0.65F, 0.2F, 0.65F };
		boxes[1] = new float[] { 0.45F, 0.1F, 0.45F, 0.55F, 0.5F, 0.45F };
		boxes[2] = new float[] { 0.15F, 0.4F, 0.15F, 0.85F, 0.7F, 0.85F };
	}

	public BlockBookstand() {
		super(Material.WOOD);
		setLightOpacity(0);
		useNeighborBrightness = true;
		//setBlockBounds(0.125F, 0F, 0.125F, 0.875F, 0.75F, 0.875F); //TODO Hellfire> reflect in bounding box return statements.
		setHardness(2F);
		setResistance(2F);
		setSoundType(SoundType.WOOD);
		setUnlocalizedName("myst.bookstand");
		setCreativeTab(CreativeTabs.DECORATIONS);
	}

	//@SideOnly(Side.CLIENT)
	//@Override
	//public void registerBlockIcons(IIconRegister par1IconRegister) {
	//	this.blockIcon = Blocks.crafting_table.getBlockTextureFromSide(0);
	//}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	//@Override
	//public boolean renderAsNormalBlock() {
	//	return false;
	//}

	//@Override
	//public int getRenderType() {
	//	return -1;
	//}

	/**
	 * Returns the bounding box of the wired rectangular prism to render.
	 */
	@Override
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World par1World, int par2, int par3, int par4) {
		setBlockBounds(0.25F, 0F, 0.25F, 0.75F, 0.25F, 0.75F);
		return AxisAlignedBB.getBoundingBox(par2 + this.minX, par3 + this.minY, par4 + this.minZ, par2 + this.maxX, par3 + this.maxY, par4 + this.maxZ);
	}

	/**
	 * Returns a bounding box from the pool of bounding boxes (this means this box can change after the pool has been cleared to be reused)
	 */
	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4) {
		// setBlockBounds(0.125F,0F,0.125F, 0.875F,0.75F,0.875F);
		return AxisAlignedBB.getBoundingBox(par2 + this.minX, par3 + this.minY, par4 + this.minZ, par2 + this.maxX, par3 + this.maxY, par4 + this.maxZ);
	}

	/**
	 * Ray traces through the blocks collision from start vector to end vector returning a ray trace hit. Args: world, x, y, z, startVec, endVec
	 */
	@Override
	public MovingObjectPosition collisionRayTrace(World worldObj, int x, int y, int z, Vec3 startVec3, Vec3 endVec3) {
		MovingObjectPosition[] hits = new MovingObjectPosition[boxes.length];
		MovingObjectPosition collision = null;

		for (int i = 0; i < boxes.length; ++i) {
			float[] box = boxes[i];
			setBlockBounds(box[0], box[1], box[2], box[3], box[4], box[5]);
			hits[i] = super.collisionRayTrace(worldObj, x, y, z, startVec3, endVec3);
		}

		double farthest = 0.0D;
		for (int i = 0; i < hits.length; ++i) {
			MovingObjectPosition hit = hits[i];
			if (hit != null) {
				double dist = hit.hitVec.squareDistanceTo(endVec3);

				if (dist > farthest) {
					collision = hit;
					farthest = dist;
					// float[] box = boxes[i];
					// setBlockBounds(box[0],box[1],box[2],box[3],box[4],box[5]);
				}
			}
		}

		return collision;
	}

	@Override
	public void onBlockPlacedBy(World world, int i, int j, int k, EntityLivingBase entityliving, ItemStack itemstack) {
		int l = MathHelper.floor_double(((entityliving.rotationYaw * 8F) / 360F) + 0.5D) & 7;
		TileEntityBookstand book = (TileEntityBookstand) world.getTileEntity(i, j, k);
		int rotation = 0;
		if (l == 2) {
			rotation = 270;
		} else if (l == 3) {
			rotation = 315;
		} else if (l == 4) {
			rotation = 0;
		} else if (l == 5) {
			rotation = 45;
		} else if (l == 6) {
			rotation = 90;
		} else if (l == 7) {
			rotation = 135;
		} else if (l == 0) {
			rotation = 180;
		} else if (l == 1) {
			rotation = 225;
		}
		book.setYaw(rotation);
		book.markDirty();
	}

	@Override
	public boolean rotateBlock(World worldObj, int x, int y, int z, ForgeDirection axis) {
		TileEntityBookstand book = (TileEntityBookstand) worldObj.getTileEntity(x, y, z);
		if (book == null) return false;
		book.setYaw(book.getYaw() + 45);
		book.markDirty();
		return true;
	}

	@Nullable
	@Override
	public EnumFacing[] getValidRotations(World world, BlockPos pos) {
		return EnumFacing.VALUES;
	}

	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileEntityBookstand();
	}

	//HellFire> obsolete. kept awkward legacy for edge cases.
	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {
		return new TileEntityBookstand();
	}

}
