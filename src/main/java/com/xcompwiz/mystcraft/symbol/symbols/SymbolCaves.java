package com.xcompwiz.mystcraft.symbol.symbols;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

import com.xcompwiz.mystcraft.api.world.logic.ITerrainAlteration;
import com.xcompwiz.mystcraft.symbol.SymbolBase;
import com.xcompwiz.mystcraft.world.IAgeController;
import com.xcompwiz.mystcraft.world.gen.MapGenAdvanced;
import com.xcompwiz.mystcraft.world.gen.MapGenCavesMyst;

public class SymbolCaves extends SymbolBase {
	@Override
	public void registerLogic(IAgeController controller, long seed) {
		MapGenAdvanced generator = new MapGenCavesMyst(seed, 15, 40, Blocks.air);
		controller.registerInterface(new TerrainAlteration(generator));
	}

	@Override
	public String identifier() {
		return "Caves";
	}

	private static class TerrainAlteration implements ITerrainAlteration {
		MapGenAdvanced	generator;

		public TerrainAlteration(MapGenAdvanced gen) {
			generator = gen;
		}

		@Override
		public void alterTerrain(World worldObj, int chunkX, int chunkZ, Block[] blocks, byte[] metadata) {
			generator.generate(worldObj.getChunkProvider(), worldObj, chunkX, chunkZ, blocks, metadata);
		}
	}
}
