package com.xcompwiz.mystcraft.instability.decay;

import java.util.Random;

import com.xcompwiz.mystcraft.data.ModBlocks;
import com.xcompwiz.mystcraft.entity.EntityFallingBlock;
import com.xcompwiz.mystcraft.world.WorldInfoUtils;

import net.minecraft.init.Blocks;
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
			corrupt(world, pos.west());
			corrupt(world, pos.east());
			corrupt(world, pos.north());
			corrupt(world, pos.south());
		}
	}

	@Override
	public void onBlockAdded(World world, BlockPos pos) {
		if (!world.isRemote) {
			if (world.getBlockState(pos).getBlock() == ModBlocks.decay) {
				if (!WorldInfoUtils.isMystcraftAge(world)) {
					world.setBlockToAir(pos);
					return;
				}
				if (!WorldInfoUtils.isInstabilityEnabled(world)) { return; }
				if (world.getBlockState(pos.down()) == this.getBlockState()) {
					world.setBlockToAir(pos);
					EntityFallingBlock.drop(world, pos);
					return;
				}
				if (world.getBlockState(pos.up()) == this.getBlockState()) {
					world.setBlockToAir(pos);
					EntityFallingBlock.drop(world, pos.up());
					return;
				}
			}
		}
	}

	private void decay(World world, BlockPos pos) {
		corrupt(world, pos.west());
		corrupt(world, pos.east());
		corrupt(world, pos.north());
		corrupt(world, pos.south());
		world.setBlockToAir(pos.down());
		EntityFallingBlock.drop(world, pos);
	}

	private void corrupt(World world, BlockPos pos) {
		if (world.getBlockState(pos).getMaterial().isLiquid()) {
			world.setBlockToAir(pos);
		}
		if (!world.isAirBlock(pos)) {
			world.setBlockState(pos, this.getBlockState(), 3);
			addInstability(world, 1);
		}
	}
}
