package com.xcompwiz.mystcraft.api.instability;

import com.xcompwiz.mystcraft.api.world.logic.IEnvironmentalEffect;

/**
 * Provides methods for generating boilerplate InstabilityProviders. These functions do not register the instability effect directly. Make sure you do that
 * through the {@link IInstabilityAPI}. The implementation of this is provided via the MystAPI. Do NOT implement this yourself!
 * @author xcompwiz
 */
public interface IInstabilityFactory {

	/**
	 * Creates (but does not register!) a new instability provider for a given effect
	 * @param effectclass The class to use
	 * @param uselevel false: The effect will be initialized many times, based on level. true: The level will be passed to the effect constructor as the first
	 *            arg (must be of type Integer).
	 * @param itemCtorArgs The args to use on the constructor (the constructor will be found by argument types)
	 * @return The produced InstabilityProvider, ready to be registered
	 */
	public IInstabilityProvider createProviderForEffect(Class<? extends IEnvironmentalEffect> effectclass, boolean uselevel, Object... itemCtorArgs);

}