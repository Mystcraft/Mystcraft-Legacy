package com.xcompwiz.mystcraft.symbol.symbols;

import com.xcompwiz.mystcraft.api.util.Color;
import com.xcompwiz.mystcraft.api.world.AgeDirector;
import com.xcompwiz.mystcraft.api.world.logic.IStaticColorProvider;
import com.xcompwiz.mystcraft.symbol.SymbolBase;

import net.minecraft.init.Biomes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class SymbolColorWaterNatural extends SymbolBase {

	public SymbolColorWaterNatural(ResourceLocation identifier) {
		super(identifier);
	}

	@Override
	public void registerLogic(AgeDirector controller, long seed) {
		controller.registerInterface(new StaticColorProvider(), IStaticColorProvider.WATER);
	}

	@Override
	public boolean generatesConfigOption() {
		return true;
	}

	public class StaticColorProvider implements IStaticColorProvider {

		@Override
		public Color getStaticColor(World worldObj, Biome biome, BlockPos pos) {
			if (biome == null)
				biome = Biomes.PLAINS;
			return new Color(biome.getWaterColorMultiplier());
		}

	}
}
