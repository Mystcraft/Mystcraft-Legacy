package com.xcompwiz.mystcraft.symbol.symbols;

import com.xcompwiz.mystcraft.api.util.Color;
import com.xcompwiz.mystcraft.api.world.AgeDirector;
import com.xcompwiz.mystcraft.api.world.logic.IDynamicColorProvider;
import com.xcompwiz.mystcraft.symbol.SymbolBase;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;

public class SymbolColorSkyNatural extends SymbolBase {

	public SymbolColorSkyNatural(String identifier) {
		super(identifier);
	}

	@Override
	public void registerLogic(AgeDirector controller, long seed) {
		controller.registerInterface(new Colorizer(), IDynamicColorProvider.SKY);
	}

	private static class Colorizer implements IDynamicColorProvider {
		public Colorizer() {}

		@Override
		public Color getColor(Entity entity, Biome biome, float time, float celestial_angle, float partialtick) {
			float alpha = MathHelper.cos(celestial_angle * (float) Math.PI * 2.0F) * 2.0F + 0.5F;

			if (alpha < 0.0F) alpha = 0.0F;
			if (alpha > 1.0F) alpha = 1.0F;

			float var8 = biome.getFloatTemperature(entity.getPosition());
			int var9 = getSkyColorByTemp(var8);
			float red = (var9 >> 16 & 255) / 255.0F;
			float green = (var9 >> 8 & 255) / 255.0F;
			float blue = (var9 & 255) / 255.0F;
			red *= alpha;
			green *= alpha;
			blue *= alpha;
			return new Color(red, green, blue);
		}

		private int getSkyColorByTemp(float par1) {
			par1 /= 3.0F;

			if (par1 < -1.0F) {
				par1 = -1.0F;
			}

			if (par1 > 1.0F) {
				par1 = 1.0F;
			}

			return java.awt.Color.getHSBColor(0.62222224F - par1 * 0.05F, 0.5F + par1 * 0.1F, 1.0F).getRGB();
		}
	}
}
