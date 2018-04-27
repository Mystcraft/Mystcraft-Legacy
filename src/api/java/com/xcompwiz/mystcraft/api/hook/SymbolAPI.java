package com.xcompwiz.mystcraft.api.hook;

import java.util.List;

import com.xcompwiz.mystcraft.api.APIInstanceProvider;
import com.xcompwiz.mystcraft.api.symbol.IAgeSymbol;
import com.xcompwiz.mystcraft.api.world.AgeDirector;
import net.minecraft.util.ResourceLocation;

/**
 * Provides functions for registering different aspects to the Symbol system. You can blacklist identifiers and register your own symbols. The implementation of
 * this is provided by {@link APIInstanceProvider}.
 * 
 * Note: Don't forget to create grammar rules for your symbols! See {@link GrammarAPI}
 * Note: Don't forget to set treasure rarities! See {@link SymbolValuesAPI}
 * @author xcompwiz
 */
public interface SymbolAPI {

	/**
	 * Use this to turn off/remove symbols by identifier (ex. from the core mod).
	 * @param identifier The identifier of the symbol to prevent from being registered/unregister
	 */
	public void blacklistIdentifier(ResourceLocation identifier);

	/**
	 * Returns a list of all of the registered symbols in the system
	 * @return A new list of all the symbols
	 */
	public List<IAgeSymbol> getAllRegisteredSymbols();

	/**
	 * Retrieves the symbol that maps to the identifier
	 * @param identifier The identifier for the symbol
	 * @return The symbol with the given identifier, or null if none is registered with that id
	 */
	public IAgeSymbol getSymbol(ResourceLocation identifier);

	/**
	 * Returns the id of the mod which registered the symbol
	 * @param identifier The identifier of the symbol
	 * @return The unique id of the mod which registered the symbol
	 */
	public String getSymbolOwner(ResourceLocation identifier);
}
