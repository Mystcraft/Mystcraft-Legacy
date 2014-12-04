package com.xcompwiz.mystcraft.symbol.symbols;

import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import com.xcompwiz.mystcraft.api.util.Color;
import com.xcompwiz.mystcraft.api.world.logic.IStaticColorProvider;
import com.xcompwiz.mystcraft.symbol.SymbolBase;
import com.xcompwiz.mystcraft.world.IAgeController;

public class SymbolColorWaterNatural extends SymbolBase {

	@Override
	public void registerLogic(IAgeController controller, long seed) {
		controller.registerInterface(new StaticColorProvider(IStaticColorProvider.WATER));
	}

	@Override
	public String identifier() {
		return "ColorWaterNat";
	}

	public class StaticColorProvider implements IStaticColorProvider {

		private Object	type;

		public StaticColorProvider(String type) {
			this.type = type;
		}

		@Override
		public Color getStaticColor(String type, World worldObj, BiomeGenBase biome, int x, int y, int z) {
			if (type.equals(this.type)) { return new Color(biome.waterColorMultiplier); }
			return null;
		}

	}
}
