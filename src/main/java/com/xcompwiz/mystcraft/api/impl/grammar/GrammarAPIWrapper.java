package com.xcompwiz.mystcraft.api.impl.grammar;

import java.util.Collection;
import java.util.List;
import java.util.Random;

import com.xcompwiz.mystcraft.api.hook.IGrammarAPI;
import com.xcompwiz.mystcraft.api.impl.APIWrapper;
import com.xcompwiz.mystcraft.api.impl.InternalAPI;

public class GrammarAPIWrapper extends APIWrapper implements IGrammarAPI {

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

}
