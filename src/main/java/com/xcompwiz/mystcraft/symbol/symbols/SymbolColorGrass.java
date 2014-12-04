package com.xcompwiz.mystcraft.symbol.symbols;

import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import com.xcompwiz.mystcraft.api.util.Color;
import com.xcompwiz.mystcraft.api.world.IAgeController;
import com.xcompwiz.mystcraft.api.world.logic.IStaticColorProvider;
import com.xcompwiz.mystcraft.symbol.ModifierUtils;
import com.xcompwiz.mystcraft.symbol.SymbolBase;

public class SymbolColorGrass extends SymbolBase {

	@Override
	public void registerLogic(IAgeController controller, long seed) {
		Color color = controller.popModifier(ModifierUtils.COLOR).asColor();
		controller.registerInterface(new StaticColorProvider(IStaticColorProvider.GRASS, color));
	}

	@Override
	public String identifier() {
		return "ColorGrass";
	}

	public class StaticColorProvider implements IStaticColorProvider {

		private Object	type;
		private Color	color;

		public StaticColorProvider(String type, Color color) {
			this.type = type;
			this.color = color;
		}

		@Override
		public Color getStaticColor(String type, World worldObj, BiomeGenBase biome, int x, int y, int z) {
			if (type.equals(this.type)) return this.color;
			return null;
		}

	}
}
