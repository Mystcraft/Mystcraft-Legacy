package com.xcompwiz.mystcraft.api.impl.instability;

import com.xcompwiz.mystcraft.api.instability.IInstabilityFactory;
import com.xcompwiz.mystcraft.api.instability.IInstabilityProvider;
import com.xcompwiz.mystcraft.api.world.logic.IEnvironmentalEffect;
import com.xcompwiz.mystcraft.instability.providers.InstabilityProvider;

public class InstabilityFactory implements IInstabilityFactory {

	@Override
	public IInstabilityProvider createProviderForEffect(Class<? extends IEnvironmentalEffect> effectclass, boolean uselevel, Object... itemCtorArgs) {
		return new InstabilityProvider(uselevel, effectclass, itemCtorArgs);
	}

}
