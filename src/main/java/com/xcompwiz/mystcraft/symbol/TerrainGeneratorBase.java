package com.xcompwiz.mystcraft.symbol;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

import com.xcompwiz.mystcraft.api.world.IAgeController;
import com.xcompwiz.mystcraft.api.world.logic.ITerrainGenerator;

public abstract class TerrainGeneratorBase implements ITerrainGenerator {

	protected IAgeController	controller;

	protected Random			bedrockGen;
	private double				noise_field[];

	protected Block				seablock		= Blocks.water;
	protected byte				seameta			= 0;
	protected Block				fillblock		= Blocks.stone;
	protected byte				fillmeta		= 0;
	protected boolean			genBedrock		= true;

	private boolean				profiling		= false;
	private int					fillcounttotal	= 0;
	private int					seacounttotal	= 0;
	private int					chunkcount		= 0;
	private int					fillchunk		= 0;
	private int					seachunk		= 0;

	public TerrainGeneratorBase(IAgeController controller) {
		super();
		this.controller = controller;
		bedrockGen = new Random(controller.getSeed());
	}

	public void setProfiling(boolean profiling) {
		this.profiling = profiling;
	}

	public void setTerrainBlock(Block block, byte metadata) {
		this.fillblock = block;
		this.fillmeta = metadata;
	}

	public void setSeaBlock(Block block, byte metadata) {
		this.seablock = block;
		this.seameta = metadata;
	}

	@Override
	public void generateTerrain(int chunkX, int chunkZ, Block[] blocks, byte[] metadata) {
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

							int coords = y << 8 | z << 4 | largeX * xzstep;
							for (int subX = 0; subX < xzstep; ++subX) {

								Block block = Blocks.air;
								byte meta = 0;
								if (genBedrock && y <= 0 + bedrockGen.nextInt(5)) {
									block = Blocks.bedrock;
								} else if (xczcyc > 0.0D) {
									block = fillblock;
									meta = fillmeta;
									++fillcount;
								} else if (y < sealevel) {
									block = seablock;
									meta = seameta;
									++seacount;
								}

								blocks[coords] = block;
								metadata[coords] = meta;
								xczcyc += xdzcyc;
								++coords;
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
