package com.xcompwiz.mystcraft.api.impl.instability;

import com.xcompwiz.mystcraft.api.hook.InstabilityFactory;
import com.xcompwiz.mystcraft.api.impl.APIWrapper;
import com.xcompwiz.mystcraft.api.impl.InternalAPI;
import com.xcompwiz.mystcraft.api.instability.IInstabilityProvider;
import com.xcompwiz.mystcraft.api.world.logic.IEnvironmentalEffect;

public class InstabilityFactWrapper extends APIWrapper implements InstabilityFactory {

	public InstabilityFactWrapper(String modname) {
		super(modname);
	}

	@Override
	public IInstabilityProvider createProviderForEffect(Class<? extends IEnvironmentalEffect> effectclass, boolean uselevel, Object... itemCtorArgs) {
		return InternalAPI.instabilityFact.createProviderForEffect(effectclass, uselevel, itemCtorArgs);
	}

}
