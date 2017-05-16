package com.xcompwiz.mystcraft.block;

import com.xcompwiz.mystcraft.tileentity.TileEntityBookstand;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.*;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockBookstand extends BlockBookDisplay {

	public static final PropertyInteger ROTATION_INDEX = PropertyInteger.create("rotindex", 0, 7);

	private static float[][] boxes = new float[3][6];

	static {
		boxes[0] = new float[] { 0.35F, 0.0F, 0.35F, 0.65F, 0.2F, 0.65F };
		boxes[1] = new float[] { 0.45F, 0.1F, 0.45F, 0.55F, 0.5F, 0.45F };
		boxes[2] = new float[] { 0.15F, 0.4F, 0.15F, 0.85F, 0.7F, 0.85F };
	}

	public BlockBookstand() {
		super(Material.WOOD);
		setLightOpacity(0);
		useNeighborBrightness = true;
		setHardness(2F);
		setResistance(2F);
		setSoundType(SoundType.WOOD);
		setUnlocalizedName("myst.bookstand");
		setCreativeTab(CreativeTabs.DECORATIONS);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(ROTATION_INDEX);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(ROTATION_INDEX, MathHelper.clamp(meta, 0, 7));
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, ROTATION_INDEX);
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return new AxisAlignedBB(0.125, 0, 0.125, 0.875, 0.75, 0.875);
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Nullable
	@Override
	public RayTraceResult collisionRayTrace(IBlockState blockState, World worldIn, BlockPos pos, Vec3d start, Vec3d end) {
		RayTraceResult[] hits = new RayTraceResult[boxes.length];
		RayTraceResult hit = null;

		for (int i = 0; i < boxes.length; i++) {
			float[] box = boxes[i];
			hits[i] = super.rayTrace(pos, start, end, new AxisAlignedBB(box[0], box[1], box[2], box[3], box[4], box[5]));
		}

		double farthest = 0D;
		for (int i = 0; i < hits.length; i++) {
			RayTraceResult c = hits[i];
			if(c != null) {
				double dst = c.hitVec.squareDistanceTo(end);
				if(dst > farthest) {
					hit = c;
					farthest = dst;
				}
			}
		}

		return hit;
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
		int rot = MathHelper.floor(((placer.rotationYaw * 8F) / 360F) + 0.5D) & 7;
		return getStateFromMeta(rot);
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
