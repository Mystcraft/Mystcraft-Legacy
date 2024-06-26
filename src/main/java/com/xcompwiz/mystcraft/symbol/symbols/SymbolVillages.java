package com.xcompwiz.mystcraft.symbol.symbols;

import java.util.Random;

import com.xcompwiz.mystcraft.api.world.AgeDirector;
import com.xcompwiz.mystcraft.api.world.logic.IPopulate;
import com.xcompwiz.mystcraft.api.world.logic.ITerrainAlteration;
import com.xcompwiz.mystcraft.symbol.SymbolBase;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.structure.MapGenVillage;

public class SymbolVillages extends SymbolBase {

	public SymbolVillages(ResourceLocation identifier) {
		super(identifier);
	}

	@Override
	public void registerLogic(AgeDirector controller, long seed) {
		MapGenVillage generator = new MapGenVillage();
		controller.registerInterface(new TerrainAlteration(generator));
		controller.registerInterface(new Populator(generator));
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

		private MapGenVillage generator;

		public Populator(MapGenVillage gen) {
			generator = gen;
		}

		@Override
		public boolean populate(World worldObj, Random rand, int i, int j, boolean flag) {
			return generator.generateStructure(worldObj, rand, new ChunkPos(i >> 4, j >> 4));
		}
	}

	private class TerrainAlteration implements ITerrainAlteration {

		private MapGenVillage generator;

		public TerrainAlteration(MapGenVillage gen) {
			generator = gen;
		}

		@Override
		public void alterTerrain(World worldObj, int chunkX, int chunkZ, ChunkPrimer primer) {
			generator.generate(worldObj, chunkX, chunkZ, primer);
		}

	}
}
