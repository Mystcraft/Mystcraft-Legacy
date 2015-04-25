package com.xcompwiz.mystcraft.api.hook;

import java.util.List;

import com.xcompwiz.mystcraft.api.symbol.IAgeSymbol;

/**
 * Provides functions for registering different aspects to the Symbol system. You can blacklist identifiers and register your own symbols. The implementation of
 * this is provided by MystAPI. Do NOT implement this yourself!
 * @author xcompwiz
 */
public interface SymbolAPI {

	/**
	 * Use this to turn off/remove symbols by identifier (ex. from the core mod).
	 * @param identifier The identifier of the symbol to prevent from being registered/unregister
	 */
	public void blacklistIdentifier(String identifier);

	/**
	 * Registers a logic provider "Symbol" to the system. The symbol should provide logic elements, set values, or push modifier values to the IAgeController
	 * passed to it. If a symbol throws an exception during profiling then the symbol will not be registered and the identifier will be blacklisted. Note: Don't
	 * forget to create grammar rules for your symbols! See {@link GrammarAPI}
	 * @param symbol The AgeSymbol to register
	 * @return Success
	 */
	public boolean registerSymbol(IAgeSymbol symbol);

	/**
	 * Registers a logic provider "Symbol" to the system. The symbol should provide logic elements, set values, or push modifier values to the IAgeController
	 * passed to it. If a symbol throws an exception during profiling then the symbol will not be registered and the identifier will be blacklisted. Note: Don't
	 * forget to create grammar rules for your symbols! See {@link GrammarAPI}
	 * @param symbol The AgeSymbol to register
	 * @param generateConfigOption Whether or not a config entry should be created for the symbol
	 * @return Success
	 */
	public boolean registerSymbol(IAgeSymbol symbol, boolean generateConfigOption);

	/**
	 * Returns a list of all of the registered symbol identifiers in the system
	 * @return A new list of all the symbol identifiers
	 */
	public List<IAgeSymbol> getAllRegisteredSymbols();

	/**
	 * Retrieves the symbol that maps to the identifier
	 * @param identifier The identifier for the symbol
	 * @return The symbol with the given identifier, or null if none is registered with that id
	 */
	public IAgeSymbol getSymbol(String identifier);

	/**
	 * Returns the id of the mod which registered the symbol
	 * @param identifier The identifier of the symbol
	 * @return The unique id of the mod which registered the symbol
	 */
	public String getSymbolOwner(String identifier);
}
