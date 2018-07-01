package com.xcompwiz.mystcraft.block;

import com.xcompwiz.mystcraft.core.MystcraftCommonProxy;
import com.xcompwiz.mystcraft.portal.PortalUtils;
import com.xcompwiz.mystcraft.tileentity.TileEntityBookReceptacle;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockCrystal extends Block {

	public static final PropertyEnum<EnumFacing> SOURCE_DIRECTION = PropertyEnum.create("source", EnumFacing.class);
	public static final PropertyBool IS_PART_OF_PORTAL = PropertyBool.create("active");

	public BlockCrystal() {
		super(Material.GLASS);
		setTickRandomly(false);
		useNeighborBrightness = true;
		setHardness(1F);
		setSoundType(SoundType.GLASS);
		setLightLevel(0.5F);
		setUnlocalizedName("myst.crystal");
		setCreativeTab(MystcraftCommonProxy.tabMystCommon);
		setDefaultState(this.blockState.getBaseState().withProperty(IS_PART_OF_PORTAL, false).withProperty(SOURCE_DIRECTION, EnumFacing.DOWN));
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		if (meta == 0) {
			return getDefaultState();
		} else {
			int sh = meta - 1;
			return getDefaultState().withProperty(IS_PART_OF_PORTAL, true).withProperty(SOURCE_DIRECTION, EnumFacing.values()[sh]);
		}
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		int side = state.getValue(SOURCE_DIRECTION).ordinal();
		return state.getValue(IS_PART_OF_PORTAL) ? side + 1 : 0;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, SOURCE_DIRECTION, IS_PART_OF_PORTAL);
	}

	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		if (worldIn.isRemote)
			return;
		if (state == getDefaultState())
			return;
		TileEntity tileEntity = PortalUtils.getTileEntity(worldIn, pos);
		if (tileEntity == null || !(tileEntity instanceof TileEntityBookReceptacle) || ((TileEntityBookReceptacle) tileEntity).getBook() == null) {
			worldIn.setBlockState(pos, getDefaultState(), 2);
			PortalUtils.shutdownPortal(worldIn, pos);
		}
	}

	@Override
	public boolean hasComparatorInputOverride(IBlockState state) {
		return true;
	}

	@Override
	public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos) {
		return blockState.getValue(IS_PART_OF_PORTAL) ? 15 : 0;
	}

}
