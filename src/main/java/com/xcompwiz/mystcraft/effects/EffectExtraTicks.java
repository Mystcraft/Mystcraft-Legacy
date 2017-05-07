package com.xcompwiz.mystcraft.effects;

import java.util.Random;

import com.xcompwiz.mystcraft.api.world.logic.IEnvironmentalEffect;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

public class EffectExtraTicks implements IEnvironmentalEffect {

	private int		updateLCG	= (new Random()).nextInt();
	private Block	block;
	private Integer	metadata;

	public EffectExtraTicks() {}

	public EffectExtraTicks(Block block, Integer metadata) {
		this.block = block;
		this.metadata = metadata;
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
					Block block = storage.getBlockByExtId(x, y, z);
					if (block == null) continue;
					int metadata = storage.getExtBlockMetadata(x, y, z);
					if (this.block != null && this.block != block) continue;
					if (this.metadata != null && this.metadata != metadata) continue;

					if (this.block != null || block.getTickRandomly()) {
						block.updateTick(worldObj, x + xPos, y + storage.getYLocation(), z + zPos, worldObj.rand);
					}
				}
			}
		}
	}
}
