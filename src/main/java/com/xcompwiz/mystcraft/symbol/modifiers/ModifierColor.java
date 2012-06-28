package com.xcompwiz.mystcraft.symbol.modifiers;

import java.util.ArrayList;

import com.xcompwiz.mystcraft.api.internal.IGrammarAPI;
import com.xcompwiz.mystcraft.api.word.WordData;
import com.xcompwiz.mystcraft.grammar.GrammarGenerator.Rule;
import com.xcompwiz.mystcraft.symbol.Color;
import com.xcompwiz.mystcraft.symbol.ModifierUtils;
import com.xcompwiz.mystcraft.symbol.SymbolBase;
import com.xcompwiz.mystcraft.world.IAgeController;
import com.xcompwiz.util.CollectionUtils;

public class ModifierColor extends SymbolBase {
	private float	red;
	private float	green;
	private float	blue;
	private String	name;

	public ModifierColor(float r, float g, float b, String name) {
		this.name = name;
		this.red = r;
		this.green = g;
		this.blue = b;
		this.setWords(new String[] { WordData.Modifier, WordData.Image, WordData.Weave, this.name });
		this.setRarity(0.98F);
	}

	@Override
	public ArrayList<Rule> createRules() {
		return CollectionUtils.buildList(new Rule(IGrammarAPI.COLOR_BASIC, CollectionUtils.buildList(this.identifier()), 1));
	}

	@Override
	public void registerLogic(IAgeController controller, long seed) {
		Color color = controller.popModifier(ModifierUtils.COLOR).asColor();
		if (color != null) {
			color = color.average(red, green, blue);
		} else {
			color = new Color(red, green, blue);
		}
		controller.setModifier(ModifierUtils.COLOR, color);
	}

	@Override
	public String identifier() {
		return "Mod" + name;
	}

	@Override
	public String displayName() {
		return name + " Color";
	}
}
