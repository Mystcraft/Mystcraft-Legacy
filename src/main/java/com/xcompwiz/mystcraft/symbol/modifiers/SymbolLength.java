package com.xcompwiz.mystcraft.symbol.modifiers;

import com.xcompwiz.mystcraft.api.symbol.ModifierUtils;
import com.xcompwiz.mystcraft.api.world.AgeDirector;
import com.xcompwiz.mystcraft.symbol.SymbolBase;
import net.minecraft.util.ResourceLocation;

public class SymbolLength extends SymbolBase {
	private final float value;
	private final String display;

	public SymbolLength(ResourceLocation identifier, float value, String display) {
		super(identifier);
		this.value = value;
		this.display = display;
	}

	@Override
	public boolean generatesConfigOption() {
		return true;
	}

	@Override
	public void registerLogic(AgeDirector controller, long seed) {
		Number prev = controller.popModifier(ModifierUtils.FACTOR).asNumber();
		float value = this.value;
		if (prev != null) {
			value = ModifierUtils.averageLengths(value, prev.floatValue());
		}
		controller.setModifier(ModifierUtils.FACTOR, value);
	}

	@Override
	public String generateLocalizedName() {
		//XXX (Localization)
		return display;
	}
}
