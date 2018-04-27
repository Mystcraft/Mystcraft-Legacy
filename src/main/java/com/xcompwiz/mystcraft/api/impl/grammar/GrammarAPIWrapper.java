package com.xcompwiz.mystcraft.api.impl.grammar;

import java.util.Collection;
import java.util.List;
import java.util.Random;

import com.xcompwiz.mystcraft.api.hook.GrammarAPI;
import com.xcompwiz.mystcraft.api.impl.APIWrapper;
import com.xcompwiz.mystcraft.api.impl.InternalAPI;
import com.xcompwiz.mystcraft.api.symbol.IAgeSymbol;
import net.minecraft.util.ResourceLocation;

public class GrammarAPIWrapper extends APIWrapper implements GrammarAPI {

	public GrammarAPIWrapper(String modname) {
		super(modname);
	}

	@Override
	public void registerGrammarRule(ResourceLocation parent, Integer rank, ResourceLocation... args) {
		InternalAPI.grammar.registerGrammarRule(parent, rank, args);
	}

	@Override
	public Collection<ResourceLocation> getTokensProducingToken(ResourceLocation token) {
		return InternalAPI.grammar.getTokensProducingToken(token);
	}

	@Override
	public List<ResourceLocation> generateFromToken(ResourceLocation root, Random rand) {
		return InternalAPI.grammar.generateFromToken(root, rand);
	}

	@Override
	public List<ResourceLocation> generateFromToken(ResourceLocation root, Random rand, List<ResourceLocation> written) {
		return InternalAPI.grammar.generateFromToken(root, rand, written);
	}

	@Override
	public Collection<IAgeSymbol> getSymbolsExpandingToken(ResourceLocation token) {
		return InternalAPI.grammar.getSymbolsExpandingToken(token);
	}

}
