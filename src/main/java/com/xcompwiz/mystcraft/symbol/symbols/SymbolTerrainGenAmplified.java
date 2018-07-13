package com.xcompwiz.mystcraft.symbol.symbols;

import com.xcompwiz.mystcraft.api.symbol.BlockCategory;
import com.xcompwiz.mystcraft.api.symbol.BlockDescriptor;
import com.xcompwiz.mystcraft.api.symbol.ModifierUtils;
import com.xcompwiz.mystcraft.api.world.AgeDirector;
import com.xcompwiz.mystcraft.symbol.SymbolBase;
import com.xcompwiz.mystcraft.symbol.symbols.SymbolTerrainGenNormal.TerrainGeneratorNormal;

import net.minecraft.util.ResourceLocation;

public class SymbolTerrainGenAmplified extends SymbolBase {

	public SymbolTerrainGenAmplified(ResourceLocation identifier) {
		super(identifier);
	}

	@Override
	public void registerLogic(AgeDirector controller, long seed) {
		TerrainGeneratorNormal gen = new TerrainGeneratorNormal(controller, true);
		BlockDescriptor block;
		block = ModifierUtils.popBlockMatching(controller, BlockCategory.SEA);
		if (block != null) {
			gen.setSeaBlock(block.blockstate);
		}
		block = ModifierUtils.popBlockMatching(controller, BlockCategory.TERRAIN);
		if (block != null) {
			gen.setTerrainBlock(block.blockstate);
		}
		controller.registerInterface(gen);
	}

	@Override
	public boolean generatesConfigOption() {
		return true;
	}
}
