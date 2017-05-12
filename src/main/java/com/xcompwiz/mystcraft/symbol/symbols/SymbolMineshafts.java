package com.xcompwiz.mystcraft.symbol.symbols;

import java.util.Random;

import com.xcompwiz.mystcraft.api.world.AgeDirector;
import com.xcompwiz.mystcraft.api.world.logic.IPopulate;
import com.xcompwiz.mystcraft.api.world.logic.ITerrainAlteration;
import com.xcompwiz.mystcraft.symbol.SymbolBase;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
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
			mineshaftGenerator.generateStructure(worldObj, rand, new ChunkPos(i >> 4, j >> 4));
			return false;
		}
	}

	private class TerrainAlteration implements ITerrainAlteration {

		private MapGenMineshaft	mineshaftGenerator;

		public TerrainAlteration(MapGenMineshaft gen) {
			mineshaftGenerator = gen;
		}

		@Override
		public void alterTerrain(World worldObj, int chunkX, int chunkZ, ChunkPrimer primer) {
			mineshaftGenerator.generate(worldObj, chunkX, chunkZ, primer); //Primer actually never used here.
		}
	}
}
