package com.xcompwiz.mystcraft.world.gen;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;

public class MapGenAdvanced {
	/** The number of Chunks to gen-check in any given direction. */
	protected int		range			= 8;

	/** The RNG used by the MapGen classes. */
	protected Random	rand			= new Random();

	private long		seed;

	private Block		block;
	private byte		blockMeta;

	private boolean		profiling		= false;
	private int			blockcount;
	private int			blockcounttotal	= 0;
	private int			callcount		= 0;
	private int			fillcount		= 0;

	public MapGenAdvanced(long seed, Block block, byte blockMeta) {
		this.seed = seed;
		this.block = block;
		this.blockMeta = blockMeta;
	}

	public void generate(IChunkProvider chunkprovider, World worldObj, int chunkX, int chunkZ, Block[] blocks, byte[] metadata) {
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
				this.recursiveGenerate(worldObj, x, z, chunkX, chunkZ, blocks, metadata);
			}
		}
		if (profiling) {
			if (blockcount > 0) ++fillcount;
			System.out.println(String.format("Using %s. AVG: [%f] / %d / %d TOT: [%d] GENFRQ: [%f:%f]", this.toString(), blockcounttotal / (float) fillcount, fillcount, callcount, blockcounttotal, fillcount / (float) callcount, blockcounttotal / (float) callcount));
		}
	}

	/**
	 * Recursively called by generate() (generate) and optionally by itself.
	 */
	protected void recursiveGenerate(World worldObj, int x, int z, int chunkX, int chunkZ, Block[] blocks, byte[] metadata) {}

	protected boolean placeBlock(Block[] blocks, byte[] metadata, int coords) {
		Block block = blocks[coords];

		if (this.block == null || (block != null && block.getMaterial().isLiquid())) return false;
		if (block == Blocks.bedrock) return false;
		++blockcounttotal;
		++blockcount;

		blocks[coords] = this.block;
		metadata[coords] = this.blockMeta;
		return true;
	}

	public void setProfiling(boolean profiling) {
		this.profiling = profiling;
	}
}
