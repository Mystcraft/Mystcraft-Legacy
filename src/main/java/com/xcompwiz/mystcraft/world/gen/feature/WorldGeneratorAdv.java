package com.xcompwiz.mystcraft.world.gen.feature;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public abstract class WorldGeneratorAdv extends WorldGenerator {
	private boolean	profiling		= false;
	private int		blockcount;
	private int		blockcounttotal	= 0;
	private int		callcount		= 0;
	private int		fillcount		= 0;

	public void setProfiling(boolean profiling) {
		this.profiling = profiling;
	}

	public final void noGen() {
		++callcount;
	}

	@Override
	public final boolean generate(World worldObj, Random rand, int x, int y, int z) {
		blockcount = 0;
		++callcount;

		boolean flag = doGeneration(worldObj, rand, x, y, z);

		if (profiling) {
			if (blockcount > 0) ++fillcount;
			System.out.println(String.format("Using %s. AVG: [%f] / %d / %d TOT: [%d] GENFRQ: [%f:%f]", this.toString(), blockcounttotal / (float) fillcount, fillcount, callcount, blockcounttotal, fillcount / (float) callcount, blockcounttotal / (float) callcount));
		}
		return flag;
	}

	public abstract boolean doGeneration(World worldObj, Random rand, int x, int y, int z);

	protected final boolean placeBlock(World worldObj, int x, int y, int z, Block block, int metadata, int mask) {
		++blockcounttotal;
		++blockcount;
		worldObj.setBlock(x, y, z, block, metadata, mask);
		return true;
	}
}
