package com.xcompwiz.mystcraft.effects;

import java.util.Random;

import com.xcompwiz.mystcraft.api.world.logic.IEnvironmentalEffect;

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
		int xPos = chunk.xPosition * 16;
		int zPos = chunk.zPosition * 16;

		ExtendedBlockStorage[] storagea = chunk.getBlockStorageArray();

		for (int k = 0; k < storagea.length; ++k) {
			ExtendedBlockStorage storage = storagea[k];

			if (storage != null && storage.getNeedsRandomTick()) {
				for (int i = 0; i < 3; ++i) {
					this.updateLCG = this.updateLCG * 3 + 1013904223;
					int bits = this.updateLCG >> 2;
					int x = bits & 15;
					int z = bits >> 8 & 15;
					int y = bits >> 16 & 15;
					IBlockState blockstate = storage.get(x, y, z);
					if (blockstate == null) continue;
					if (this.blockstate != null && this.blockstate != blockstate) continue;

					if (this.blockstate != null || blockstate.getBlock().getTickRandomly()) {
						blockstate.getBlock().randomTick(worldObj, new BlockPos(x + xPos, y + storage.getYLocation(), z + zPos), blockstate, worldObj.rand);
					}
				}
			}
		}
	}
}
