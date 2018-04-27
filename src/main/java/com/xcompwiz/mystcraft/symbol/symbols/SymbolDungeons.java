package com.xcompwiz.mystcraft.symbol.symbols;

import java.util.Random;

import com.xcompwiz.mystcraft.api.world.AgeDirector;
import com.xcompwiz.mystcraft.api.world.logic.IPopulate;
import com.xcompwiz.mystcraft.symbol.SymbolBase;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenDungeons;

public class SymbolDungeons extends SymbolBase {

	public SymbolDungeons(ResourceLocation identifier) {
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
				(new WorldGenDungeons()).generate(worldObj, rand, new BlockPos(i3, i4, k4));
			}
			return false;
		}
	}

	@Override
	public boolean generatesConfigOption() {
		return true;
	}
}
