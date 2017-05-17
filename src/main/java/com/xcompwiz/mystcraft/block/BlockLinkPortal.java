package com.xcompwiz.mystcraft.block;

import java.util.List;
import java.util.Random;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.xcompwiz.mystcraft.api.item.IItemPortalActivator;
import com.xcompwiz.mystcraft.data.ModBlocks;
import com.xcompwiz.mystcraft.portal.PortalUtils;
import com.xcompwiz.mystcraft.tileentity.TileEntityBookReceptacle;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBreakable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class BlockLinkPortal extends BlockBreakable {

    public static final PropertyEnum<EnumFacing> SOURCE_DIRECTION = PropertyEnum.create("source", EnumFacing.class);
	public static final PropertyBool IS_PART_OF_PORTAL = PropertyBool.create("active");

	public static final PropertyEnum<EnumFacing.Axis> RENDER_ROTATION = PropertyEnum.create("renderface", EnumFacing.Axis.class); //If, render specific facing
	public static final PropertyBool HAS_ROTATION = PropertyBool.create("hasface"); //If not, render full cube

	public BlockLinkPortal() {
		super(Material.PORTAL, false);
		setTickRandomly(true);
		setBlockUnbreakable();
		setSoundType(SoundType.GLASS);
		setLightLevel(0.75F);
		setUnlocalizedName("myst.linkportal");
		setDefaultState(this.blockState.getBaseState()
                .withProperty(HAS_ROTATION, false).withProperty(RENDER_ROTATION, EnumFacing.Axis.X) //Doesn't matter normally, but is required.
                .withProperty(IS_PART_OF_PORTAL, false).withProperty(SOURCE_DIRECTION, EnumFacing.DOWN));
	}

    @Override
    public IBlockState getStateFromMeta(int meta) {
		if(meta == 0) {
			return getDefaultState();
		} else {
			int sh = meta >> 1;
			return getDefaultState().withProperty(IS_PART_OF_PORTAL, true).withProperty(SOURCE_DIRECTION, EnumFacing.values()[sh]);
		}
    }

    @Override
    public int getMetaFromState(IBlockState state) {
		int side = state.getValue(SOURCE_DIRECTION).ordinal();
		return state.getValue(IS_PART_OF_PORTAL) ? side << 1 : 0;
    }

    @Override
    protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this,
                SOURCE_DIRECTION, IS_PART_OF_PORTAL, HAS_ROTATION, RENDER_ROTATION);
    }

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        List<EnumFacing.Axis> validAxis = Lists.newArrayList();

        boolean has = true;
        EnumFacing offset = EnumFacing.NORTH;
        EnumFacing.Axis axis = EnumFacing.Axis.X;
        for (int i = 0; i < 4; i++) {
            offset = offset.rotateAround(axis);
            if(!isPortalBlock(worldIn.getBlockState(pos.offset(offset)))) {
                has = false;
                break;
            }
        }
        if(has) {
            validAxis.add(axis);
        }

        has = true;
        offset = EnumFacing.NORTH;
        axis = EnumFacing.Axis.Y;
        for (int i = 0; i < 4; i++) {
            offset = offset.rotateAround(axis);
            if(!isPortalBlock(worldIn.getBlockState(pos.offset(offset)))) {
                has = false;
                break;
            }
        }
        if(has) {
            validAxis.add(axis);
        }

        has = true;
        offset = EnumFacing.UP;
        axis = EnumFacing.Axis.Z;
        for (int i = 0; i < 4; i++) {
            offset = offset.rotateAround(axis);
            if(!isPortalBlock(worldIn.getBlockState(pos.offset(offset)))) {
                has = false;
                break;
            }
        }
        if(has) {
            validAxis.add(axis);
        }
        state = state.withProperty(HAS_ROTATION, validAxis.size() == 1);
        if(validAxis.size() == 1) {
            state = state.withProperty(RENDER_ROTATION, validAxis.get(0));
        }
		return state;
	}

	private boolean isPortalBlock(IBlockState state) {
	    return state.getBlock().equals(ModBlocks.portal) || state.getBlock().equals(ModBlocks.crystal);
    }

	@Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        return NULL_AABB;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        float xmin = 0.25F;
        float xmax = 0.75F;
        float ymin = 0.25F;
        float ymax = 0.75F;
        float zmin = 0.25F;
        float zmax = 0.75F;
        if (PortalUtils.isValidLinkPortalBlock(source.getBlockState(pos.offset(EnumFacing.WEST))) > 0) {
            xmin = 0;
        }
        if (PortalUtils.isValidLinkPortalBlock(source.getBlockState(pos.offset(EnumFacing.EAST))) > 0) {
            xmax = 1;
        }
        if (PortalUtils.isValidLinkPortalBlock(source.getBlockState(pos.offset(EnumFacing.DOWN))) > 0) {
            ymin = 0;
        }
        if (PortalUtils.isValidLinkPortalBlock(source.getBlockState(pos.offset(EnumFacing.UP))) > 0) {
            ymax = 1;
        }
        if (PortalUtils.isValidLinkPortalBlock(source.getBlockState(pos.offset(EnumFacing.NORTH))) > 0) {
            zmin = 0;
        }
        if (PortalUtils.isValidLinkPortalBlock(source.getBlockState(pos.offset(EnumFacing.SOUTH))) > 0) {
            zmax = 1;
        }
        return new AxisAlignedBB(xmin, ymin, zmin, xmax, ymax, zmax);
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
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
	    if(!blockState.getValue(HAS_ROTATION)) {
	        IBlockState offset = blockAccess.getBlockState(pos.offset(side));
	        offset = offset.getActualState(blockAccess, pos.offset(side));
	        if(isPortalBlock(offset) && (offset.getBlock().equals(ModBlocks.crystal) || !offset.getValue(HAS_ROTATION))) {
	            return false;
            }
            return true;
        }
        return super.shouldSideBeRendered(blockState, blockAccess, pos, side);
    }

    //Hellfire> referenced now from ModBlocks -> custom IBlockColor implementation
	@SideOnly(Side.CLIENT)
	public static int colorMultiplier(IBlockAccess blockAccess, BlockPos pos) {
		TileEntity entity = PortalUtils.getTileEntity(blockAccess, pos);
		if (entity != null && entity instanceof TileEntityBookReceptacle) {
			TileEntityBookReceptacle book = (TileEntityBookReceptacle) entity;
			return book.getPortalColor();
		}
		return 0x3333FF;
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
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        super.breakBlock(worldIn, pos, state);
        // if(world.isRemote) return;
        // validate(world, pos);
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
