package com.xcompwiz.mystcraft.symbol.symbols;

import com.xcompwiz.mystcraft.api.symbol.BlockCategory;
import com.xcompwiz.mystcraft.api.symbol.BlockDescriptor;
import com.xcompwiz.mystcraft.api.symbol.ModifierUtils;
import com.xcompwiz.mystcraft.api.world.AgeDirector;
import com.xcompwiz.mystcraft.api.world.logic.ITerrainGenerator;
import com.xcompwiz.mystcraft.symbol.SymbolBase;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.chunk.ChunkPrimer;

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
			gen.setSeaBlock(block.blockstate);
		}
		block = ModifierUtils.popBlockMatching(controller, BlockCategory.TERRAIN);
		if (block != null) {
			gen.setTerrainBlock(block.blockstate);
		}
		controller.registerInterface(gen);
	}

	private static class TerrainGenerator implements ITerrainGenerator {

		private AgeDirector	controller;
		private IBlockState fillblock	= Blocks.STONE.getDefaultState();
		private IBlockState	seablock	= Blocks.WATER.getDefaultState();

		public TerrainGenerator(AgeDirector controller) {
			this.controller = controller;
		}

		public void setTerrainBlock(IBlockState state) {
			this.fillblock = state;
		}

		public void setSeaBlock(IBlockState state) {
			this.seablock = state;
		}

		@Override
		public void generateTerrain(int chunkX, int chunkZ, ChunkPrimer primer) {
			int plane = controller.getAverageGroundLevel();
			int sealevel = controller.getSeaLevel();
			int layers = 256;

			for (int y = 0; y < layers; ++y) {
				IBlockState blockId = Blocks.AIR.getDefaultState();
				if (y == 0) {
					blockId = Blocks.BEDROCK.getDefaultState();
				} else if (y < plane) {
					blockId = fillblock;
				} else if (y <= sealevel) {
					blockId = seablock;
				}
				for (int z = 0; z < 16; ++z) {
					for (int x = 0; x < 16; ++x) {
						primer.setBlockState(x, y, z, blockId);
					}
				}
			}
		}
	}
}
