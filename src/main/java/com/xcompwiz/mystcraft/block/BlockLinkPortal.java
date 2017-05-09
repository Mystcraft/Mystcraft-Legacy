package com.xcompwiz.mystcraft.block;

import java.util.Random;

import com.xcompwiz.mystcraft.api.item.IItemPortalActivator;
import com.xcompwiz.mystcraft.portal.PortalUtils;
import com.xcompwiz.mystcraft.tileentity.TileEntityBookReceptacle;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBreakable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class BlockLinkPortal extends BlockBreakable {

    public static final PropertyEnum<EnumFacing> SOURCE_DIRECTION = PropertyEnum.create("source", EnumFacing.class);

	public BlockLinkPortal() {
		super(Material.PORTAL, false);
		setTickRandomly(true);
		setBlockUnbreakable();
		setSoundType(SoundType.GLASS);
		setLightLevel(0.75F);
		setUnlocalizedName("myst.linkportal");
		setDefaultState(this.blockState.getBaseState().withProperty(SOURCE_DIRECTION, EnumFacing.UP));
	}

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(SOURCE_DIRECTION, EnumFacing.values()[meta]);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(SOURCE_DIRECTION).ordinal();
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, SOURCE_DIRECTION);
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
