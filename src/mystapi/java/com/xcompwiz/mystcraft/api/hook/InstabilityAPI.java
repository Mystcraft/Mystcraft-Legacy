package com.xcompwiz.mystcraft.api.hook;

import java.util.Collection;

import com.xcompwiz.mystcraft.api.APIInstanceProvider;
import com.xcompwiz.mystcraft.api.instability.IInstabilityProvider;

/**
 * Provides methods for registering providers to and interacting with the instability system. The implementation of this is provided by {@link APIInstanceProvider}. Do NOT
 * implement this yourself!
 * @author xcompwiz
 */
public interface InstabilityAPI {

	/**
	 * Registers an instability provider to the instability system.
	 * @return 
	 */
	boolean registerInstability(String identifier, IInstabilityProvider provider, int activationcost);

	/**
	 * Adds card instances of a registered effect to the given deck by deck name.  If the deck does not exist, this throws an error.
	 * @param deck The name of the deck to register the effect to. Mystcraft provided options are basic, harsh, destructive, eating, and death.
	 * @param identifier The identifier of the provider to create cards for. If the identifier does not map to a provider, then this will log an error.
	 * @param count The number of cards of the identifier to add to the deck.
	 */
	void addCards(String deck, String identifier, int count);

	/**
	 * @return An immutable list of all the instability providers registered
	 */
	public Collection<String> getAllInstabilityProviders();

	/**
	 * Maps an identifier to an instability provider
	 * @param identifier The identifier to map
	 * @return The instability provider with that id or null if there isn't one
	 */
	public IInstabilityProvider getInstabilityProvider(String identifier);
}
