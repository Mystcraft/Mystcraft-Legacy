package com.xcompwiz.mystcraft.api.impl;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class WrapperBuilder {

	private final Constructor<?> itemCtor;

	public WrapperBuilder(Class<?> clazz) {
		try {
			itemCtor = clazz.getConstructor(String.class);
		} catch (Exception e) {
			throw new RuntimeException("Kick XComp. He's a derp.", e);
		}
	}

	public Object newInstance(String owner) throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
		return itemCtor.newInstance(owner);
	}

}
