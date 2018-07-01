package com.xcompwiz.mystcraft.symbol.symbols;

import java.util.Random;

import com.xcompwiz.mystcraft.api.world.AgeDirector;
import com.xcompwiz.mystcraft.api.world.logic.IPopulate;
import com.xcompwiz.mystcraft.api.world.logic.ITerrainAlteration;
import com.xcompwiz.mystcraft.api.world.logic.ITerrainFeatureLocator;
import com.xcompwiz.mystcraft.symbol.SymbolBase;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.structure.MapGenStronghold;

public class SymbolStrongholds extends SymbolBase {

	public SymbolStrongholds(ResourceLocation identifier) {
		super(identifier);
	}

	@Override
	public void registerLogic(AgeDirector controller, long seed) {
		MapGenStronghold generator = new MapGenStronghold();
		controller.registerInterface(new TerrainAlteration(generator));
		controller.registerInterface(new Populator(generator));
		controller.registerInterface(new FeatureLocator(generator));
	}

	@Override
	public int instabilityModifier(int count) {
		if (count > 3)
			return 100;
		return 0;
	}

	@Override
	public boolean generatesConfigOption() {
		return true;
	}

	private class Populator implements IPopulate {

		private MapGenStronghold generator;

		public Populator(MapGenStronghold gen) {
			generator = gen;
		}

		@Override
		public boolean populate(World worldObj, Random rand, int i, int j, boolean flag) {
			generator.generateStructure(worldObj, rand, new ChunkPos(i >> 4, j >> 4));
			return false;
		}
	}

	private class TerrainAlteration implements ITerrainAlteration {

		private MapGenStronghold generator;

		public TerrainAlteration(MapGenStronghold gen) {
			generator = gen;
		}

		@Override
		public void alterTerrain(World worldObj, int chunkX, int chunkZ, ChunkPrimer primer) {
			generator.generate(worldObj, chunkX, chunkZ, primer);
		}
	}

	private class FeatureLocator implements ITerrainFeatureLocator {

		private MapGenStronghold generator;

		public FeatureLocator(MapGenStronghold gen) {
			generator = gen;
		}

		@Override
		public BlockPos locate(World world, String s, BlockPos pos, boolean genChunks) {
			if ("Stronghold".equals(s) && generator != null) {
				return generator.getNearestStructurePos(world, pos, genChunks);
			}
			return null;
		}

		@Override
		public boolean isInsideFeature(World world, String identifier, BlockPos pos) {
			if ("Stronghold".equals(identifier) && generator != null) {
				return generator.isPositionInStructure(world, pos);
			}
			return false;
		}
	}

}
