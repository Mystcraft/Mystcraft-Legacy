package com.xcompwiz.mystcraft.block;

import com.xcompwiz.mystcraft.core.MystcraftCommonProxy;
import com.xcompwiz.mystcraft.tileentity.TileEntityLectern;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockLectern extends BlockBookDisplay {

	public static final PropertyEnum<EnumFacing> ROTATION = PropertyEnum.create("facing", EnumFacing.class, EnumFacing.HORIZONTALS);

	public BlockLectern() {
		super(Material.WOOD);
		setLightOpacity(255);
		useNeighborBrightness = true;
		setHardness(2F);
		setResistance(2F);
		setSoundType(SoundType.WOOD);
		setUnlocalizedName("myst.lectern");
		setCreativeTab(MystcraftCommonProxy.tabMystCommon);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(ROTATION, EnumFacing.getHorizontal(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(ROTATION).getHorizontalIndex();
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, ROTATION);
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
		return getDefaultState().withProperty(ROTATION, placer.getHorizontalFacing());
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return new AxisAlignedBB(0, 0, 0, 1, 0.4375, 1);
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileEntityLectern();
	}

	//HellFire> obsolete. kept awkward legacy for edge cases.
	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {
		return new TileEntityLectern();
	}
}
