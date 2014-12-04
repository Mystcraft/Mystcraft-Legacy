package com.xcompwiz.mystcraft.symbol.symbols;

import net.minecraft.util.MathHelper;

import com.xcompwiz.mystcraft.api.util.Color;
import com.xcompwiz.mystcraft.api.world.logic.IFogColorProvider;
import com.xcompwiz.mystcraft.symbol.SymbolBase;
import com.xcompwiz.mystcraft.world.IAgeController;

public class SymbolColorFogNatural extends SymbolBase {
	@Override
	public void registerLogic(IAgeController controller, long seed) {
		controller.registerInterface(new FogColorizer());
	}

	@Override
	public String identifier() {
		return "ColorFogNat";
	}

	private static class FogColorizer implements IFogColorProvider {
		public FogColorizer() {}

		@Override
		public Color getFogColor(float celestial_angle, float time) {
			float f2 = MathHelper.cos(celestial_angle * 3.141593F * 2.0F) * 2.0F + 0.5F;
			if (f2 < 0.0F) {
				f2 = 0.0F;
			}
			if (f2 > 1.0F) {
				f2 = 1.0F;
			}
			float f3 = 0.7529412F;
			float f4 = 0.8470588F;
			float f5 = 1.0F;
			f3 *= f2 * 0.94F + 0.06F;
			f4 *= f2 * 0.94F + 0.06F;
			f5 *= f2 * 0.91F + 0.09F;
			return new Color(f3, f4, f5);
		}
	}
}
