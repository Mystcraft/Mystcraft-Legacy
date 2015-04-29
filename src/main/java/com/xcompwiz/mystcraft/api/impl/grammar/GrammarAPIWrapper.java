package com.xcompwiz.mystcraft.api.impl.grammar;

import java.util.Collection;
import java.util.List;
import java.util.Random;

import com.xcompwiz.mystcraft.api.hook.GrammarAPI;
import com.xcompwiz.mystcraft.api.impl.APIWrapper;
import com.xcompwiz.mystcraft.api.impl.InternalAPI;
import com.xcompwiz.mystcraft.api.symbol.IAgeSymbol;

public class GrammarAPIWrapper extends APIWrapper implements GrammarAPI {

	public GrammarAPIWrapper(String modname) {
		super(modname);
	}

	@Override
	public void registerGrammarRule(String parent, Integer rank, String... args) {
		InternalAPI.grammar.registerGrammarRule(parent, rank, args);
	}

	@Override
	public Collection<String> getTokensProducingToken(String token) {
		return InternalAPI.grammar.getTokensProducingToken(token);
	}

	@Override
	public List<String> generateFromToken(String root, Random rand) {
		return InternalAPI.grammar.generateFromToken(root, rand);
	}

	@Override
	public List<String> generateFromToken(String root, Random rand, List<String> written) {
		return InternalAPI.grammar.generateFromToken(root, rand, written);
	}

	@Override
	public Collection<IAgeSymbol> getSymbolsExpandingToken(String token) {
		return InternalAPI.grammar.getSymbolsExpandingToken(token);
	}

}