package com.xcompwiz.mystcraft.symbol.symbols;

import java.util.Random;

import com.xcompwiz.mystcraft.api.world.AgeDirector;
import com.xcompwiz.mystcraft.api.world.logic.IPopulate;
import com.xcompwiz.mystcraft.symbol.SymbolBase;
import com.xcompwiz.mystcraft.world.gen.feature.WorldGenMystBigTree;

import net.minecraft.world.World;

public class SymbolHugeTrees extends SymbolBase {

	public SymbolHugeTrees(String identifier) {
		super(identifier);
	}

	@Override
	public void registerLogic(AgeDirector controller, long seed) {
		controller.registerInterface(new Populator());
	}

	private class Populator implements IPopulate {
		public Populator() {}

		@Override
		public boolean populate(World worldObj, Random rand, int i, int j, boolean flag) {
			WorldGenMystBigTree genBigTree = new WorldGenMystBigTree(false);
			int j6 = i + rand.nextInt(16) + 8;
			int k10 = j + rand.nextInt(16) + 8;
			genBigTree.generate(worldObj, rand, j6, worldObj.getHeightValue(j6, k10), k10);
			return false;
		}
	}
}
