package com.xcompwiz.mystcraft.symbol.symbols;

import com.xcompwiz.mystcraft.api.symbol.BlockCategory;
import com.xcompwiz.mystcraft.api.symbol.BlockDescriptor;
import com.xcompwiz.mystcraft.api.symbol.ModifierUtils;
import com.xcompwiz.mystcraft.api.world.AgeDirector;
import com.xcompwiz.mystcraft.api.world.logic.ITerrainAlteration;
import com.xcompwiz.mystcraft.symbol.SymbolBase;
import com.xcompwiz.mystcraft.world.gen.MapGenAdvanced;
import com.xcompwiz.mystcraft.world.gen.MapGenSpheresMyst;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;

public class SymbolSpheres extends SymbolBase {

	public SymbolSpheres(ResourceLocation identifier) {
		super(identifier);
	}

	@Override
	public void registerLogic(AgeDirector controller, long seed) {
		MapGenAdvanced generator;
		BlockDescriptor block = ModifierUtils.popBlockMatching(controller, BlockCategory.STRUCTURE);
		if (block != null) {
			generator = new MapGenSpheresMyst(seed, block.blockstate);
		} else {
			generator = new MapGenSpheresMyst(seed, Blocks.COBBLESTONE);
		}
		controller.registerInterface(new TerrainAlteration(generator));
	}

	@Override
	public boolean generatesConfigOption() {
		return true;
	}

	private class TerrainAlteration implements ITerrainAlteration {

		private MapGenAdvanced generator;

		public TerrainAlteration(MapGenAdvanced gen) {
			generator = gen;
		}

		@Override
		public void alterTerrain(World worldObj, int chunkX, int chunkZ, ChunkPrimer primer) {
			generator.generate(worldObj.getChunkProvider(), worldObj, chunkX, chunkZ, primer);
		}
	}
}
