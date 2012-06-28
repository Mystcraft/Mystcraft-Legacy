package com.xcompwiz.mystcraft.symbol;

import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import com.xcompwiz.mystcraft.api.world.logic.IPopulate;
import com.xcompwiz.mystcraft.world.IAgeController;

public class SymbolWorldGen extends SymbolBase {
	String					id	= null;
	Class<WorldGenerator>	gen;

	public SymbolWorldGen(String id, Class<WorldGenerator> gen) {
		this.id = id;
		this.gen = gen;
	}

	@Override
	public void registerLogic(IAgeController controller, long seed) {
		controller.registerInterface(new Populator(gen));
	}

	@Override
	public String identifier() {
		return id;
	}

	private class Populator implements IPopulate {
		Class<WorldGenerator>	gen;

		public Populator(Class<WorldGenerator> gen) {
			this.gen = gen;
		}

		@Override
		public boolean populate(World worldObj, Random rand, int k, int l, boolean flag) {
			if (!flag && rand.nextInt(4) == 0) {
				int i1 = k + rand.nextInt(16) + 8;
				int j2 = rand.nextInt(256);
				int k3 = l + rand.nextInt(16) + 8;
				try {
					(gen.newInstance()).generate(worldObj, rand, i1, j2, k3);
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
			return false;
		}
	}
}
