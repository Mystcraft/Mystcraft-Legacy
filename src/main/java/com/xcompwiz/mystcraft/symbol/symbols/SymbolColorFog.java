package com.xcompwiz.mystcraft.symbol.symbols;

import com.xcompwiz.mystcraft.api.symbol.ModifierUtils;
import com.xcompwiz.mystcraft.api.util.Color;
import com.xcompwiz.mystcraft.api.util.ColorGradient;
import com.xcompwiz.mystcraft.api.world.AgeDirector;
import com.xcompwiz.mystcraft.api.world.logic.IDynamicColorProvider;
import com.xcompwiz.mystcraft.symbol.SymbolBase;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;

public class SymbolColorFog extends SymbolBase {

	public SymbolColorFog(ResourceLocation identifier) {
		super(identifier);
	}

	@Override
	public void registerLogic(AgeDirector controller, long seed) {
		ColorGradient gradient = ModifierUtils.popGradient(controller, 0.7529412F, 0.8470588F, 1.0F);
		controller.registerInterface(new FogColorizer(gradient), IDynamicColorProvider.FOG);
	}

	@Override
	public boolean generatesConfigOption() {
		return true;
	}

	private static class FogColorizer implements IDynamicColorProvider {
		private static final Color black = new Color(0.0001F, 0.0001F, 0.0001F);

		ColorGradient gradient;

		public FogColorizer(ColorGradient gradient) {
			this.gradient = gradient;
		}

		@Override
		public Color getColor(Entity entity, Biome biome, float time, float celestial_angle, float partialtick) {
			Color color = gradient.getColor(time / 12000F);
			if ((color.r == 0) && (color.g == 0) && (color.b == 0)) {
				color = black;
			}
			return color;
		}
	}
}
