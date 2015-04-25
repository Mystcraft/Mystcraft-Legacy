package com.xcompwiz.mystcraft.instability;

import com.xcompwiz.mystcraft.api.instability.InstabilityDirector;
import com.xcompwiz.mystcraft.api.instability.IInstabilityProvider;
import com.xcompwiz.mystcraft.api.world.logic.IEnvironmentalEffect;

public class InstabilityProfiler implements InstabilityDirector {
	private IInstabilityProvider	agent	= null;

	public void startProfiling(IInstabilityProvider symbol) {
		if (this.agent != null) throw new RuntimeException("Attempting to profile multiple InstabilityProviders at once!");
		this.agent = symbol;
	}

	public void endProfiling(IInstabilityProvider symbol) {
		if (this.agent != symbol) throw new RuntimeException("Mismatch in instability profiler termination!");
		this.agent = null;
	}

	@Override
	public int getInstabilityScore() {
		return 0;
	}

	@Override
	public void registerEffect(IEnvironmentalEffect effect) {}
}
