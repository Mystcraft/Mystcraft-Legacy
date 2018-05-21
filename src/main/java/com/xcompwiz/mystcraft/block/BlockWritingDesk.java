package com.xcompwiz.mystcraft.block;

import java.util.ArrayList;
import java.util.List;

import com.xcompwiz.mystcraft.Mystcraft;
import com.xcompwiz.mystcraft.data.ModBlocks;
import com.xcompwiz.mystcraft.data.ModGUIs;
import com.xcompwiz.mystcraft.data.ModItems;
import com.xcompwiz.mystcraft.tileentity.TileEntityDesk;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;

public class BlockWritingDesk extends Block {

	public static final PropertyEnum<EnumFacing> ROTATION = PropertyEnum.create("facing", EnumFacing.class, EnumFacing.HORIZONTALS);
	public static final PropertyBool IS_TOP = PropertyBool.create("istop");
	public static final PropertyBool IS_FOOT = PropertyBool.create("isfoot");

	private static final int headFootMap[][] = { { 0, 1 }, { -1, 0 }, { 0, -1 }, { 1, 0 } };

	public BlockWritingDesk() {
		super(Material.WOOD);
		setHardness(2.5F);
		setSoundType(SoundType.WOOD);
		setUnlocalizedName("myst.writing_desk");
		setDefaultState(this.blockState.getBaseState().withProperty(ROTATION, EnumFacing.NORTH).withProperty(IS_TOP, false).withProperty(IS_FOOT, false));
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		int t = isBlockTop(state) ? 1 : 0;
		int f = isBlockFoot(state) ? 1 : 0;
		int horizontal = getDirectionFromMetadata(state).getHorizontalIndex();
		return (t << 3) | (f << 2) | (horizontal & 3);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		boolean t = ((meta >> 3) & 1) != 0;
		boolean f = ((meta >> 2) & 1) != 0;
		int horizontalIndex = meta & 3;
		return getDefaultState().withProperty(IS_TOP, t).withProperty(IS_FOOT, f).withProperty(ROTATION, EnumFacing.HORIZONTALS[horizontalIndex]);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, ROTATION, IS_TOP, IS_FOOT);
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		float xmin = 0.0F;
		float xmax = 1.0F;
		float ymin = 0.0F;
		float ymax = 1.0F;
		float zmin = 0.0F;
		float zmax = 1.0F;
		if(isBlockTop(state)) {
			ymax = 0.75F;
			int dirInt = getDirectionFromMetadata(state).getHorizontalIndex();
			if (dirInt == 0) {
				xmin = 0.5F;
			}
			if (dirInt == 1) {
				zmin = 0.5F;
			}
			if (dirInt == 2) {
				xmax = 0.5F;
			}
			if (dirInt == 3) {
				zmax = 0.5F;
			}
		}
		return new AxisAlignedBB(xmin, ymin, zmin, xmax, ymax, zmax);
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return !isBlockTop(state);
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		if(isBlockTop(state) && !isBlockFoot(state)) {
			if(!worldIn.getBlockState(pos.down()).getBlock().equals(this)) {
				if(!worldIn.isRemote) {
					dropBlockAsItem(worldIn, pos, state, 0);
				}
				worldIn.setBlockToAir(pos);
			}
		}
		EnumFacing face = getDirectionFromMetadata(state);
		int dirInt = face.getHorizontalIndex();
		if (isBlockFoot(state)) {
			if(!worldIn.getBlockState(pos.add(-headFootMap[dirInt][0], 0, -headFootMap[dirInt][1])).getBlock().equals(this)) {
				worldIn.setBlockToAir(pos);
			}
		} else if(!worldIn.getBlockState(pos.add(headFootMap[dirInt][0], 0, headFootMap[dirInt][1])).getBlock().equals(this)) {
			worldIn.setBlockToAir(pos);
		}
	}

	@Override
	public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
		if (player.capabilities.isCreativeMode && (!isBlockTop(state)) && world.getBlockState(pos.up()).getBlock().equals(this) && isBlockTop(world.getBlockState(pos.up()))) {
			world.setBlockToAir(pos.up());
			EnumFacing direction = getDirectionFromMetadata(state);
			int dirInt = direction.getHorizontalIndex();
			if (isBlockFoot(state)) {
				BlockPos offset = pos.add(-headFootMap[dirInt][0], 1, -headFootMap[dirInt][1]);
				world.setBlockToAir(offset);
			} else {
				BlockPos offset = pos.add(headFootMap[dirInt][0], 1, headFootMap[dirInt][1]);
				world.setBlockToAir(offset);
			}
		}
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (world.isRemote) {
		    return true;
        }
		playerIn.openGui(Mystcraft.instance, ModGUIs.WRITING_DESK.ordinal(), world, pos.getX(), pos.getY(), pos.getZ());
		return true;
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		TileEntity tileentity = world.getTileEntity(pos);
		if (tileentity != null && tileentity instanceof TileEntityDesk) {
			IItemHandler handle = ((TileEntityDesk) tileentity).getContainerItemHandler();
			for (int l = 0; l < handle.getSlots(); l++) {
				ItemStack itemstack = handle.getStackInSlot(l);
				if (itemstack.isEmpty()) {
					continue;
				}
				float f =  world.rand.nextFloat() * 0.8F + 0.1F;
				float f1 = world.rand.nextFloat() * 0.8F + 0.1F;
				float f2 = world.rand.nextFloat() * 0.8F + 0.1F;
				EntityItem entityitem = new EntityItem(world, pos.getX() + f, pos.getY() + f1, pos.getZ() + f2, itemstack);
				float f3 = 0.05F;
				entityitem.motionX = (float) world.rand.nextGaussian() * f3;
				entityitem.motionY = (float) world.rand.nextGaussian() * f3 + 0.2F;
				entityitem.motionZ = (float) world.rand.nextGaussian() * f3;
				world.spawnEntity(entityitem);
			}
		}
		super.breakBlock(world, pos, state);
	}

	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
		if (isBlockTop(state)) {
			ret.add(new ItemStack(ModItems.desk, 1, 1));
		} else {
			ret.add(new ItemStack(ModItems.desk));
		}
		return ret;
	}

	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
		if(isBlockTop(state)) {
			return new ItemStack(ModItems.desk, 1, 1);
		}
		return new ItemStack(ModItems.desk);
	}

	public static EnumFacing getMetadataFromDirection(int i, int j) {
		return EnumFacing.getHorizontal(getMetadataFromDirectionInternal(i, j));
	}

	private static int getMetadataFromDirectionInternal(int i, int j) {
		if (j == 1) {
			return 0;
		} else if (i == -1) {
			return 1;
		} else if (j == -1) {
			return 2;
		} else if (i == 1) {
			return 3;
		}
		return 0;
	}

	public static EnumFacing getDirectionFromMetadata(IBlockState state) {
		if(!state.getBlock().equals(ModBlocks.writingdesk)) return EnumFacing.NORTH; //Uh.....
		return state.getValue(ROTATION);
	}

	public static boolean isBlockFoot(IBlockState state) {
		return state.getBlock().equals(ModBlocks.writingdesk) && state.getValue(IS_FOOT);
	}

	public static boolean isBlockTop(IBlockState state) {
		return state.getBlock().equals(ModBlocks.writingdesk) && state.getValue(IS_TOP);
	}

	public static TileEntityDesk getTileEntity(World world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		if (isBlockTop(state)) { return getTileEntity(world, pos.down()); }
		if (isBlockFoot(state)) {
			EnumFacing direction = getDirectionFromMetadata(state);
			int dirInt = direction.getHorizontalIndex();
			BlockPos at = pos.add(-headFootMap[dirInt][0], 0, -headFootMap[dirInt][1]);
			return (TileEntityDesk) world.getTileEntity(at);
		}
		return (TileEntityDesk) world.getTileEntity(pos);
	}

	@Override
	public boolean hasTileEntity(IBlockState state) {
		return !isBlockFoot(state) && !isBlockTop(state);
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileEntityDesk();
	}

	@Override
	public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
		super.onBlockAdded(worldIn, pos, state);
		if(hasTileEntity(state)) {
			worldIn.setTileEntity(pos, createTileEntity(worldIn, state));
		}
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		if(!worldIn.getBlockState(pos).getBlock().equals(this)) return;
		EnumFacing face = placer.getHorizontalFacing(); //TODO Hellfire> Test if this actually still behaves the way it did.
		int facing = face.getHorizontalIndex();

		int xOffset = 0;
		int zOffset = 0;
		if (facing == 0) {
			zOffset = 1;
		}
		if (facing == 1) {
			xOffset = -1;
		}
		if (facing == 2) {
			zOffset = -1;
		}
		if (facing == 3) {
			xOffset = 1;
		}
		BlockPos offset = pos.add(xOffset, 0, zOffset);
		if(worldIn.isAirBlock(offset)) {
			worldIn.setBlockState(offset, getDefaultState().withProperty(ROTATION, face).withProperty(IS_FOOT, true));
		} else {
			worldIn.setBlockToAir(pos);
		}
	}

	@Override
	public boolean eventReceived(IBlockState state, World worldIn, BlockPos pos, int id, int param) {
		TileEntity te = worldIn.getTileEntity(pos);
		if(te != null) {
			te.receiveClientEvent(id, param);
		}
		return false;
	}

}
