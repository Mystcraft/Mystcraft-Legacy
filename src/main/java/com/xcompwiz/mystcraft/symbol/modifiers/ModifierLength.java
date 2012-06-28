package com.xcompwiz.mystcraft.symbol.modifiers;

import com.xcompwiz.mystcraft.symbol.ModifierUtils;
import com.xcompwiz.mystcraft.symbol.SymbolBase;
import com.xcompwiz.mystcraft.world.IAgeController;

public class ModifierLength extends SymbolBase {
	private final float		value;
	private final String	identifier;
	private final String	display;

	public ModifierLength(float value, String identifier, String display) {
		this.value = value;
		this.identifier = identifier;
		this.display = display;
	}

	@Override
	public void registerLogic(IAgeController controller, long seed) {
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
		return display;
	}
}
