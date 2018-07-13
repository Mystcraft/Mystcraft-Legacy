package com.xcompwiz.mystcraft.api.impl.grammar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.xcompwiz.mystcraft.api.symbol.IAgeSymbol;
import com.xcompwiz.mystcraft.grammar.GrammarGenerator;
import com.xcompwiz.mystcraft.grammar.GrammarGenerator.Rule;
import com.xcompwiz.mystcraft.grammar.GrammarTree;
import com.xcompwiz.mystcraft.symbol.SymbolManager;
import com.xcompwiz.util.CollectionUtils;

import net.minecraft.util.ResourceLocation;

public class GrammarAPIDelegate {

	public void registerGrammarRule(ResourceLocation parent, Integer rank, ResourceLocation... args) {
		ArrayList<ResourceLocation> list = CollectionUtils.buildList(args);
		GrammarGenerator.registerRule(new Rule(parent, list, rank));
	}

	public Collection<IAgeSymbol> getSymbolsExpandingToken(ResourceLocation token) {
		// First, grab all the rules
		List<Rule> rules = new ArrayList<Rule>();
		List<Rule> tokenrules = GrammarGenerator.getAllRules(token);
		if (tokenrules != null)
			rules.addAll(tokenrules);

		Set<IAgeSymbol> symbols = new HashSet<IAgeSymbol>();

		// Get symbols
		for (Rule rule : rules) {
			for (ResourceLocation rule_token : rule.getValues()) {
				if (!SymbolManager.hasBinding(rule_token))
					continue;
				IAgeSymbol symbol = SymbolManager.getAgeSymbol(rule_token);
				if (symbol == null)
					continue;
				symbols.add(symbol);
			}
		}
		return symbols;
	}

	public Collection<ResourceLocation> getTokensProducingToken(ResourceLocation token) {
		Set<ResourceLocation> set = new HashSet<>();
		List<Rule> rules = GrammarGenerator.getParentRules(token);
		for (Rule rule : rules) {
			set.add(rule.getParent());
		}
		return set;
	}

	public List<ResourceLocation> generateFromToken(ResourceLocation root, Random rand) {
		GrammarTree tree = new GrammarTree(root);
		return tree.getExpanded(rand);
	}

	public List<ResourceLocation> generateFromToken(ResourceLocation root, Random rand, List<ResourceLocation> parsed) {
		GrammarTree tree = new GrammarTree(root);
		tree.parseTerminals(parsed, rand);
		return tree.getExpanded(rand);
	}
}
