package com.xcompwiz.mystcraft.symbol.symbols;

import java.util.Random;

import com.xcompwiz.mystcraft.api.symbol.BlockCategory;
import com.xcompwiz.mystcraft.api.symbol.BlockDescriptor;
import com.xcompwiz.mystcraft.api.symbol.ModifierUtils;
import com.xcompwiz.mystcraft.api.world.AgeDirector;
import com.xcompwiz.mystcraft.api.world.logic.IPopulate;
import com.xcompwiz.mystcraft.symbol.SymbolBase;
import com.xcompwiz.mystcraft.world.gen.feature.WorldGenLakesAdv;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class SymbolLakesSurface extends SymbolBase {

	public SymbolLakesSurface(String identifier) {
		super(identifier);
	}

	@Override
	public void registerLogic(AgeDirector controller, long seed) {
		BlockDescriptor block = ModifierUtils.popBlockMatching(controller, BlockCategory.FLUID);
		WorldGenerator generator = null;
		if (block != null) {
			generator = new WorldGenLakesAdv(block.block, block.metadata);
		} else {
			generator = new WorldGenLakesAdv(Blocks.water);
		}
		controller.registerInterface(new Populator(generator));
	}

	private class Populator implements IPopulate {
		private WorldGenerator	generator;

		public Populator(WorldGenerator generator) {
			this.generator = generator;
		}

		@Override
		public boolean populate(World worldObj, Random rand, int k, int l, boolean flag) {
			if (!flag && rand.nextInt(4) == 0) {
				int i1 = k + rand.nextInt(16) + 8;
				int j2 = rand.nextInt(256);
				int k3 = l + rand.nextInt(16) + 8;
				generator.generate(worldObj, rand, i1, j2, k3);
			}
			return false;
		}
	}
}
