package com.xcompwiz.mystcraft.instability.bonus;

import java.lang.reflect.Constructor;

import net.minecraft.world.World;

import com.google.common.collect.ObjectArrays;
import com.xcompwiz.mystcraft.instability.bonus.InstabilityBonusManager.IInstabilityBonus;
import com.xcompwiz.mystcraft.instability.bonus.InstabilityBonusManager.IInstabilityBonusProvider;
import com.xcompwiz.mystcraft.logging.LoggerUtils;

public class BonusProvider implements IInstabilityBonusProvider {

	private Class<? extends IInstabilityBonus>			bonusclass;
	private Object[]									itemCtorArgs;

	private Constructor<? extends IInstabilityBonus>	itemCtor;

	public BonusProvider(Class<? extends IInstabilityBonus> bonusclass, Object... itemCtorArgs) {
		this.bonusclass = bonusclass;
		this.itemCtorArgs = itemCtorArgs;

		Class<?>[] ctorArgClasses = new Class<?>[itemCtorArgs.length + 2];
		ctorArgClasses[0] = InstabilityBonusManager.class;
		ctorArgClasses[1] = World.class;
		for (int idx = 0; idx < itemCtorArgs.length; ++idx) {
			ctorArgClasses[idx + 2] = itemCtorArgs[idx].getClass();
		}
		try {
			itemCtor = bonusclass.getConstructor(ctorArgClasses);
		} catch (Exception e) {
			LoggerUtils.error("Caught an exception during instability bonus registration");
			LoggerUtils.error(e.toString());
			throw new RuntimeException("Error when getting constructor for generic instability bonus class " + bonusclass.getCanonicalName(), e);
		}
	}

	@Override
	public void register(InstabilityBonusManager bonusmanager, World world) {
		try {
			Object[] args = itemCtorArgs;
			args = ObjectArrays.concat(world, args);
			args = ObjectArrays.concat(bonusmanager, args);
			IInstabilityBonus bonus = itemCtor.newInstance(args);
			bonusmanager.register(bonus);
		} catch (Exception e) {
			LoggerUtils.error("Caught an exception during instability bonus construction usage");
			throw new RuntimeException("Error when building generic instability bonus handler from class " + bonusclass.getCanonicalName() + " with args " + itemCtorArgs, e);
		}
	}
}
