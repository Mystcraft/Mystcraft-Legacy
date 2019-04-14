package com.xcompwiz.mystcraft.world.chunk;

import com.google.common.collect.Sets;
import com.xcompwiz.mystcraft.api.world.logic.IPrimerFilter;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.chunk.ChunkPrimer;

import java.util.Collection;

public class ChunkPrimerMyst extends ChunkPrimer {

	public boolean inBiomeDecoration = false;
	private Collection<IPrimerFilter> filters = Sets.newHashSet();

	public void addFilter(IPrimerFilter filter) {
		this.filters.add(filter);
	}

	@Override
	public void setBlockState(int x, int y, int z, IBlockState state) {
		if (state.getBlock() == Blocks.BEDROCK) { // Logic to prevent biomes from generating bedrock along world bottom
			if (y <= 5 && inBiomeDecoration) {
				return;
			}
		}

		for (IPrimerFilter filter : this.filters) {
			if ((state = filter.filter(x, y, z, state)) == null) {
				return;
			}
		}
		super.setBlockState(x, y, z, state);
	}
}
