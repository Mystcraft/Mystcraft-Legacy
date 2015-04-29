package com.xcompwiz.mystcraft.symbol.symbols;

import net.minecraft.util.MathHelper;
import net.minecraft.world.ColorizerGrass;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import com.xcompwiz.mystcraft.api.util.Color;
import com.xcompwiz.mystcraft.api.world.AgeDirector;
import com.xcompwiz.mystcraft.api.world.logic.IStaticColorProvider;
import com.xcompwiz.mystcraft.symbol.SymbolBase;

public class SymbolColorGrassNatural extends SymbolBase {

	@Override
	public void registerLogic(AgeDirector controller, long seed) {
		controller.registerInterface(new StaticColorProvider(), IStaticColorProvider.GRASS);
	}

	@Override
	public String identifier() {
		return "ColorGrassNat";
	}

	public class StaticColorProvider implements IStaticColorProvider {

		@Override
		public Color getStaticColor(World worldObj, BiomeGenBase biome, int x, int y, int z) {
			double d0 = MathHelper.clamp_float(biome.getFloatTemperature(x, y, z), 0.0F, 1.0F);
			double d1 = MathHelper.clamp_float(biome.getFloatRainfall(), 0.0F, 1.0F);
			return new Color(ColorizerGrass.getGrassColor(d0, d1));
		}

	}
}
