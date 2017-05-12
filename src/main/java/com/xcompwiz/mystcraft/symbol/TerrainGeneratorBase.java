package com.xcompwiz.mystcraft.symbol;

import java.util.Random;

import com.xcompwiz.mystcraft.api.world.AgeDirector;
import com.xcompwiz.mystcraft.api.world.logic.ITerrainGenerator;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.chunk.ChunkPrimer;

public abstract class TerrainGeneratorBase implements ITerrainGenerator {

	protected AgeDirector controller;

	protected Random bedrockGen;
	private double noise_field[];

	protected IBlockState seablock = Blocks.WATER.getDefaultState();
	protected IBlockState fillblock = Blocks.STONE.getDefaultState();
	protected boolean genBedrock = true;

	private boolean profiling = false;
	private int fillcounttotal = 0;
	private int seacounttotal = 0;
	private int chunkcount = 0;
	private int fillchunk = 0;
	private int seachunk = 0;

	public TerrainGeneratorBase(AgeDirector controller) {
		super();
		this.controller = controller;
		bedrockGen = new Random(controller.getSeed());
	}

	public void setProfiling(boolean profiling) {
		this.profiling = profiling;
	}

	public void setTerrainBlock(IBlockState state) {
		this.fillblock = state;
	}

	public void setSeaBlock(IBlockState state) {
		this.seablock = state;
	}

	@Override
	public void generateTerrain(int chunkX, int chunkZ, ChunkPrimer primer) {
		int fillcount = 0;
		int seacount = 0;
		int xzstep = 4;
		byte width = (byte) (16 / xzstep);
		int ystep = 8;
		int height = 128 / ystep;
		int sealevel = controller.getSeaLevel();
		int width2a = width + 1;
		int height2 = height + 1;
		int width2b = width + 1;
		double noise_ystep_factor = 0.125D;
		final double noise_xzstep_factor = 0.25D;
		noise_field = initializeNoiseField(noise_field, chunkX * width, 0, chunkZ * width, width2a, height2, width2b);

		for (int largeY = 0; largeY < height; ++largeY) {
			for (int largeZ = 0; largeZ < width; ++largeZ) {
				for (int largeX = 0; largeX < width; ++largeX) {
					double x0z0y0 = noise_field[((largeX + 0) * width2b + (largeZ + 0)) * height2 + (largeY + 0)];
					double x0z1y0 = noise_field[((largeX + 0) * width2b + (largeZ + 1)) * height2 + (largeY + 0)];
					double x1z0y0 = noise_field[((largeX + 1) * width2b + (largeZ + 0)) * height2 + (largeY + 0)];
					double x1z1y0 = noise_field[((largeX + 1) * width2b + (largeZ + 1)) * height2 + (largeY + 0)];
					double x0z0yd = (noise_field[((largeX + 0) * width2b + (largeZ + 0)) * height2 + (largeY + 1)] - x0z0y0) * noise_ystep_factor;
					double x0z1yd = (noise_field[((largeX + 0) * width2b + (largeZ + 1)) * height2 + (largeY + 1)] - x0z1y0) * noise_ystep_factor;
					double x1z0yd = (noise_field[((largeX + 1) * width2b + (largeZ + 0)) * height2 + (largeY + 1)] - x1z0y0) * noise_ystep_factor;
					double x1z1yd = (noise_field[((largeX + 1) * width2b + (largeZ + 1)) * height2 + (largeY + 1)] - x1z1y0) * noise_ystep_factor;
					double x0z0yc = x0z0y0;
					double x0z1yc = x0z1y0;
					double x1z0yc = x1z0y0;
					double x1z1yc = x1z1y0;

					int y = largeY * ystep;
					for (int subY = 0; subY < ystep; ++subY) {
						double x0zcyc = x0z0yc;
						double x1zcyc = x1z0yc;
						double x0zdyc = (x0z1yc - x0z0yc) * noise_xzstep_factor;
						double x1zdyc = (x1z1yc - x1z0yc) * noise_xzstep_factor;

						int z = largeZ * xzstep;
						for (int subZ = 0; subZ < xzstep; ++subZ) {
							double xczcyc = x0zcyc;
							double xdzcyc = (x1zcyc - x0zcyc) * noise_xzstep_factor;

							int x = largeX * xzstep;
							for (int subX = 0; subX < xzstep; ++subX) {

								IBlockState block = Blocks.AIR.getDefaultState();
								if (genBedrock && y <= bedrockGen.nextInt(5)) {
									block = Blocks.BEDROCK.getDefaultState();
								} else if (xczcyc > 0.0D) {
									block = fillblock;
									++fillcount;
								} else if (y < sealevel) {
									block = seablock;
									++seacount;
								}

								primer.setBlockState(x, y, z, block);
								xczcyc += xdzcyc;
								++x;
							}

							x0zcyc += x0zdyc;
							x1zcyc += x1zdyc;
							++z;
						}

						x0z0yc += x0z0yd;
						x0z1yc += x0z1yd;
						x1z0yc += x1z0yd;
						x1z1yc += x1z1yd;
						++y;
					}
				}
			}
		}
		if (profiling) {
			++chunkcount;
			fillcounttotal += fillcount;
			seacounttotal += seacount;
			if (fillcount > 0) ++fillchunk;
			if (seacount > 0) ++seachunk;
			System.out.println(String.format("Using %s. AVG: [%f] [%f] / %d TOT: [%d] [%d] CNKFRQ: [%f:%f] [%f:%f]", this.toString(), fillcounttotal / (float) chunkcount, seacounttotal / (float) chunkcount, chunkcount, fillcounttotal, seacounttotal, fillchunk / (float) chunkcount, fillcounttotal / (float) fillchunk, seachunk / (float) chunkcount, seacounttotal / (float) seachunk));
		}
	}

	protected abstract double[] initializeNoiseField(double noise_field[], int subchunkX, int subchunkY, int subchunkZ, int sizeX, int sizeY, int sizeZ);

}
