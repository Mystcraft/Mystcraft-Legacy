package com.xcompwiz.mystcraft.core;

import java.util.Collection;
import java.util.HashMap;

public abstract class DebugDataTracker {

	private static HashMap<String, String>	vars = new HashMap<String, String>();

	public static void clear() {
		vars.clear();
	}

	public static void set(String id, String val) {
		vars.put(id.replaceAll(" ", "_"), val);
	}

	public static String get(String id) {
		return vars.get(id);
	}

	public static Collection getParams() {
		return vars.keySet();
	}
}
