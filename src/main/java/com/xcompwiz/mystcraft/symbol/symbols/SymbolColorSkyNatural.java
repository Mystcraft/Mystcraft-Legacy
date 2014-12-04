package com.xcompwiz.mystcraft.symbol.symbols;

import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.biome.BiomeGenBase;

import com.xcompwiz.mystcraft.api.util.Color;
import com.xcompwiz.mystcraft.api.world.IAgeController;
import com.xcompwiz.mystcraft.api.world.logic.ISkyColorProvider;
import com.xcompwiz.mystcraft.symbol.SymbolBase;

public class SymbolColorSkyNatural extends SymbolBase {

	@Override
	public void registerLogic(IAgeController controller, long seed) {
		controller.registerInterface(new Colorizer());
	}

	@Override
	public String identifier() {
		return "ColorSkyNat";
	}

	private static class Colorizer implements ISkyColorProvider {
		public Colorizer() {}

		@Override
		public Color getSkyColor(Entity entity, BiomeGenBase biome, float time, float celestial_angle) {
			float alpha = MathHelper.cos(celestial_angle * (float) Math.PI * 2.0F) * 2.0F + 0.5F;

			if (alpha < 0.0F) alpha = 0.0F;
			if (alpha > 1.0F) alpha = 1.0F;

			float var8 = biome.getFloatTemperature(MathHelper.floor_double(entity.posX), MathHelper.floor_double(entity.posY), MathHelper.floor_double(entity.posZ));
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
