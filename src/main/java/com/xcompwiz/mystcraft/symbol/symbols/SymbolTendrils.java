package com.xcompwiz.mystcraft.symbol.symbols;

import com.xcompwiz.mystcraft.api.symbol.BlockCategory;
import com.xcompwiz.mystcraft.api.symbol.BlockDescriptor;
import com.xcompwiz.mystcraft.api.symbol.ModifierUtils;
import com.xcompwiz.mystcraft.api.world.AgeDirector;
import com.xcompwiz.mystcraft.api.world.logic.ITerrainAlteration;
import com.xcompwiz.mystcraft.symbol.SymbolBase;
import com.xcompwiz.mystcraft.world.gen.MapGenAdvanced;
import com.xcompwiz.mystcraft.world.gen.MapGenCavesMyst;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;

public class SymbolTendrils extends SymbolBase {

	public SymbolTendrils(String identifier) {
		super(identifier);
	}

	@Override
	public void registerLogic(AgeDirector controller, long seed) {
		MapGenAdvanced generator;
		BlockDescriptor block = ModifierUtils.popBlockMatching(controller, BlockCategory.STRUCTURE);
		if (block != null) {
			generator = new MapGenCavesMyst(seed, 15, 18, block.blockstate);
		} else {
			generator = new MapGenCavesMyst(seed, 15, 18, Blocks.LOG);
		}
		controller.registerInterface(new TerrainAlteration(generator));
	}

	private class TerrainAlteration implements ITerrainAlteration {

		private MapGenAdvanced generator;

		public TerrainAlteration(MapGenAdvanced gen) {
			generator = gen;
		}

		@Override
		public void alterTerrain(World worldObj, int chunkX, int chunkZ, ChunkPrimer primer) {
			generator.generate(worldObj.getChunkProvider(), worldObj, chunkX, chunkZ, primer);
		}
	}
}
