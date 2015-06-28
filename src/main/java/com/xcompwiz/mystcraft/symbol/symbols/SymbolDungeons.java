package com.xcompwiz.mystcraft.symbol.symbols;

import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenDungeons;

import com.xcompwiz.mystcraft.api.world.AgeDirector;
import com.xcompwiz.mystcraft.api.world.logic.IPopulate;
import com.xcompwiz.mystcraft.symbol.SymbolBase;

public class SymbolDungeons extends SymbolBase {

	public SymbolDungeons(String identifier) {
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
			for (int k1 = 0; k1 < 8; k1++) {
				int i3 = i + rand.nextInt(16) + 8;
				int i4 = rand.nextInt(256);
				int k4 = j + rand.nextInt(16) + 8;
				(new WorldGenDungeons()).generate(worldObj, rand, i3, i4, k4);
			}
			return false;
		}
	}
}
