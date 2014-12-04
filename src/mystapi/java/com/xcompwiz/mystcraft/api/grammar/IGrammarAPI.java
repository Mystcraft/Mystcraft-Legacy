package com.xcompwiz.mystcraft.api.grammar;

import java.util.Collection;
import java.util.List;
import java.util.Random;

/**
 * Provides methods for interacting with the grammar rules for the symbol generation system. The implementation of this is provided by MystAPI. Do NOT implement
 * this yourself!
 * @author xcompwiz
 */
public interface IGrammarAPI {

	/**
	 * Registers a rule for the grammar system. In general, symbols providing critical logic should only have a rule for that element. Otherwise, there is no
	 * limit to the number or kinds of rules that can produce a symbol. Rules must be registered before post-init. Examples:
	 * {@code registerGrammarRule(IGrammarAPI.TERRAIN, 1.0F, IGrammarAPI.BLOCK_TERRAIN, "SymbolIdentifer")}
	 * {@code registerGrammarRule(IGrammarAPI.TERRAINALT, 1.0F, IGrammarAPI.BLOCK_STRUCTURE, "SymbolIdentifer")}
	 * @param parent The token to expand
	 * @param rank The rarity ranking compared to others with the same parent (0 super common, 1 very common, 2 common, 3...)
	 * @param args The tokens to expand to
	 */
	void registerGrammarRule(String parent, Integer rank, String... args);

	/**
	 * Produces a list of symbols from the given token
	 * @param token The token to expand
	 * @return A collection of the identifiers of all the symbols which are contained in rules which directly expand the token
	 */
	//Collection<IAgeSymbol> getSymbolsExpandingToken(String token);

	/**
	 * Produces a list of all the tokens which have rules that produce the provided token
	 * @param token The token produced
	 * @return A collection of all the parents of rules producing the given token
	 */
	Collection<String> getTokensProducingToken(String token);

	/**
	 * Produces a phrase from the given token
	 * @param token The token to use as the root of the phrase
	 * @param rand The random number generator to use during expansion
	 * @return The generated phrase as a list of terminals. These should all be symbol identifiers.
	 */
	List<String> generateFromToken(String root, Random rand);

	/**
	 * Produces a phrase from the given token and a list of tokens to include, in order
	 * @param token The token to use as the root of the phrase
	 * @param rand The random number generator to use during parsing and expansion
	 * @param written A set of symbols to include, in order, which will be parsed like when generating an age.
	 * @return The generated phrase as a list of terminals. These should all be symbol identifiers.
	 */
	List<String> generateFromToken(String root, Random rand, List<String> written);
}
