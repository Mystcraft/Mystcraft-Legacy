package com.xcompwiz.mystcraft.symbol.symbols;

import com.xcompwiz.mystcraft.api.util.Color;
import com.xcompwiz.mystcraft.api.world.AgeDirector;
import com.xcompwiz.mystcraft.api.world.logic.IStaticColorProvider;
import com.xcompwiz.mystcraft.symbol.SymbolBase;

import net.minecraft.init.Biomes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ColorizerGrass;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class SymbolColorGrassNatural extends SymbolBase {

	public SymbolColorGrassNatural(ResourceLocation identifier) {
		super(identifier);
	}

	@Override
	public void registerLogic(AgeDirector controller, long seed) {
		controller.registerInterface(new StaticColorProvider(), IStaticColorProvider.GRASS);
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
			if (pos == null)
				pos = BlockPos.ORIGIN;
			double d0 = MathHelper.clamp(biome.getTemperature(pos), 0.0F, 1.0F);
			double d1 = MathHelper.clamp(biome.getRainfall(), 0.0F, 1.0F);
			return new Color(ColorizerGrass.getGrassColor(d0, d1));
		}

	}
}
