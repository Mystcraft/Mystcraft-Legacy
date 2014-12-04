package com.xcompwiz.mystcraft.symbol.modifiers;

import com.xcompwiz.mystcraft.api.world.IAgeController;
import com.xcompwiz.mystcraft.symbol.ModifierUtils;
import com.xcompwiz.mystcraft.symbol.SymbolBase;

public class ModifierPhase extends SymbolBase {
	private final float		value;
	private final String	identifier;
	private final String	display;

	public ModifierPhase(float value, String identifier, String display) {
		this.value = value;
		this.identifier = identifier;
		this.display = display;
	}

	@Override
	public void registerLogic(IAgeController controller, long seed) {
		Float value = this.value;
		Number prev = controller.popModifier(ModifierUtils.PHASE).asNumber();
		if (prev != null) {
			value = ModifierUtils.averageAngles(prev.floatValue(), value);
		}
		if (value >= 360.0F) {
			value -= 360.0F;
		}
		controller.setModifier(ModifierUtils.PHASE, value);
	}

	@Override
	public String identifier() {
		return identifier;
	}

	@Override
	public String displayName() {
		//XXX: Localization
		return display + " Phase";
	}
}
