package com.xcompwiz.mystcraft.api.world.logic;

import java.util.List;

import com.xcompwiz.mystcraft.api.symbol.ModifierUtils;
import com.xcompwiz.mystcraft.api.util.Color;
import com.xcompwiz.mystcraft.api.util.ColorGradient;
import com.xcompwiz.mystcraft.api.world.AgeDirector;

/**
 * The modifier object class This wraps the modifier objects registered to the {@link AgeDirector} during symbol logic initialization. See {@link ModifierUtils}
 * for more advanced modifier usages.
 * @author xcompwiz
 */
public final class Modifier {
	private Object value;

	/** Amount of instability added if the modifier is left in the system */
	public int dangling;

	public static final int dangling_default = 100;

	public Modifier() {
		this(null);
	}

	public Modifier(Object value) {
		this(value, dangling_default);
	}

	/**
	 * @param value contained value
	 * @param dangling The amount of instability to add if unused
	 */
	public Modifier(Object value, int dangling) {
		this.value = value;
		this.dangling = dangling;
	}

	/**
	 * Returns the stored object without any casting
	 * @return Returns the stored object or null
	 */
	public Object asObject() {
		return value;
	}

	/**
	 * Casts the stored object as a Number
	 * @return Returns the stored object as a Number or null
	 */
	public Number asNumber() {
		if (value instanceof Number) {
			return (Number) value;
		}
		return null;
	}

	/**
	 * Casts the stored object as a Color
	 * @return Returns the stored object as a Color or null
	 */
	public Color asColor() {
		if (value instanceof Color) {
			return (Color) value;
		}
		return null;
	}

	/**
	 * Casts the stored object as a gradient
	 * @return Returns the stored object as a gradient or null
	 */
	public ColorGradient asGradient() {
		if (value instanceof ColorGradient) {
			return (ColorGradient) value;
		}
		return null;
	}

	/**
	 * Casts the stored object as a list
	 * @param <T> The type of element in the list
	 * @return Returns the stored object as a list or null
	 */
	@SuppressWarnings("unchecked")
	public <T> List<T> asList() {
		if (value instanceof List) {
			return (List<T>) value;
		}
		return null;
	}
}
