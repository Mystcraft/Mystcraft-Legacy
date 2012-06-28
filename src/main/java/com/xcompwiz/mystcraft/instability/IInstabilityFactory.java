package com.xcompwiz.mystcraft.instability;

import com.xcompwiz.mystcraft.api.world.logic.IEnvironmentalEffect;
import com.xcompwiz.mystcraft.oldapi.internal.IInstabilityAPI;

/**
 * Provides methods for generating boilerplate InstabilityProviders. These functions do not register the instability
 * effect directly. Make sure you do that through the {@link IInstabilityAPI}. The implementation of this is provided via the
 * MystAPI. Do NOT implement this yourself!
 * 
 * @author xcompwiz
 */
public interface IInstabilityFactory {

	/**
	 * Creates (but does not register!) a new instability provider for a given effect
	 * 
	 * @return The produced InstabilityProvider, ready to be registered
	 */
	public IInstabilityProvider createProviderForEffect(Class<? extends IEnvironmentalEffect> effectclass, Object... itemCtorArgs);

}
