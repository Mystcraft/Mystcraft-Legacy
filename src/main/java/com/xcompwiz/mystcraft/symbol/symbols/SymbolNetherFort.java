package com.xcompwiz.mystcraft.symbol.symbols;

import java.util.Random;

import com.xcompwiz.mystcraft.api.world.AgeDirector;
import com.xcompwiz.mystcraft.api.world.logic.IPopulate;
import com.xcompwiz.mystcraft.api.world.logic.ITerrainAlteration;
import com.xcompwiz.mystcraft.api.world.logic.ITerrainFeatureLocator;
import com.xcompwiz.mystcraft.symbol.SymbolBase;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.structure.MapGenNetherBridge;

public class SymbolNetherFort extends SymbolBase {

	public SymbolNetherFort(String identifier) {
		super(identifier);
	}

	@Override
	public void registerLogic(AgeDirector controller, long seed) {
		MapGenNetherBridge generator = new MapGenNetherBridge();
		controller.registerInterface(new TerrainAlteration(generator));
		controller.registerInterface(new Populator(generator));
		controller.registerInterface(new FeatureLocator(generator));
	}

	@Override
	public int instabilityModifier(int count) {
		if (count > 3) return 100;
		return 0;
	}

	private class Populator implements IPopulate {

		private MapGenNetherBridge	generator;

		public Populator(MapGenNetherBridge gen) {
			generator = gen;
		}

		@Override
		public boolean populate(World worldObj, Random rand, int i, int j, boolean flag) {
			generator.generateStructure(worldObj, rand, new ChunkPos(i >> 4, j >> 4));
			return false;
		}
	}

	private class TerrainAlteration implements ITerrainAlteration {

		private MapGenNetherBridge generator;

		public TerrainAlteration(MapGenNetherBridge gen) {
			generator = gen;
		}

		@Override
		public void alterTerrain(World worldObj, int chunkX, int chunkZ, ChunkPrimer primer) {
			generator.generate(worldObj, chunkX, chunkZ, primer);
		}
	}

	private class FeatureLocator implements ITerrainFeatureLocator {

		private MapGenNetherBridge generator;

		public FeatureLocator(MapGenNetherBridge gen) {
			generator = gen;
		}

		@Override
		public BlockPos locate(World world, String s, BlockPos pos, boolean genChunks) {
			if ("Nether Fortress".equals(s) && generator != null) {
				return generator.getNearestStructurePos(world, pos, genChunks);
			}
			return null;
		}
	}

}
