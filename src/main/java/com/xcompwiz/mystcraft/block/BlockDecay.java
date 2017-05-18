package com.xcompwiz.mystcraft.block;

import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;
import com.xcompwiz.mystcraft.instability.decay.DecayHandler;
import com.xcompwiz.mystcraft.instability.decay.DecayHandler.DecayType;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockDecay extends Block {

	public static final PropertyEnum<DecayType> DECAY_META = PropertyEnum.create("decay", DecayType.class);

	public BlockDecay() {
		super(Material.SAND);
		setTickRandomly(false);
		setSoundType(SoundType.SAND);
		setUnlocalizedName("myst.unstable");
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(DECAY_META, DecayType.values()[meta]);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
        return state.getValue(DECAY_META).ordinal();
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, DECAY_META);
	}

	@Override
	public void onBlockDestroyedByPlayer(World worldIn, BlockPos pos, IBlockState state) {
		super.onBlockDestroyedByPlayer(worldIn, pos, state);
		getDecayHandler(state.getValue(DECAY_META)).onBlockDestroyedByPlayer(worldIn, pos);
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		if(!worldIn.isRemote) {
			getDecayHandler(state.getValue(DECAY_META)).updateTick(worldIn, pos, rand);
		}
	}

	@Override
	public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
		super.onBlockAdded(worldIn, pos, state);
		getDecayHandler(state.getValue(DECAY_META)).onBlockAdded(worldIn, pos);
	}

	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		return Lists.newArrayList();
	}

	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
		return new ItemStack(this, 1, getMetaFromState(state));
	}

	@Override
	public float getExplosionResistance(World world, BlockPos pos, Entity exploder, Explosion explosion) {
		return getDecayHandler(world.getBlockState(pos).getValue(DECAY_META)).getExplosionResistance(world, pos, exploder, explosion);
	}

	@Override
	public float getBlockHardness(IBlockState blockState, World worldIn, BlockPos pos) {
		return getDecayHandler(blockState.getValue(DECAY_META)).getBlockHardness(worldIn, pos);
	}

	@Override
	public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
		return lightValue;
	}

	@Override
	public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn) {
		getDecayHandler(worldIn.getBlockState(pos).getValue(DECAY_META)).onEntityContact(worldIn, pos, entityIn);
	}

	@Override
	public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
		getDecayHandler(state.getValue(DECAY_META)).onEntityContact(worldIn, pos, entityIn);
	}

	private DecayHandler getDecayHandler(DecayType decayType) {
		DecayHandler handler = DecayHandler.getHandler(decayType);
		if (handler == null) handler = DecayHandler.getHandler(DecayType.BLACK);
		return handler;
	}
}
