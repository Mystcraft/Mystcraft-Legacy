package com.xcompwiz.mystcraft.world.gen;

import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunkProvider;

public class MapGenAdvanced {
	/** The number of Chunks to gen-check in any given direction. */
	protected int range = 8;

	/** The RNG used by the MapGen classes. */
	protected Random rand = new Random();

	private long seed;

	private IBlockState state;

	private boolean profiling = false;
	private int blockcount;
	private int blockcounttotal = 0;
	private int callcount = 0;
	private int fillcount = 0;

	public MapGenAdvanced(long seed, IBlockState state) {
		this.seed = seed;
		this.state = state;
	}

	public void generate(IChunkProvider chunkprovider, World worldObj, int chunkX, int chunkZ, ChunkPrimer primer) {
		int range = this.range;
		this.rand.setSeed(seed);
		long xseed = this.rand.nextLong();
		long zseed = this.rand.nextLong();

		blockcount = 0;
		++callcount;

		for (int x = chunkX - range; x <= chunkX + range; ++x) {
			for (int z = chunkZ - range; z <= chunkZ + range; ++z) {
				long xseed2 = x * xseed;
				long zseed2 = z * zseed;
				this.rand.setSeed(xseed2 ^ zseed2 ^ seed);
				this.recursiveGenerate(worldObj, x, z, chunkX, chunkZ, primer);
			}
		}
		if (profiling) {
			if (blockcount > 0)
				++fillcount;
			System.out.println(String.format("Using %s. AVG: [%f] / %d / %d TOT: [%d] GENFRQ: [%f:%f]", this.toString(), blockcounttotal / (float) fillcount, fillcount, callcount, blockcounttotal, fillcount / (float) callcount, blockcounttotal / (float) callcount));
		}
	}

	/**
	 * Recursively called by generate() (generate) and optionally by itself.
	 */
	protected void recursiveGenerate(World worldObj, int x, int z, int chunkX, int chunkZ, ChunkPrimer primer) {}

	protected boolean placeBlock(ChunkPrimer primer, int x, int y, int z) {
		return placeBlock(primer, x, y, z, this.state);
	}
	
	protected boolean placeBlock(ChunkPrimer primer, int x, int y, int z, IBlockState newState) {
		if (this.state == null) {
			return false;
		}

		IBlockState current = primer.getBlockState(x, y, z);
		if (current.getBlock().equals(Blocks.BEDROCK)) {
			return false;
		}
		if (!this.state.getMaterial().isSolid() && current.getMaterial().isLiquid()) {
			return false;
		}
		++blockcounttotal;
		++blockcount;

		primer.setBlockState(x, y, z, newState);
		return true;
	}

	public void setProfiling(boolean profiling) {
		this.profiling = profiling;
	}
}
