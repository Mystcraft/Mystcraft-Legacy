package com.xcompwiz.mystcraft.world;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class MystEmptyChunk extends Chunk {

	public MystEmptyChunk(World worldIn, int x, int z) {
		super(worldIn, x, z);
	}

	public boolean isAtLocation(int x, int z) {
		return x == this.x && z == this.z;
	}

	@Override
	public int getHeightValue(int x, int z) {
		return 0;
	}

	@Override
	protected void generateHeightMap() {}

	@Override
	public void generateSkylightMap() {}

	@Override
	public IBlockState getBlockState(BlockPos pos) {
		return Blocks.AIR.getDefaultState();
	}

	@Override
	public int getBlockLightOpacity(BlockPos pos) {
		return 255;
	}

	@Override
	public int getLightFor(EnumSkyBlock p_177413_1_, BlockPos pos) {
		return p_177413_1_.defaultLightValue;
	}

	@Override
	public void setLightFor(EnumSkyBlock p_177431_1_, BlockPos pos, int value) {}

	@Override
	public int getLightSubtracted(BlockPos pos, int amount) {
		return 0;
	}

	@Override
	public void addEntity(Entity entityIn) {}

	@Override
	public void removeEntity(Entity entityIn) {}

	@Override
	public void removeEntityAtIndex(Entity entityIn, int index) {}

	@Override
	public boolean canSeeSky(BlockPos pos) {
		return false;
	}

	@Nullable
	@Override
	public TileEntity getTileEntity(BlockPos pos, EnumCreateEntityType p_177424_2_) {
		return null;
	}

	@Override
	public void addTileEntity(TileEntity tileEntityIn) {}

	@Override
	public void addTileEntity(BlockPos pos, TileEntity tileEntityIn) {}

	@Override
	public void removeTileEntity(BlockPos pos) {}

	@Override
	public void onUnload() {
		super.onUnload();
	}

	@Override
	public void onLoad() {
		super.onLoad();
	}

	@Override
	public void setModified(boolean modified) {}

	@Override
	public <T extends Entity> void getEntitiesOfTypeWithinAABB(Class<? extends T> entityClass, AxisAlignedBB aabb, List<T> listToFill, Predicate<? super T> filter) {}

	@Override
	public void getEntitiesWithinAABBForEntity(@Nullable Entity entityIn, AxisAlignedBB aabb, List<Entity> listToFill, Predicate<? super Entity> p_177414_4_) {}

	public boolean needsSaving(boolean p_76601_1_) {
		return false;
	}

	public Random getRandomWithSeed(long seed) {
		return new Random(this.getWorld().getSeed() + (long) (this.x * this.x * 4987142) + (long) (this.x * 5947611) + (long) (this.z * this.z) * 4392871L + (long) (this.z * 389711) ^ seed);
	}

	@Override
	public boolean isEmpty() {
		return true;
	}

	@Override
	public boolean isEmptyBetween(int startY, int endY) {
		return true;
	}

}
