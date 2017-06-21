package com.xcompwiz.mystcraft.world.gen.feature;

import java.util.Random;

import com.xcompwiz.mystcraft.data.ModBlocks;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenMystStarFissure extends WorldGenerator {

	@Override
	public boolean generate(World world, Random random, BlockPos pos) {
		int[][] noise = generateNoise(random);
		for (int row = 0; row < noise.length; ++row) {
			int x = noise[row][0];
			for (; x <= noise[row][1]; ++x) {
				set(world, pos.add(x, 0, row * 2));
				set(world, pos.add(x, 0, row * 2 + 1));
			}
		}
		return true;
	}

	private void set(World world, BlockPos pos) {
		world.setBlockState(pos.up(), ModBlocks.starfissure.getDefaultState());
		pos = pos.up();
		for (; pos.getY() < world.getHeight();) {
			pos = pos.up();
			world.setBlockToAir(pos);
		}
	}

	private int[][] generateNoise(Random rand) {
		int length = rand.nextInt(8) + 10;
		int[][] noise = new int[length][2];
		noise[0][0] = noise[0][1] = 0;
		for (int row = 1; row < noise.length; ++row) {
			int scale = (noise.length) + Math.min(row, noise.length - row) + 1;
			noise[row][1] = rand.nextInt(scale) - (scale >> 1);
			noise[row][1] = (noise[row][1] + noise[row - 1][1]) / 2;
			noise[row][0] = rand.nextInt(scale) - (scale >> 1);
			noise[row][0] = (noise[row][0] + noise[row - 1][0]) / 2;
			if (noise[row][0] > noise[row][1]) {
				int temp = noise[row][0];
				noise[row][0] = noise[row][1];
				noise[row][1] = temp;
			}
			if (noise[row][0] > noise[row - 1][1]) {
				noise[row][0] = noise[row - 1][1];
			}
			if (noise[row][1] < noise[row - 1][0]) {
				noise[row][1] = noise[row - 1][0];
			}
		}
		return noise;
	}
	// int width = random.nextInt(length>>2)+Math.min(row, length-row)+1;
	// int column = -width>>1;
	// width += column;
	// column += random.nextInt((width)+1);
	// width -= random.nextInt((width)+1);
	// for (; column <= width; ++column) {
	// set(world, i+column, j, k+row);
	// }
}
