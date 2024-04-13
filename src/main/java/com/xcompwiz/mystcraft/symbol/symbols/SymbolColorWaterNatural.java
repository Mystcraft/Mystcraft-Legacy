package com.xcompwiz.mystcraft.symbol.symbols;

import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import com.xcompwiz.mystcraft.api.util.Color;
import com.xcompwiz.mystcraft.api.world.AgeDirector;
import com.xcompwiz.mystcraft.api.world.logic.IStaticColorProvider;
import com.xcompwiz.mystcraft.symbol.SymbolBase;

public class SymbolColorWaterNatural extends SymbolBase {

	public SymbolColorWaterNatural(String identifier) {
		super(identifier);
	}

	@Override
	public void registerLogic(AgeDirector controller, long seed) {
		controller.registerInterface(new StaticColorProvider(), IStaticColorProvider.WATER);
	}

	public class StaticColorProvider implements IStaticColorProvider {

		@Override
		public Color getStaticColor(World worldObj, BiomeGenBase biome, int x, int y, int z) {
			return new Color(biome.waterColorMultiplier);
		}

	}
}
