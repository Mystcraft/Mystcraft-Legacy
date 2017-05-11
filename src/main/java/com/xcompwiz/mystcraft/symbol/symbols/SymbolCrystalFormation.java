package com.xcompwiz.mystcraft.symbol.symbols;

import java.util.Random;

import com.xcompwiz.mystcraft.api.symbol.BlockCategory;
import com.xcompwiz.mystcraft.api.symbol.BlockDescriptor;
import com.xcompwiz.mystcraft.api.symbol.ModifierUtils;
import com.xcompwiz.mystcraft.api.world.AgeDirector;
import com.xcompwiz.mystcraft.api.world.logic.IPopulate;
import com.xcompwiz.mystcraft.data.ModBlocks;
import com.xcompwiz.mystcraft.symbol.SymbolBase;
import com.xcompwiz.mystcraft.world.gen.feature.WorldGenMystCrystalFormation;
import com.xcompwiz.mystcraft.world.gen.feature.WorldGeneratorAdv;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SymbolCrystalFormation extends SymbolBase {

	public SymbolCrystalFormation(String identifier) {
		super(identifier);
	}

	@Override
	public void registerLogic(AgeDirector controller, long seed) {
		WorldGenMystCrystalFormation generator;
		BlockDescriptor block = ModifierUtils.popBlockMatching(controller, BlockCategory.CRYSTAL);
		if (block != null) {
			generator = new WorldGenMystCrystalFormation(block.blockstate);
		} else {
			generator = new WorldGenMystCrystalFormation(ModBlocks.crystal);
		}
		controller.registerInterface(new Populator(generator));
	}

	private static class Populator implements IPopulate {

		private WorldGeneratorAdv	gen;
		private int					rate;

		Populator(WorldGeneratorAdv gen) {
			this.gen = gen;
			this.rate = 15;
		}

		@Override
		public boolean populate(World worldObj, Random rand, int i, int j, boolean flag) {
			if (!flag && rand.nextInt(rate) == 0) {
				i += rand.nextInt(16) + 8;
				j += rand.nextInt(16) + 8;
				gen.generate(worldObj, rand, new BlockPos(i, 0, j));
			} else {
				gen.noGen();
			}
			return false;
		}
	}
}
