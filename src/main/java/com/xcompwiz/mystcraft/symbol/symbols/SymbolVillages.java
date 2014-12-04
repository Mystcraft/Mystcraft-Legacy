package com.xcompwiz.mystcraft.symbol.symbols;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.MapGenVillage;

import com.xcompwiz.mystcraft.api.world.IAgeController;
import com.xcompwiz.mystcraft.api.world.logic.IPopulate;
import com.xcompwiz.mystcraft.api.world.logic.ITerrainAlteration;
import com.xcompwiz.mystcraft.symbol.SymbolBase;

public class SymbolVillages extends SymbolBase {
	@Override
	public void registerLogic(IAgeController controller, long seed) {
		MapGenVillage generator = new MapGenVillage();
		controller.registerInterface(new TerrainAlteration(generator));
		controller.registerInterface(new Populator(generator));
	}

	@Override
	public String identifier() {
		return "Villages";
	}

	@Override
	public int instabilityModifier(int count) {
		if (count > 3) return 100;
		return 0;
	}

	private class Populator implements IPopulate {
		MapGenVillage	generator;

		public Populator(MapGenVillage gen) {
			generator = gen;
		}

		@Override
		public boolean populate(World worldObj, Random rand, int i, int j, boolean flag) {
			return generator.generateStructuresInChunk(worldObj, rand, i >> 4, j >> 4);
		}
	}

	private class TerrainAlteration implements ITerrainAlteration {
		MapGenVillage	generator;

		public TerrainAlteration(MapGenVillage gen) {
			generator = gen;
		}

		@Override
		public void alterTerrain(World worldObj, int chunkX, int chunkZ, Block[] blocks, byte[] metadata) {
			generator.func_151539_a(worldObj.getChunkProvider(), worldObj, chunkX, chunkZ, null); // Note: Null block array for structure generation...
		}
	}
}
