package com.xcompwiz.mystcraft.instability.decay;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;

import com.xcompwiz.mystcraft.data.ModBlocks;
import com.xcompwiz.mystcraft.entity.EntityFallingBlock;
import com.xcompwiz.mystcraft.world.WorldInfoHelper;

public class DecayHandlerBlack extends DecayHandler {

	@Override
	public String getIdentifier() {
		return "black";
	}

	@Override
	public void pulse(World world, int x, int y, int z, Random random) {
		if (world.rand.nextInt(10) == 0) {
			decay(world, x, y, z);
		} else if (world.rand.nextInt(5) == 0) {
			corrupt(world, x - 1, y, z);
			corrupt(world, x + 1, y, z);
			corrupt(world, x, y, z - 1);
			corrupt(world, x, y, z + 1);
		}
	}

	@Override
	public void onBlockAdded(World world, int i, int j, int k) {
		if (!world.isRemote) {
			if (world.getBlock(i, j, k) == ModBlocks.decay) {
				if (!WorldInfoHelper.isMystcraftAge(world)) {
					world.setBlock(i, j, k, Blocks.air);
					return;
				}
				if (!WorldInfoHelper.isInstabilityEnabled(world)) { return; }
				if (world.getBlock(i, j - 1, k) == ModBlocks.decay && world.getBlockMetadata(i, j - 1, k) == this.getMetadata()) {
					world.setBlock(i, j - 1, k, Blocks.air);
					EntityFallingBlock.drop(world, i, j, k);
					return;
				}
				if (world.getBlock(i, j + 1, k) == ModBlocks.decay && world.getBlockMetadata(i, j - 1, k) == this.getMetadata()) {
					world.setBlock(i, j, k, Blocks.air);
					EntityFallingBlock.drop(world, i, j + 1, k);
					return;
				}
			}
		}
	}

	private void decay(World world, int i, int j, int k) {
		corrupt(world, i - 1, j, k);
		corrupt(world, i + 1, j, k);
		corrupt(world, i, j, k - 1);
		corrupt(world, i, j, k + 1);
		world.setBlock(i, j - 1, k, Blocks.air);
		EntityFallingBlock.drop(world, i, j, k);
	}

	private void corrupt(World world, int i, int j, int k) {
		if (world.getBlock(i, j, k).getMaterial().isLiquid()) {
			world.setBlock(i, j, k, Blocks.air);
		}
		if (!world.isAirBlock(i, j, k)) {
			world.setBlock(i, j, k, ModBlocks.decay, this.getMetadata(), 3);
			addInstability(world, 1);
		}
	}
}
