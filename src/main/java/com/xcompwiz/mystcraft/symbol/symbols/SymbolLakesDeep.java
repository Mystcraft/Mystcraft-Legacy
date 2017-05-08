package com.xcompwiz.mystcraft.symbol.symbols;

import java.util.Random;

import com.xcompwiz.mystcraft.api.symbol.BlockCategory;
import com.xcompwiz.mystcraft.api.symbol.BlockDescriptor;
import com.xcompwiz.mystcraft.api.symbol.ModifierUtils;
import com.xcompwiz.mystcraft.api.world.AgeDirector;
import com.xcompwiz.mystcraft.api.world.logic.IPopulate;
import com.xcompwiz.mystcraft.symbol.SymbolBase;
import com.xcompwiz.mystcraft.world.gen.feature.WorldGenLakesAdv;
import com.xcompwiz.mystcraft.world.gen.feature.WorldGeneratorAdv;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class SymbolLakesDeep extends SymbolBase {

	public SymbolLakesDeep(String identifier) {
		super(identifier);
	}

	@Override
	public void registerLogic(AgeDirector controller, long seed) {
		BlockDescriptor block = ModifierUtils.popBlockMatching(controller, BlockCategory.FLUID, BlockCategory.GAS);
		WorldGeneratorAdv generator = null;
		if (block != null) {
			generator = new WorldGenLakesAdv(block.block, block.metadata);
		} else {
			generator = new WorldGenLakesAdv(Blocks.LAVA);
		}
		controller.registerInterface(new Populator(controller, generator));
	}

	private class Populator implements IPopulate {
		private AgeDirector		controller;
		private WorldGeneratorAdv	gen;

		public Populator(AgeDirector controller, WorldGeneratorAdv generator) {
			this.controller = controller;
			this.gen = generator;
		}

		@Override
		public boolean populate(World worldObj, Random rand, int i, int j, boolean flag) {
			if (!flag && rand.nextInt(8) == 0) {
				int j1 = i + rand.nextInt(16) + 8;
				int k2 = rand.nextInt(rand.nextInt(248) + 8);
				int l3 = j + rand.nextInt(16) + 8;
				if (k2 < controller.getSeaLevel() || rand.nextInt(10) == 0) {
					gen.generate(worldObj, rand, j1, k2, l3);
				} else {
					gen.noGen();
				}
			}
			return false;
		}
	}
}
