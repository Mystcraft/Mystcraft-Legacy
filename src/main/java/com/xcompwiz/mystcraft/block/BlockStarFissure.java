package com.xcompwiz.mystcraft.block;

import java.util.List;
import java.util.Random;

import com.xcompwiz.mystcraft.Mystcraft;
import com.xcompwiz.mystcraft.api.event.StarFissureLinkEvent;
import com.xcompwiz.mystcraft.api.hook.LinkPropertyAPI;
import com.xcompwiz.mystcraft.api.linking.ILinkInfo;
import com.xcompwiz.mystcraft.data.Sounds;
import com.xcompwiz.mystcraft.linking.LinkController;
import com.xcompwiz.mystcraft.linking.LinkOptions;
import com.xcompwiz.mystcraft.tileentity.TileEntityStarFissure;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import javax.annotation.Nullable;

public class BlockStarFissure extends BlockContainer {

	private static LinkOptions defaultstarfissure = new LinkOptions(null);

	static {
		defaultstarfissure.setDimensionUID(Mystcraft.homeDimension);
		defaultstarfissure.setFlag(LinkPropertyAPI.FLAG_NATURAL, true);
		defaultstarfissure.setFlag(LinkPropertyAPI.FLAG_EXTERNAL, true);
		defaultstarfissure.setProperty(LinkPropertyAPI.PROP_SOUND, Sounds.FISSURELINK);
	}

	public BlockStarFissure() {
		super(Material.PORTAL);
		setLightLevel(0.4F);
		setTickRandomly(false);
		setBlockUnbreakable();
		setUnlocalizedName("myst.starfissure");
	}

	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileEntityStarFissure();
	}

	//HellFire> obsolete. kept awkward legacy for edge cases.
	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {
		return new TileEntityStarFissure();
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return new AxisAlignedBB(0, 0, 0, 0, 0, 0);
	}

	@Override
	public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean p_185477_7_) {}

	@Override
	public boolean causesSuffocation(IBlockState state) {
		return false;
	}

	@Override
	public boolean isCollidable() {
		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public int quantityDropped(Random random) {
		return 0;
	}

    @Override
    public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity) {
        ILinkInfo info = defaultstarfissure.clone();
        info.setSpawnYaw(entity.rotationYaw);
        MinecraftForge.EVENT_BUS.post(new StarFissureLinkEvent(world, entity, info));
        LinkController.travelEntity(world, entity, info);
    }

	@Override
	public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {}

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {}

	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {}

}
