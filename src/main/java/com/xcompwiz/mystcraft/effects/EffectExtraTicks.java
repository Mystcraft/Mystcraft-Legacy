package com.xcompwiz.mystcraft.effects;

import java.util.Random;

import com.xcompwiz.mystcraft.api.world.logic.IEnvironmentalEffect;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

public class EffectExtraTicks implements IEnvironmentalEffect {

	private int updateLCG = (new Random()).nextInt();
	private IBlockState blockstate;

	public EffectExtraTicks() {}

	public EffectExtraTicks(IBlockState blockstate) {
		this.blockstate = blockstate;
	}

	@Override
	public void tick(World worldObj, Chunk chunk) {
		int xPos = chunk.x * 16;
		int zPos = chunk.z * 16;

		ExtendedBlockStorage[] storagea = chunk.getBlockStorageArray();

		for (int k = 0; k < storagea.length; ++k) {
			ExtendedBlockStorage storage = storagea[k];

			if (storage != null && storage.needsRandomTick()) {
				for (int i = 0; i < 3; ++i) {
					this.updateLCG = this.updateLCG * 3 + 1013904223;
					int bits = this.updateLCG >> 2;
					int x = bits & 15;
					int z = bits >> 8 & 15;
					int y = bits >> 16 & 15;
					IBlockState state = storage.get(x, y, z);
					if (this.block != null && this.block != state.getBlock()) continue;
					if (this.metadata != null && this.metadata != state.getBlock().getMetaFromState(state)) continue;

					if(state.getBlock().getTickRandomly()) {
						state.getBlock().updateTick(worldObj, new BlockPos(x, y, z).add(xPos, storage.getYLocation(), zPos), state, worldObj.rand);
					}
				}
			}
		}
	}
}
