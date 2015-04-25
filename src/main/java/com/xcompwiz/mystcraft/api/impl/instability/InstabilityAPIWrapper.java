package com.xcompwiz.mystcraft.api.impl.instability;

import java.util.Collection;

import com.xcompwiz.mystcraft.api.hook.IInstabilityAPI;
import com.xcompwiz.mystcraft.api.impl.APIWrapper;
import com.xcompwiz.mystcraft.api.impl.InternalAPI;
import com.xcompwiz.mystcraft.api.instability.IInstabilityProvider;

public class InstabilityAPIWrapper extends APIWrapper implements IInstabilityAPI {

	public InstabilityAPIWrapper(String modname) {
		super(modname);
	}

	@Override
	public boolean registerInstability(String identifier, IInstabilityProvider provider, int activationcost) {
		return InternalAPI.instability.registerInstability(identifier, provider, activationcost);
	}

	@Override
	public void addCards(String deck, String identifier, int count) {
		InternalAPI.instability.addCards(deck, identifier, count);		
	}

	@Override
	public Collection<String> getAllInstabilityProviders() {
		return InternalAPI.instability.getAllInstabilityProviders();
	}

	@Override
	public IInstabilityProvider getInstabilityProvider(String identifier) {
		return InternalAPI.instability.getInstabilityProvider(identifier);
	}

}
