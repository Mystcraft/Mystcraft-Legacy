package com.xcompwiz.mystcraft.symbol.modifiers;

import com.xcompwiz.mystcraft.api.symbol.ModifierUtils;
import com.xcompwiz.mystcraft.api.world.AgeDirector;
import com.xcompwiz.mystcraft.symbol.SymbolBase;

import net.minecraft.util.ResourceLocation;

public class SymbolAngle extends SymbolBase {
	private final float value;
	private final String display;

	public SymbolAngle(ResourceLocation identifier, float value, String display) {
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
		Float value = this.value;
		Number prev = controller.popModifier(ModifierUtils.ANGLE).asNumber();
		if (prev != null) {
			value = ModifierUtils.averageAngles(prev.floatValue(), value);
		}
		if (value >= 360.0F) {
			value -= 360.0F;
		}
		controller.setModifier(ModifierUtils.ANGLE, value);
	}

	@Override
	public String generateLocalizedName() {
		//XXX: (Localization)
		return display + " Direction";
	}
}
