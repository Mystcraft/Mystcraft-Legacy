package com.xcompwiz.mystcraft.api.impl.instability;

import java.util.Collection;

import com.xcompwiz.mystcraft.api.instability.IInstabilityAPI;
import com.xcompwiz.mystcraft.api.instability.IInstabilityProvider;
import com.xcompwiz.mystcraft.instability.InstabilityManager;

public class InstabilityAPIDelegate implements IInstabilityAPI {

	@Override
	public boolean registerInstability(String identifier, IInstabilityProvider provider, int activationcost) {
		return InstabilityManager.registerProvider(identifier, provider, activationcost);
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
