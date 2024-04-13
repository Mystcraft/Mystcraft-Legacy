package com.xcompwiz.mystcraft.symbol.symbols;

import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import com.xcompwiz.mystcraft.api.symbol.ModifierUtils;
import com.xcompwiz.mystcraft.api.util.Color;
import com.xcompwiz.mystcraft.api.world.AgeDirector;
import com.xcompwiz.mystcraft.api.world.logic.IStaticColorProvider;
import com.xcompwiz.mystcraft.symbol.SymbolBase;

public class SymbolColorGrass extends SymbolBase {

	public SymbolColorGrass(String identifier) {
		super(identifier);
	}

	@Override
	public void registerLogic(AgeDirector controller, long seed) {
		Color color = controller.popModifier(ModifierUtils.COLOR).asColor();
		controller.registerInterface(new StaticColorProvider(color), IStaticColorProvider.GRASS);
	}

	public class StaticColorProvider implements IStaticColorProvider {

		private Color	color;

		public StaticColorProvider(Color color) {
			this.color = color;
		}

		@Override
		public Color getStaticColor(World worldObj, BiomeGenBase biome, int x, int y, int z) {
			return this.color;
		}

	}
}
