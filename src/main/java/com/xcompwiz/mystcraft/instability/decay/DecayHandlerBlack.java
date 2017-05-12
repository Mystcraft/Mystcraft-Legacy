package com.xcompwiz.mystcraft.instability.decay;

import java.util.Random;

import com.xcompwiz.mystcraft.block.BlockDecay;
import com.xcompwiz.mystcraft.data.ModBlocks;
import com.xcompwiz.mystcraft.entity.EntityFallingBlock;
import com.xcompwiz.mystcraft.world.WorldInfoUtils;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DecayHandlerBlack extends DecayHandler {

	@Override
	public String getIdentifier() {
		return "black";
	}

	@Override
	public void pulse(World world, BlockPos pos, Random random) {
		if (world.rand.nextInt(10) == 0) {
			decay(world, pos);
		} else if (world.rand.nextInt(5) == 0) {
			for (EnumFacing face : EnumFacing.HORIZONTALS) {
				corrupt(world, pos.offset(face));
			}
		}
	}

	@Override
	public void onBlockAdded(World world, BlockPos pos) {
		if (!world.isRemote) {
			if (world.getBlockState(pos).getBlock().equals(ModBlocks.decay)) {
				if (!WorldInfoUtils.isMystcraftAge(world)) {
					world.setBlockToAir(pos);
					return;
				}
				if (!WorldInfoUtils.isInstabilityEnabled(world)) {
					return;
				}
				if (world.getBlockState(pos.up()) == this.getBlockState()) {
					world.setBlockToAir(pos);
					EntityFallingBlock.drop(world, pos.up());
				}
				state = world.getBlockState(pos.up());
				if (state.getBlock().equals(ModBlocks.decay) && ModBlocks.decay.getMetaFromState(state) == this.getMetadata()) {
					EntityFallingBlock.drop(world, pos);
					world.setBlockToAir(pos.up());
				}
			}
		}
	}

	private void decay(World world, BlockPos pos) {
		for (EnumFacing face : EnumFacing.HORIZONTALS) {
			corrupt(world, pos.offset(face));
		}
		world.setBlockToAir(pos.down());
		EntityFallingBlock.drop(world, pos);
	}

	private void corrupt(World world, BlockPos pos) {
		if (world.getBlockState(pos).getMaterial().isLiquid()) {
			world.setBlockToAir(pos);
		}
		if (!world.isAirBlock(pos)) {
			world.setBlockState(pos, ModBlocks.decay.getDefaultState().withProperty(BlockDecay.DECAY_META, getMetadata()));
			addInstability(world, 1);
		}
	}
}
