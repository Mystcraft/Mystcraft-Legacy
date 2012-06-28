package com.xcompwiz.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public final class CollectionUtils {

	public static <T> HashSet<T> buildSet(T... args) {
		HashSet<T> set = new HashSet<T>();
		for (T arg : args) {
			set.add(arg);
		}
		return set;
	}

	public static <T> ArrayList<T> buildList(T... args) {
		ArrayList<T> list = new ArrayList<T>();
		for (T arg : args) {
			list.add(arg);
		}
		return list;
	}

	public static <T> ArrayList<T> buildList(List<T> prefix, List<T> suffix, T... args) {
		ArrayList<T> list = new ArrayList<T>();
		if (prefix != null) list.addAll(prefix);
		for (T arg : args) {
			list.add(arg);
		}
		if (suffix != null) list.addAll(suffix);
		return list;
	}

	public static <R, T> List<T> getOrCreateElement(R key, HashMap<R, List<T>> map) {
		List<T> rules = map.get(key);
		if (rules == null) {
			rules = new ArrayList<T>();
			map.put(key, rules);
		}
		return rules;
	}

}
