package com.xcompwiz.mystcraft.symbol.symbols;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import com.xcompwiz.mystcraft.api.symbol.BlockCategory;
import com.xcompwiz.mystcraft.api.symbol.ModifierUtils;
import com.xcompwiz.mystcraft.api.world.IAgeController;
import com.xcompwiz.mystcraft.api.world.logic.IPopulate;
import com.xcompwiz.mystcraft.symbol.BlockDescriptor;
import com.xcompwiz.mystcraft.symbol.SymbolBase;
import com.xcompwiz.mystcraft.world.gen.feature.WorldGenLakesAdv;

public class SymbolLakesSurface extends SymbolBase {
	@Override
	public void registerLogic(IAgeController controller, long seed) {
		BlockDescriptor block = ModifierUtils.popBlockMatching(controller, BlockCategory.FLUID);
		WorldGenerator generator = null;
		if (block != null) {
			generator = new WorldGenLakesAdv(block.block, block.metadata);
		} else {
			generator = new WorldGenLakesAdv(Blocks.water);
		}
		controller.registerInterface(new Populator(generator));
	}

	@Override
	public String identifier() {
		return "LakesSurface";
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
