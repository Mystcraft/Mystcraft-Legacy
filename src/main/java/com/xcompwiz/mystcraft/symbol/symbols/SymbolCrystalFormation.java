package com.xcompwiz.mystcraft.symbol.symbols;

import java.util.Random;

import net.minecraft.world.World;

import com.xcompwiz.mystcraft.api.symbol.BlockCategory;
import com.xcompwiz.mystcraft.api.world.IAgeController;
import com.xcompwiz.mystcraft.api.world.logic.IPopulate;
import com.xcompwiz.mystcraft.data.ModBlocks;
import com.xcompwiz.mystcraft.symbol.BlockDescriptor;
import com.xcompwiz.mystcraft.symbol.ModifierUtils;
import com.xcompwiz.mystcraft.symbol.SymbolBase;
import com.xcompwiz.mystcraft.world.gen.feature.WorldGenMystCrystalFormation;
import com.xcompwiz.mystcraft.world.gen.feature.WorldGeneratorAdv;

public class SymbolCrystalFormation extends SymbolBase {
	@Override
	public void registerLogic(IAgeController controller, long seed) {
		WorldGenMystCrystalFormation generator;
		BlockDescriptor block = ModifierUtils.popBlockMatching(controller, BlockCategory.CRYSTAL);
		if (block != null) {
			generator = new WorldGenMystCrystalFormation(block.block, block.metadata);
		} else {
			generator = new WorldGenMystCrystalFormation(ModBlocks.crystal);
		}
		controller.registerInterface(new Populator(generator));
	}

	@Override
	public String identifier() {
		return "CryForm";
	}

	private static class Populator implements IPopulate {
		WorldGeneratorAdv	gen;
		private int			rate;

		Populator(WorldGeneratorAdv gen) {
			this.gen = gen;
			this.rate = 15;
		}

		@Override
		public boolean populate(World worldObj, Random rand, int i, int j, boolean flag) {
			if (!flag && rand.nextInt(rate) == 0) {
				i += rand.nextInt(16) + 8;
				j += rand.nextInt(16) + 8;
				gen.generate(worldObj, rand, i, 0, j);
			} else {
				gen.noGen();
			}
			return false;
		}
	}
}
