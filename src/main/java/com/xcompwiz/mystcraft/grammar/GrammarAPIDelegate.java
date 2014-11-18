package com.xcompwiz.mystcraft.grammar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.xcompwiz.mystcraft.api.internal.IGrammarAPI;
import com.xcompwiz.mystcraft.grammar.GrammarGenerator.Rule;
import com.xcompwiz.mystcraft.symbol.IAgeSymbol;
import com.xcompwiz.mystcraft.symbol.SymbolManager;
import com.xcompwiz.util.CollectionUtils;

public class GrammarAPIDelegate implements IGrammarAPI {

	@Override
	public void registerGrammarRule(String parent, Integer rank, String... args) {
		ArrayList<String> list = CollectionUtils.buildList(args);
		GrammarGenerator.registerRule(new Rule(parent, list, rank));
	}

	public Collection<IAgeSymbol> getSymbolsExpandingToken(String token) {
		// First, grab all the rules
		List<Rule> rules = new ArrayList<Rule>();
		List<Rule> tokenrules = GrammarGenerator.getAllRules(token);
		if (tokenrules != null) rules.addAll(tokenrules);

		Set<IAgeSymbol> symbols = new HashSet<IAgeSymbol>();

		// Get symbols
		for (Rule rule : rules) {
			for (String rule_token : rule.getValues()) {
				if (!SymbolManager.hasBinding(rule_token)) continue;
				IAgeSymbol symbol = SymbolManager.getAgeSymbol(rule_token);
				if (symbol == null) continue;
				symbols.add(symbol);
			}
		}
		return symbols;
	}

	@Override
	public Collection<String> getTokensProducingToken(String token) {
		Set<String> set = new HashSet<String>();
		List<Rule> rules = GrammarGenerator.getParentRules(token);
		for (Rule rule : rules) {
			set.add(rule.getParent());
		}
		return set;
	}

	@Override
	public List<String> generateFromToken(String root, Random rand) {
		GrammarTree tree = new GrammarTree(root);
		return tree.getExpanded(rand);
	}

	@Override
	public List<String> generateFromToken(String root, Random rand, List<String> parsed) {
		GrammarTree tree = new GrammarTree(root);
		tree.parseTerminals(parsed, rand);
		return tree.getExpanded(rand);
	}
}
