package com.xcompwiz.mystcraft.symbol.symbols;

import java.util.Random;

import com.xcompwiz.mystcraft.api.symbol.BlockCategory;
import com.xcompwiz.mystcraft.api.symbol.BlockDescriptor;
import com.xcompwiz.mystcraft.api.symbol.ModifierUtils;
import com.xcompwiz.mystcraft.api.world.AgeDirector;
import com.xcompwiz.mystcraft.api.world.logic.IPopulate;
import com.xcompwiz.mystcraft.symbol.SymbolBase;
import com.xcompwiz.mystcraft.world.gen.feature.WorldGenSpikesAdv;
import com.xcompwiz.mystcraft.world.gen.feature.WorldGeneratorAdv;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class SymbolSpikes extends SymbolBase {

	public SymbolSpikes(String identifier) {
		super(identifier);
	}

	@Override
	public void registerLogic(AgeDirector controller, long seed) {
		BlockDescriptor block = ModifierUtils.popBlockMatching(controller, BlockCategory.STRUCTURE);
		WorldGeneratorAdv generator = null;
		if (block != null) {
			generator = new WorldGenSpikesAdv(block.block, block.metadata);
		} else {
			generator = new WorldGenSpikesAdv(Blocks.STONE);
		}
		controller.registerInterface(new Populator(generator));
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
