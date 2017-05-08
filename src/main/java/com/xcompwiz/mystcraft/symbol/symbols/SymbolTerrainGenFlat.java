package com.xcompwiz.mystcraft.symbol.symbols;

import com.xcompwiz.mystcraft.api.symbol.BlockCategory;
import com.xcompwiz.mystcraft.api.symbol.BlockDescriptor;
import com.xcompwiz.mystcraft.api.symbol.ModifierUtils;
import com.xcompwiz.mystcraft.api.world.AgeDirector;
import com.xcompwiz.mystcraft.api.world.logic.ITerrainGenerator;
import com.xcompwiz.mystcraft.symbol.SymbolBase;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

public class SymbolTerrainGenFlat extends SymbolBase {

	public SymbolTerrainGenFlat(String identifier) {
		super(identifier);
	}

	@Override
	public void registerLogic(AgeDirector controller, long seed) {
		TerrainGenerator gen = new TerrainGenerator(controller);
		BlockDescriptor block;
		block = ModifierUtils.popBlockMatching(controller, BlockCategory.SEA);
		if (block != null) {
			gen.setSeaBlock(block.block, block.metadata);
		}
		block = ModifierUtils.popBlockMatching(controller, BlockCategory.TERRAIN);
		if (block != null) {
			gen.setTerrainBlock(block.block, block.metadata);
		}
		controller.registerInterface(gen);
	}

	private static class TerrainGenerator implements ITerrainGenerator {
		private AgeDirector	controller;
		private Block			fillblock	= Blocks.STONE;
		private byte			fillmeta	= 0;
		private Block			seablock	= Blocks.WATER;
		private byte			seameta		= 0;

		public TerrainGenerator(AgeDirector controller) {
			this.controller = controller;
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
			int plane = controller.getAverageGroundLevel();
			int sealevel = controller.getSeaLevel();
			int layers = blocks.length / 256;

			for (int y = 0; y < layers; ++y) {
				Block blockId = Blocks.AIR;
				byte meta = 0;
				if (y == 0) {
					blockId = Blocks.bedrock;
				} else if (y < plane) {
					blockId = fillblock;
					meta = fillmeta;
				} else if (y <= sealevel) {
					blockId = seablock;
					meta = seameta;
				}
				for (int z = 0; z < 16; ++z) {
					for (int x = 0; x < 16; ++x) {
						blocks[y << 8 | z << 4 | x] = blockId;
						metadata[y << 8 | z << 4 | x] = meta;
					}
				}
			}
		}
	}
}
