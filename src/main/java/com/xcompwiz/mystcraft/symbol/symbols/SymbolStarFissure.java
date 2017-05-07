package com.xcompwiz.mystcraft.symbol.symbols;

import java.util.Random;

import com.xcompwiz.mystcraft.api.world.AgeDirector;
import com.xcompwiz.mystcraft.api.world.logic.IPopulate;
import com.xcompwiz.mystcraft.symbol.SymbolBase;
import com.xcompwiz.mystcraft.world.gen.feature.WorldGenMystStarFissure;

import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

public class SymbolStarFissure extends SymbolBase {

	public SymbolStarFissure(String identifier) {
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
			ChunkCoordinates spawn = worldObj.getSpawnPoint();
			if (i >> 4 != spawn.posX >> 4 || j >> 4 != spawn.posZ >> 4) return false;
			WorldGenMystStarFissure gen = new WorldGenMystStarFissure();
			while (!flag) {
				i += rand.nextInt(16) + 8;
				j += rand.nextInt(16) + 8;
				flag = gen.generate(worldObj, rand, i, 0, j);
			}
			// LoggerHelper.info("Star Fissure " + (flag?"":"NOT ") + "Generated ("+i+", "+j+")");
			return flag;
		}
	}
}
