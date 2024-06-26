package com.xcompwiz.mystcraft.world.gen.feature;

import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public abstract class WorldGeneratorAdv extends WorldGenerator {

	private boolean profiling = false;
	private int blockcount;
	private int blockcounttotal = 0;
	private int callcount = 0;
	private int fillcount = 0;

	public void setProfiling(boolean profiling) {
		this.profiling = profiling;
	}

	public final void noGen() {
		++callcount;
	}

	@Override
	public final boolean generate(World worldObj, Random rand, BlockPos pos) {
		blockcount = 0;
		++callcount;

		boolean flag = doGeneration(worldObj, rand, pos);

		if (profiling) {
			if (blockcount > 0)
				++fillcount;
			System.out.println(String.format("Using %s. AVG: [%f] / %d / %d TOT: [%d] GENFRQ: [%f:%f]", this.toString(), blockcounttotal / (float) fillcount, fillcount, callcount, blockcounttotal, fillcount / (float) callcount, blockcounttotal / (float) callcount));
		}
		return flag;
	}

	public abstract boolean doGeneration(World worldObj, Random rand, BlockPos pos);

	protected final boolean placeBlock(World worldObj, BlockPos pos, IBlockState state, int flags) {
		++blockcounttotal;
		++blockcount;
		worldObj.setBlockState(pos, state, flags);
		return true;
	}
}
