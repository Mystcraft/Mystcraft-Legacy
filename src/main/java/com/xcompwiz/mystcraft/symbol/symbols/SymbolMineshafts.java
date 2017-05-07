package com.xcompwiz.mystcraft.symbol.symbols;

import java.util.Random;

import com.xcompwiz.mystcraft.api.world.AgeDirector;
import com.xcompwiz.mystcraft.api.world.logic.IPopulate;
import com.xcompwiz.mystcraft.api.world.logic.ITerrainAlteration;
import com.xcompwiz.mystcraft.symbol.SymbolBase;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.MapGenMineshaft;

public class SymbolMineshafts extends SymbolBase {

	public SymbolMineshafts(String identifier) {
		super(identifier);
	}

	@Override
	public void registerLogic(AgeDirector controller, long seed) {
		MapGenMineshaft mineshaftGenerator = new MapGenMineshaft();
		controller.registerInterface(new TerrainAlteration(mineshaftGenerator));
		controller.registerInterface(new Populator(mineshaftGenerator));
	}

	@Override
	public int instabilityModifier(int count) {
		if (count > 3) return 100;
		return 0;
	}

	private class Populator implements IPopulate {
		MapGenMineshaft	mineshaftGenerator;

		public Populator(MapGenMineshaft gen) {
			mineshaftGenerator = gen;
		}

		@Override
		public boolean populate(World worldObj, Random rand, int i, int j, boolean flag) {
			mineshaftGenerator.generateStructuresInChunk(worldObj, rand, i >> 4, j >> 4);
			return false;
		}
	}

	private class TerrainAlteration implements ITerrainAlteration {
		MapGenMineshaft	mineshaftGenerator;

		public TerrainAlteration(MapGenMineshaft gen) {
			mineshaftGenerator = gen;
		}

		@Override
		public void alterTerrain(World worldObj, int chunkX, int chunkZ, Block[] blocks, byte[] metadata) {
			mineshaftGenerator.func_151539_a(worldObj.getChunkProvider(), worldObj, chunkX, chunkZ, null); // Note: Null
																											// block
																											// array for
																											// structure
																											// generation...
		}
	}
}
