package com.xcompwiz.mystcraft.symbol.symbols;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;

import com.xcompwiz.mystcraft.api.symbol.BlockCategory;
import com.xcompwiz.mystcraft.api.world.logic.IPopulate;
import com.xcompwiz.mystcraft.symbol.BlockDescriptor;
import com.xcompwiz.mystcraft.symbol.ModifierUtils;
import com.xcompwiz.mystcraft.symbol.SymbolBase;
import com.xcompwiz.mystcraft.world.IAgeController;
import com.xcompwiz.mystcraft.world.gen.feature.WorldGenSpikesAdv;
import com.xcompwiz.mystcraft.world.gen.feature.WorldGeneratorAdv;

public class SymbolSpikes extends SymbolBase {
	@Override
	public void registerLogic(IAgeController controller, long seed) {
		BlockDescriptor block = ModifierUtils.popBlockMatching(controller, BlockCategory.STRUCTURE);
		WorldGeneratorAdv generator = null;
		if (block != null) {
			generator = new WorldGenSpikesAdv(block.block, block.metadata);
		} else {
			generator = new WorldGenSpikesAdv(Blocks.stone);
		}
		controller.registerInterface(new Populator(generator));
	}

	@Override
	public String identifier() {
		return "GenSpikes";
	}

	private static class Populator implements IPopulate {
		private WorldGeneratorAdv	generator;

		public Populator(WorldGeneratorAdv generator) {
			this.generator = generator;
		}

		@Override
		public boolean populate(World worldObj, Random rand, int k, int l, boolean flag) {
			if (!flag && rand.nextInt(18) == 0) {
				int x = k + rand.nextInt(16);
				int z = l + rand.nextInt(16);
				int y = worldObj.getHeightValue(x, z);
				generator.generate(worldObj, rand, x, y, z);
			} else {
				generator.noGen();
			}
			return false;
		}
	}
}
