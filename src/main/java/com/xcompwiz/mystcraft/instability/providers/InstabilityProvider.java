package com.xcompwiz.mystcraft.instability.providers;

import java.lang.reflect.Constructor;

import com.google.common.collect.ObjectArrays;
import com.xcompwiz.mystcraft.api.instability.IInstabilityProvider;
import com.xcompwiz.mystcraft.api.instability.InstabilityDirector;
import com.xcompwiz.mystcraft.api.world.logic.IEnvironmentalEffect;
import com.xcompwiz.mystcraft.logging.LoggerUtils;

public class InstabilityProvider implements IInstabilityProvider {

	private Class<? extends IEnvironmentalEffect>		effectclass;
	private Object[]									itemCtorArgs;
	private boolean										uselevel;

	private Constructor<? extends IEnvironmentalEffect>	itemCtor;

	public InstabilityProvider(boolean uselevel, Class<? extends IEnvironmentalEffect> effectclass, Object... itemCtorArgs) {
		this.effectclass = effectclass;
		this.itemCtorArgs = itemCtorArgs;
		this.uselevel = uselevel;

		Class<?>[] ctorArgClasses = new Class<?>[itemCtorArgs.length + (this.uselevel ? 1 : 0)];
		if (this.uselevel) ctorArgClasses[0] = int.class;
		for (int idx = 0; idx < itemCtorArgs.length; ++idx) {
			ctorArgClasses[idx + (this.uselevel ? 1 : 0)] = itemCtorArgs[idx].getClass();
		}
		try {
			itemCtor = effectclass.getConstructor(ctorArgClasses);
		} catch (Exception e) {
			LoggerUtils.error("Caught an exception during instability registration");
			LoggerUtils.error(e.toString());
			throw new RuntimeException("Error when building generic instability effect from effect class " + effectclass.getCanonicalName(), e);
		}
	}

	@Override
	public void addEffects(InstabilityDirector controller, Integer level) {
		try {
			Object[] args = itemCtorArgs;
			if (uselevel) args = ObjectArrays.concat(level.intValue(), itemCtorArgs);
			for (int i = 0; i < (uselevel ? 1 : level); ++i) {
				IEnvironmentalEffect effect = itemCtor.newInstance(args);
				controller.registerEffect(effect);
			}
		} catch (Exception e) {
			LoggerUtils.error("Caught an exception during instability usage");
			throw new RuntimeException("Error when building generic instability effect from effect class " + effectclass.getCanonicalName() + " with args " + itemCtorArgs, e);
		}
	}
}
