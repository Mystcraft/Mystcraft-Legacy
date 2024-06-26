package com.xcompwiz.mystcraft.symbol.symbols;

import com.xcompwiz.mystcraft.api.world.AgeDirector;
import com.xcompwiz.mystcraft.api.world.logic.ITerrainGenerator;
import com.xcompwiz.mystcraft.symbol.SymbolBase;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.chunk.ChunkPrimer;

public class SymbolTerrainGenVoid extends SymbolBase {

	public SymbolTerrainGenVoid(ResourceLocation identifier) {
		super(identifier);
	}

	@Override
	public void registerLogic(AgeDirector controller, long seed) {
		controller.registerInterface(new TerrainGenerator());
		controller.setCloudHeight(0);
		controller.setHorizon(0);
		controller.setDrawHorizon(false);
		controller.setDrawVoid(false);
	}

	@Override
	public boolean generatesConfigOption() {
		return true;
	}

	private class TerrainGenerator implements ITerrainGenerator {

		public TerrainGenerator() {}

		@Override
		public void generateTerrain(int chunkX, int chunkZ, ChunkPrimer primer) {}

	}
}
