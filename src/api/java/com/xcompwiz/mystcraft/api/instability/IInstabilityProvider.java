package com.xcompwiz.mystcraft.api.instability;

/**
 * Provides an instability mechanic description and registers effects using the same approach as the symbols. Implement and register this through the
 * InstabilityAPI to add your own instability mechanics to Mystcraft.
 */
public interface IInstabilityProvider {

	/**
	 * Called when the provider should register its effects to the passed controller. This will be called only once during instability construction, no matter
	 * what level the provider is for the world (regardless of the number of times 'stacked'). Instability effects may be reconstructed many times during a
	 * session.
	 * @param level The level (stack count) of the provider [1-inf)
	 */
	public void addEffects(InstabilityDirector controller, Integer level);
}
