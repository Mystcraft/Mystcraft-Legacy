package com.xcompwiz.mystcraft.symbol.symbols;

import com.xcompwiz.mystcraft.api.world.AgeDirector;
import com.xcompwiz.mystcraft.api.world.logic.ITerrainAlteration;
import com.xcompwiz.mystcraft.symbol.SymbolBase;
import com.xcompwiz.mystcraft.world.gen.MapGenAdvanced;
import com.xcompwiz.mystcraft.world.gen.feature.WorldGenMystBigTree;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;

public class SymbolHugeTrees extends SymbolBase {

	public SymbolHugeTrees(ResourceLocation identifier) {
		super(identifier);
	}

	@Override
	public void registerLogic(AgeDirector controller, long seed) {
		MapGenAdvanced generator = new WorldGenMystBigTree(seed);
		controller.registerInterface(new TerrainAlteration(generator));
	}

	@Override
	public boolean generatesConfigOption() {
		return true;
	}

	private static class TerrainAlteration implements ITerrainAlteration {

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
