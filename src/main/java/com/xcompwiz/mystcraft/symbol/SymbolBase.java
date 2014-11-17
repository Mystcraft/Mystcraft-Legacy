package com.xcompwiz.mystcraft.symbol;

import java.util.ArrayList;

import net.minecraft.util.StatCollector;

import com.xcompwiz.mystcraft.core.InternalAPI;
import com.xcompwiz.mystcraft.grammar.GrammarGenerator.Rule;
import com.xcompwiz.mystcraft.world.IAgeController;

public abstract class SymbolBase implements IAgeSymbol {

	private ArrayList<Rule>	rules;
	private String[]		words;

	public void setWords(String[] words) {
		this.words = words;
	}

	public IAgeSymbol setCardRank(Integer cardrank) {
		if (cardrank == null) return this;
		InternalAPI.symbolValues.setSymbolCardRank(this, cardrank);
		return this;
	}

	public final ArrayList<Rule> getRules() {
		ArrayList<Rule> out = createRules();
		if (rules != null) {
			if (out == null) out = new ArrayList<Rule>();
			out.addAll(rules);
		}
		return out;
	}

	protected ArrayList<Rule> createRules() {
		return null;
	}

	public void addRule(Rule rule) {
		if (rules == null) rules = new ArrayList<Rule>();
		rules.add(rule);
	}

	/**
	 * Returns the unlocalized name
	 * 
	 * @return The lookup key used to map the symbol to a user readable text string
	 */
	public String getUnlocalizedName() {
		return "mystsymbol." + this.identifier();
	}

	/**
	 * Returns the user localized name
	 * 
	 * @return Name the user sees for the symbol
	 */
	@Override
	public String displayName() {
		return StatCollector.translateToLocal(this.getUnlocalizedName() + ".name");
	}

	@Override
	public abstract void registerLogic(IAgeController controller, long seed);

	@Override
	public int instabilityModifier(int count) {
		return 0;
	}

	@Override
	public abstract String identifier();

	@Override
	public final String[] getPoem() {
		return words;
	}
}
