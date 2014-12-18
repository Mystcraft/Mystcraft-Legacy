package com.xcompwiz.mystcraft.debug;

import java.util.Collection;
import java.util.HashMap;

import net.minecraft.command.ICommandSender;

public abstract class DebugHierarchy {

	public interface IDebugElement {}

	public static class DebugNode implements IDebugElement {
		public final DebugNode					parent;
		private HashMap<String, IDebugElement>	children	= new HashMap<String, IDebugElement>();
		private HashMap<IDebugElement, String>	keymap		= new HashMap<IDebugElement, String>();

		public DebugNode(DebugNode parent) {
			this.parent = parent;
		}

		public void addChild(String id, IDebugElement elem) {
			String key = id.replaceAll(" ", "_");
			if (children.containsKey(key)) {
				keymap.remove(children.remove(key));
			}
			keymap.put(elem, key);
			children.put(key, elem);
		}

		public void removeChild(IDebugElement elem) {
			String key = keymap.remove(elem);
			if (key != null) children.remove(key);
		}

		public Collection<String> getChildren() {
			return this.children.keySet();
		}
	}

	public interface DebugValueCallback extends IDebugElement {
		String get(ICommandSender agent);

		void set(ICommandSender agent, boolean state);
	}

	public interface DebugTaskCallback extends IDebugElement {
		void run(ICommandSender agent, Object... args);
	}

	private static HashMap<String, DebugValueCallback>	callbacks	= new HashMap<String, DebugValueCallback>();
	private static HashMap<DebugValueCallback, String>	keymap		= new HashMap<DebugValueCallback, String>();

	public static void register(String id, DebugValueCallback callback) {
		String key = id.replaceAll(" ", "_");
		if (callbacks.containsKey(key)) {
			keymap.remove(callbacks.remove(key));
		}
		keymap.put(callback, key);
		callbacks.put(key, callback);
	}

	public static void remove(DebugValueCallback callback) {
		String key = keymap.remove(callback);
		if (key != null) callbacks.remove(key);
	}

	public static DebugValueCallback getCallback(ICommandSender agent, String command) {
		return callbacks.get(command);
	}

	public static Collection<String> getKeys() {
		return callbacks.keySet();
	}
}
