package com.xcompwiz.mystcraft.instability;

import java.util.Collection;

import com.xcompwiz.mystcraft.api.instability.IInstabilityAPI;
import com.xcompwiz.mystcraft.api.instability.IInstabilityProvider;

public class InstabilityAPIDelegate implements IInstabilityAPI {

	@Override
	public void registerInstability(String identifier, IInstabilityProvider provider, int activationcost) {
		InstabilityManager.registerProvider(identifier, provider, activationcost);
	}

	@Override
	public Collection<String> getAllInstabilityProviders() {
		return InstabilityManager.getAllProviders();
	}

	@Override
	public IInstabilityProvider getInstabilityProvider(String identifier) {
		return InstabilityManager.getProvider(identifier);
	}

	@Override
	public void addCards(String deck, String identifier, int count) {
		InstabilityManager.addCards(deck, identifier, count);
	}

}
