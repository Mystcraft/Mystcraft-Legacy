package com.xcompwiz.mystcraft.oldapi.internal;

import java.util.Collection;

import com.xcompwiz.mystcraft.api.MystAPI;
import com.xcompwiz.mystcraft.api.instability.IInstabilityProvider;

/**
 * Provides methods for registering providers to and interacting with the instability system. The implementation of this is provided by {@link MystAPI}. Do NOT
 * implement this yourself!
 * @author xcompwiz
 */
public interface IInstabilityAPI {

	/**
	 * Registers an instability provider to the instability system This makes it available for selection when a world is unstable as governed by its internal
	 * stability values
	 */
	void registerInstability(String identifier, IInstabilityProvider provider, int activationcost);

	/**
	 * @return An immutable list of all the instability providers registered
	 */
	public Collection<String> getAllInstabilityProviders();

	/**
	 * Maps an identifier to an instability provider
	 * @param identifier The indetifier to map
	 * @return The instability provider with that id or null if there isn't one
	 */
	public IInstabilityProvider getInstabilityProvider(String identifier);
}
