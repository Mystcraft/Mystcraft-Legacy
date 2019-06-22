package com.xcompwiz.mystcraft.block;

import java.util.Random;

import javax.annotation.Nullable;

import com.xcompwiz.mystcraft.api.item.IItemPortalActivator;
import com.xcompwiz.mystcraft.core.MystcraftCommonProxy;
import com.xcompwiz.mystcraft.data.ModBlocks;
import com.xcompwiz.mystcraft.portal.PortalUtils;
import com.xcompwiz.mystcraft.tileentity.IOInventory;
import com.xcompwiz.mystcraft.tileentity.TileEntityBookReceptacle;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class BlockBookReceptacle extends BlockContainer {

	public static final PropertyEnum<EnumFacing> ROTATION = PropertyEnum.create("rotation", EnumFacing.class);

	public BlockBookReceptacle() {
		super(Material.GLASS);
		setTickRandomly(false);
		useNeighborBrightness = true;
		setHardness(1F);
		setSoundType(SoundType.GLASS);
		setUnlocalizedName("myst.receptacle");
		setCreativeTab(MystcraftCommonProxy.tabMystCommon);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(ROTATION, EnumFacing.values()[meta]);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(ROTATION).ordinal();
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, ROTATION);
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public int quantityDropped(Random par1Random) {
		return 1;
	}

	@Override
	public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side) {
		if (side == EnumFacing.DOWN) {
			return false;
		}
		if (!worldIn.getBlockState(pos.offset(side.getOpposite())).getBlock().equals(ModBlocks.crystal)) {
			return false;
		}
		return canPlaceBlockAt(worldIn, pos);
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		switch (state.getValue(ROTATION)) {
		case UP:
			return new AxisAlignedBB(0, 0, 0, 1, 0.375, 1);
		case DOWN:
			return new AxisAlignedBB(0, 1 - 0.375, 0, 1, 1, 1);
		case NORTH:
			return new AxisAlignedBB(0, 0, 1 - 0.375, 1, 1, 1);
		case SOUTH:
			return new AxisAlignedBB(0, 0, 0, 1, 1, 0.375);
		case WEST:
			return new AxisAlignedBB(1 - 0.375, 0, 0, 1, 1, 1);
		case EAST:
			return new AxisAlignedBB(0, 0, 0, 0.375, 1, 1);
		}
		return FULL_BLOCK_AABB;
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
		return getDefaultState().withProperty(ROTATION, facing);
	}

	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		BlockPos receptable = PortalUtils.getReceptacleBase(pos, state.getValue(ROTATION));
		if (!worldIn.getBlockState(receptable).getBlock().equals(ModBlocks.crystal)) {
			dropBlockAsItem(worldIn, pos, state, 0);
			worldIn.setBlockToAir(pos);
		}
		super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		if (!worldIn.isRemote) {
			TileEntityBookReceptacle book = (TileEntityBookReceptacle) worldIn.getTileEntity(pos);
			if (book != null) {
				ItemStack in = book.getBook();
				if (!in.isEmpty()) {
					EntityItem i = new EntityItem(worldIn, pos.getX(), pos.getY(), pos.getZ(), in);
					worldIn.spawnEntity(i);
				}
			}
		}
		super.breakBlock(worldIn, pos, state);
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		TileEntityBookReceptacle book = (TileEntityBookReceptacle) worldIn.getTileEntity(pos);
		if (book == null) {
			return false;
		}
		ItemStack in = book.getBook();
		if (in.isEmpty()) {
			in = playerIn.getHeldItem(hand);
			if (!in.isEmpty() && in.getItem() instanceof IItemPortalActivator) {
				if (!worldIn.isRemote) {
					playerIn.setHeldItem(hand, ItemStack.EMPTY);
					book.setBook(in);
				}
				return true;
			}
		} else {
			if (!worldIn.isRemote && playerIn.getHeldItem(hand).isEmpty()) {
				playerIn.setHeldItem(hand, in);
				book.setBook(ItemStack.EMPTY);
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileEntityBookReceptacle();
	}

	//HellFire> obsolete. kept awkward legacy for edge cases.
	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {
		return new TileEntityBookReceptacle();
	}

	@Override
	public boolean hasComparatorInputOverride(IBlockState state) {
		return true;
	}

	@Override
	public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos) {
		TileEntity te = worldIn.getTileEntity(pos);
		if (te != null) {
			IItemHandler cap = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.DOWN);
			if (cap != null && cap instanceof IOInventory) {
				return ((IOInventory) cap).calcRedstoneFromInventory();
			}
		}
		return 0;
	}

}
