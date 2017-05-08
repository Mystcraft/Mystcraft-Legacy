package com.xcompwiz.mystcraft.block;

import java.util.Random;

import com.xcompwiz.mystcraft.api.item.IItemPortalActivator;
import com.xcompwiz.mystcraft.portal.PortalUtils;
import com.xcompwiz.mystcraft.tileentity.TileEntityBookReceptacle;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBreakable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockLinkPortal extends BlockBreakable {

	public BlockLinkPortal() {
		super(Material.PORTAL, false);
		setTickRandomly(true);
		setBlockUnbreakable();
		setSoundType(SoundType.GLASS);
		setLightLevel(0.75F);
		setUnlocalizedName("myst.linkportal");
	}

	/**
	 * Returns a bounding box from the pool of bounding boxes (this means this box can change after the pool has been cleared to be reused)
	 */
	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int i) {
		return null;
	}

	/**
	 * Updates the blocks bounds based on its current state. Args: world, x, y, z
	 */
	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) {
		float xmin = 0.25F;
		float xmax = 0.75F;
		float ymin = 0.25F;
		float ymax = 0.75F;
		float zmin = 0.25F;
		float zmax = 0.75F;
		if (PortalUtils.isValidLinkPortalBlock(par1IBlockAccess.getBlock(par2 - 1, par3, par4)) > 0) {
			xmin = 0;
		}
		if (PortalUtils.isValidLinkPortalBlock(par1IBlockAccess.getBlock(par2 + 1, par3, par4)) > 0) {
			xmax = 1;
		}
		if (PortalUtils.isValidLinkPortalBlock(par1IBlockAccess.getBlock(par2, par3 - 1, par4)) > 0) {
			ymin = 0;
		}
		if (PortalUtils.isValidLinkPortalBlock(par1IBlockAccess.getBlock(par2, par3 + 1, par4)) > 0) {
			ymax = 1;
		}
		if (PortalUtils.isValidLinkPortalBlock(par1IBlockAccess.getBlock(par2, par3, par4 - 1)) > 0) {
			zmin = 0;
		}
		if (PortalUtils.isValidLinkPortalBlock(par1IBlockAccess.getBlock(par2, par3, par4 + 1)) > 0) {
			zmax = 1;
		}
		setBlockBounds(xmin, ymin, zmin, xmax, ymax, zmax);
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	//@Override
	//public boolean renderAsNormalBlock() {
	//	return false;
	//}

	//@Override
	//public int getRenderType() {
	//	return 0;
	//}

	//@Override //TODO Hellfire> custom iBlockColor impl
	//public int getBlockColor() {
	//	return 0x3333ff;
	//}
//
	//@Override
	//public int getRenderColor(int par1) {
	//	return getBlockColor();
	//}

	/**
	 * Returns a integer with hex for 0xrrggbb with this color multiplied against the blocks color. Note only called when first determining what to render.
	 */
	@SideOnly(Side.CLIENT)
	@Override
	public int colorMultiplier(IBlockAccess blockAccess, int i, int j, int k) {
		World world = Minecraft.getMinecraft().theWorld; //TODO: Referencing theWorld is problematic. Fix this.
		TileEntity entity = PortalUtils.getTileEntity(world, i, j, k);
		if (entity != null && entity instanceof TileEntityBookReceptacle) {
			TileEntityBookReceptacle book = (TileEntityBookReceptacle) entity;
			return book.getPortalColor();
		}
		return getBlockColor();
	}

	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		if (worldIn.isRemote) return;
		PortalUtils.validatePortal(worldIn, pos);
	}

	@Override
	public int quantityDropped(Random par1Random) {
		return 0;
	}

	@Override
	public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
		return layer == BlockRenderLayer.TRANSLUCENT;
	}

	@Override
	public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
		if(worldIn.isRemote) return;
		TileEntity te = PortalUtils.getTileEntity(worldIn, pos);
		if(te == null || !(te instanceof TileEntityBookReceptacle)) {
			worldIn.setBlockToAir(pos);
			return;
		}
		TileEntityBookReceptacle container = (TileEntityBookReceptacle) te;
		if(container.getBook().isEmpty()) {
			worldIn.setBlockToAir(pos);
			return;
		}
		ItemStack book = container.getBook();
		if(book.getItem() instanceof IItemPortalActivator) {
			((IItemPortalActivator) book.getItem()).onPortalCollision(book, worldIn, entityIn, pos);
		}
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		if (worldIn.isRemote) return;
		PortalUtils.validatePortal(worldIn, pos);
	}

	@Override
	public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
		super.onBlockAdded(worldIn, pos, state);
		if(worldIn.isRemote) return;
		//Hellfire> wtf are we doing here.
	}

	@Override
	public void breakBlock(World world, int i, int j, int k, Block block, int meta) {
		super.breakBlock(world, i, j, k, block, meta);
		// if(world.isRemote) return;
		// validate(world, new ChunkPos(i,j,k));
	}

	//	/**
	//	 * A randomly called display update to be able to add particles or other items for display
	//	 */
	//	@Override
	//	public void randomDisplayTick(World par1World, int par2, int par3, int par4, Random par5Random) {
	//		if (par5Random.nextInt(100) == 0) {
	//			par1World.markBlockForRenderUpdate(par2, par3, par4);
	//		}
	//	}
}
