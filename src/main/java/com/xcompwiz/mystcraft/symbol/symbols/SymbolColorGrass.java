package com.xcompwiz.mystcraft.symbol.symbols;

import com.xcompwiz.mystcraft.api.symbol.ModifierUtils;
import com.xcompwiz.mystcraft.api.util.Color;
import com.xcompwiz.mystcraft.api.world.AgeDirector;
import com.xcompwiz.mystcraft.api.world.logic.IStaticColorProvider;
import com.xcompwiz.mystcraft.symbol.SymbolBase;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class SymbolColorGrass extends SymbolBase {

	public SymbolColorGrass(ResourceLocation identifier) {
		super(identifier);
	}

	@Override
	public void registerLogic(AgeDirector controller, long seed) {
		Color color = controller.popModifier(ModifierUtils.COLOR).asColor();
		controller.registerInterface(new StaticColorProvider(color), IStaticColorProvider.GRASS);
	}

	@Override
	public boolean generatesConfigOption() {
		return true;
	}

	public class StaticColorProvider implements IStaticColorProvider {

		private Color color;

		public StaticColorProvider(Color color) {
			this.color = color;
		}

		@Override
		public Color getStaticColor(World worldObj, Biome biome, BlockPos pos) {
			return this.color;
		}

	}
}
