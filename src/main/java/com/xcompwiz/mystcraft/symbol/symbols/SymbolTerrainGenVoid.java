package com.xcompwiz.mystcraft.symbol.symbols;

import net.minecraft.block.Block;

import com.xcompwiz.mystcraft.api.world.AgeDirector;
import com.xcompwiz.mystcraft.api.world.logic.ITerrainGenerator;
import com.xcompwiz.mystcraft.symbol.SymbolBase;

public class SymbolTerrainGenVoid extends SymbolBase {

	public SymbolTerrainGenVoid(String identifier) {
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

	private class TerrainGenerator implements ITerrainGenerator {
		public TerrainGenerator() {}

		@Override
		public void generateTerrain(int chunkX, int chunkZ, Block[] blocks, byte[] metadata) {}
	}
}
