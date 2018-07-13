package com.xcompwiz.mystcraft.symbol.symbols;

import com.xcompwiz.mystcraft.api.world.AgeDirector;
import com.xcompwiz.mystcraft.api.world.logic.ITerrainAlteration;
import com.xcompwiz.mystcraft.symbol.SymbolBase;
import com.xcompwiz.mystcraft.world.gen.MapGenAdvanced;
import com.xcompwiz.mystcraft.world.gen.MapGenCavesMyst;

import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;

public class SymbolCaves extends SymbolBase {

	public SymbolCaves(ResourceLocation identifier) {
		super(identifier);
	}

	@Override
	public void registerLogic(AgeDirector controller, long seed) {
		MapGenAdvanced generator = new MapGenCavesMyst(seed, 15, 40, Blocks.AIR);
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
