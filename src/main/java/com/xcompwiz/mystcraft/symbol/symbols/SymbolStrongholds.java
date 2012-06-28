package com.xcompwiz.mystcraft.symbol.symbols;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.MapGenStronghold;

import com.xcompwiz.mystcraft.api.world.logic.IPopulate;
import com.xcompwiz.mystcraft.api.world.logic.ITerrainAlteration;
import com.xcompwiz.mystcraft.api.world.logic.ITerrainFeatureLocator;
import com.xcompwiz.mystcraft.symbol.SymbolBase;
import com.xcompwiz.mystcraft.world.IAgeController;

public class SymbolStrongholds extends SymbolBase {
	@Override
	public void registerLogic(IAgeController controller, long seed) {
		MapGenStronghold generator = new MapGenStronghold();
		controller.registerInterface(new TerrainAlteration(generator));
		controller.registerInterface(new Populator(generator));
		controller.registerInterface(new FeatureLocator(generator));
	}

	@Override
	public String identifier() {
		return "Strongholds";
	}

	@Override
	public int instabilityModifier(int count) {
		if (count > 3) return 100;
		return 0;
	}

	private class Populator implements IPopulate {
		MapGenStronghold	generator;

		public Populator(MapGenStronghold gen) {
			generator = gen;
		}

		@Override
		public boolean populate(World worldObj, Random rand, int i, int j, boolean flag) {
			generator.generateStructuresInChunk(worldObj, rand, i >> 4, j >> 4);
			return false;
		}
	}

	private class TerrainAlteration implements ITerrainAlteration {
		MapGenStronghold	generator;

		public TerrainAlteration(MapGenStronghold gen) {
			generator = gen;
		}

		@Override
		public void alterTerrain(World worldObj, int chunkX, int chunkZ, Block[] blocks, byte[] metadata) {
			generator.func_151539_a(worldObj.getChunkProvider(), worldObj, chunkX, chunkZ, null); // Note: Null block array for structure generation...
		}
	}

	private class FeatureLocator implements ITerrainFeatureLocator {
		MapGenStronghold	generator;

		public FeatureLocator(MapGenStronghold gen) {
			generator = gen;
		}

		@Override
		public ChunkPosition locate(World world, String s, int i, int j, int k) {
			if ("Stronghold".equals(s) && generator != null) {
				return generator.func_151545_a(world, i, j, k);
			}
			return null;
		}
	}

}
