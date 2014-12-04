package com.xcompwiz.mystcraft.instability;

import com.xcompwiz.mystcraft.api.instability.IInstabilityFactory;
import com.xcompwiz.mystcraft.api.instability.IInstabilityProvider;
import com.xcompwiz.mystcraft.api.world.logic.IEnvironmentalEffect;
import com.xcompwiz.mystcraft.instability.providers.InstabilityProvider;

public class InstabilityFactory implements IInstabilityFactory {

	//TODO: (API) Instability API
	@Override
	public IInstabilityProvider createProviderForEffect(Class<? extends IEnvironmentalEffect> effectclass, Object... itemCtorArgs) {
		return new InstabilityProvider(false, effectclass, itemCtorArgs);
	}

}
