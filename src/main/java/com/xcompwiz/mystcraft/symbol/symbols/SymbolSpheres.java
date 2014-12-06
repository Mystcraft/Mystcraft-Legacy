package com.xcompwiz.mystcraft.symbol.symbols;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

import com.xcompwiz.mystcraft.api.symbol.BlockCategory;
import com.xcompwiz.mystcraft.api.symbol.ModifierUtils;
import com.xcompwiz.mystcraft.api.world.IAgeController;
import com.xcompwiz.mystcraft.api.world.logic.ITerrainAlteration;
import com.xcompwiz.mystcraft.symbol.BlockDescriptor;
import com.xcompwiz.mystcraft.symbol.SymbolBase;
import com.xcompwiz.mystcraft.world.gen.MapGenAdvanced;
import com.xcompwiz.mystcraft.world.gen.MapGenSpheresMyst;

public class SymbolSpheres extends SymbolBase {

	@Override
	public void registerLogic(IAgeController controller, long seed) {
		MapGenAdvanced generator;
		BlockDescriptor block = ModifierUtils.popBlockMatching(controller, BlockCategory.STRUCTURE);
		if (block != null) {
			generator = new MapGenSpheresMyst(seed, block.block, block.metadata);
		} else {
			generator = new MapGenSpheresMyst(seed, Blocks.cobblestone);
		}
		controller.registerInterface(new TerrainAlteration(generator));
	}

	@Override
	public String identifier() {
		return "TerModSpheres";
	}

	private class TerrainAlteration implements ITerrainAlteration {
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
