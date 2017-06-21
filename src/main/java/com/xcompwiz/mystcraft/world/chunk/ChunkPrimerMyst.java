package com.xcompwiz.mystcraft.world.chunk;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.chunk.ChunkPrimer;

public class ChunkPrimerMyst extends ChunkPrimer {
	
	public boolean inBiomeDecoration = false;

	@Override
    public void setBlockState(int x, int y, int z, IBlockState state) {
		if (state.getBlock() == Blocks.BEDROCK) { // Logic to prevent biomes from generating bedrock along world bottom
			if (y <= 5 && inBiomeDecoration)
				return;
		}
        super.setBlockState(x, y, z, state);
    }
}
