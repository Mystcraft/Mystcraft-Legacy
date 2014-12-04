package com.xcompwiz.mystcraft.world.gen.feature;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.World;

public class WorldGenSpikesAdv extends WorldGeneratorAdv {
	private Block	block;
	private byte	metadata;

	public WorldGenSpikesAdv(Block block) {
		this(block, (byte) 0);
	}

	public WorldGenSpikesAdv(Block block, byte metadata) {
		this.block = block;
		this.metadata = metadata;
	}

	@Override
	public boolean doGeneration(World worldObj, Random rand, int baseX, int baseY, int baseZ) {
		if (worldObj.isAirBlock(baseX, baseY, baseZ)) {
			int height = rand.nextInt(32) + 6;
			int width = rand.nextInt(4) + 1;

			for (int x = baseX - width; x <= baseX + width; ++x) {
				for (int z = baseZ - width; z <= baseZ + width; ++z) {
					int dx = x - baseX;
					int dz = z - baseZ;

					if (dx * dx + dz * dz <= width * width + 1 && worldObj.isAirBlock(x, baseY - 1, z)) { return false; }
				}
			}

			for (int x = baseX - width; x <= baseX + width; ++x) {
				for (int z = baseZ - width; z <= baseZ + width; ++z) {
					int maxHeight = baseY + rand.nextInt(rand.nextInt(height) + 1) + 1;
					for (int y = baseY; y < maxHeight && y < 256; ++y) {
						int dx = x - baseX;
						int dz = z - baseZ;
						if (dx * dx + dz * dz <= width * width + 1) {
							placeBlock(worldObj, x, y, z, block, metadata, 2);
						}
					}
				}
			}
			return true;
		}
		return false;
	}
}
