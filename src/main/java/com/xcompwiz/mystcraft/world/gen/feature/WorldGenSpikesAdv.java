package com.xcompwiz.mystcraft.world.gen.feature;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WorldGenSpikesAdv extends WorldGeneratorAdv {

	private IBlockState state;

	public WorldGenSpikesAdv(Block block) {
		this(block.getDefaultState());
	}

	public WorldGenSpikesAdv(IBlockState state) {
		this.state = state;
	}

	@Override
	public boolean doGeneration(World worldObj, Random rand, BlockPos base) {
		if (worldObj.isAirBlock(base)) {
			int height = rand.nextInt(32) + 6;
			int width = rand.nextInt(4) + 1;

			for (int x = base.getX() - width; x <= base.getX() + width; ++x) {
				for (int z = base.getZ() - width; z <= base.getZ() + width; ++z) {
					int dx = x - base.getX();
					int dz = z - base.getZ();

					if (dx * dx + dz * dz <= width * width + 1 && worldObj.isAirBlock(new BlockPos(x, base.getY() - 1, z))) {
						return false;
					}
				}
			}

			for (int x = base.getX() - width; x <= base.getX() + width; ++x) {
				for (int z = base.getZ() - width; z <= base.getZ() + width; ++z) {
					int maxHeight = base.getY() + rand.nextInt(rand.nextInt(height) + 1) + 1;
					for (int y = base.getY(); y < maxHeight && y < 256; ++y) {
						int dx = x - base.getX();
						int dz = z - base.getZ();
						if (dx * dx + dz * dz <= width * width + 1) {
							placeBlock(worldObj, new BlockPos(x, y, z), state, 2);
						}
					}
				}
			}
			return true;
		}
		return false;
	}
}
