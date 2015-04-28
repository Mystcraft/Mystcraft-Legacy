package com.xcompwiz.mystcraft.symbol.modifiers;

import com.xcompwiz.mystcraft.api.symbol.ModifierUtils;
import com.xcompwiz.mystcraft.api.world.AgeDirector;
import com.xcompwiz.mystcraft.symbol.SymbolBase;

public class SymbolLength extends SymbolBase {
	private final float		value;
	private final String	identifier;
	private final String	display;

	public SymbolLength(float value, String identifier, String display) {
		this.value = value;
		this.identifier = identifier;
		this.display = display;
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
	public String identifier() {
		return identifier;
	}

	@Override
	public String displayName() {
		//XXX (Localization)
		return display;
	}
}
