package com.xcompwiz.mystcraft.core;

import java.util.Collection;
import java.util.HashMap;

public abstract class DebugDataTracker {

	public interface Callback {
		void setState(boolean state);
	}

	private static HashMap<String, String>		vars	= new HashMap<String, String>();
	private static HashMap<String, Callback>	flags	= new HashMap<String, Callback>();

	public static void clearVars() {
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

	public static void register(String flag, Callback callback) {
		flags.put(flag.replaceAll(" ", "_"), callback);
	}

	public static void setFlag(String command, boolean b) {
		flags.get(command).setState(b);
	}

	public static Collection<String> getFlags() {
		return flags.keySet();
	}
}
