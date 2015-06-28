package com.xcompwiz.mystcraft.symbol.modifiers;

import java.util.ArrayList;

import com.xcompwiz.mystcraft.api.grammar.GrammarData;
import com.xcompwiz.mystcraft.api.symbol.ModifierUtils;
import com.xcompwiz.mystcraft.api.util.Color;
import com.xcompwiz.mystcraft.api.word.WordData;
import com.xcompwiz.mystcraft.api.world.AgeDirector;
import com.xcompwiz.mystcraft.grammar.GrammarGenerator.Rule;
import com.xcompwiz.mystcraft.symbol.SymbolBase;
import com.xcompwiz.util.CollectionUtils;

public class SymbolColor extends SymbolBase {
	private float	red;
	private float	green;
	private float	blue;

	public SymbolColor(float r, float g, float b, String identifier) {
		super(identifier);
		this.red = r;
		this.green = g;
		this.blue = b;
		this.setWords(new String[] { WordData.Modifier, WordData.Image, WordData.Weave, this.identifier });
		this.setCardRank(0);
	}

	@Override
	public ArrayList<Rule> createRules() {
		return CollectionUtils.buildList(new Rule(GrammarData.COLOR_BASIC, CollectionUtils.buildList(this.identifier()), 1));
	}

	@Override
	public void registerLogic(AgeDirector controller, long seed) {
		Color color = controller.popModifier(ModifierUtils.COLOR).asColor();
		if (color != null) {
			color = color.average(red, green, blue);
		} else {
			color = new Color(red, green, blue);
		}
		controller.setModifier(ModifierUtils.COLOR, color);
	}
}
