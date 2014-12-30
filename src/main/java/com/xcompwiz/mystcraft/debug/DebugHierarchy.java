package com.xcompwiz.mystcraft.debug;

import java.util.Collection;
import java.util.HashMap;

import net.minecraft.command.ICommandSender;

public abstract class DebugHierarchy {
	public static final DebugNode	root	= new DebugNode();

	public interface IDebugElement {}

	public static class DebugNode implements IDebugElement {
		public final DebugNode					parent;
		private HashMap<String, IDebugElement>	children	= new HashMap<String, IDebugElement>();
		private HashMap<IDebugElement, String>	keymap		= new HashMap<IDebugElement, String>();

		private DebugNode() {
			this.parent = null;
		}

		private DebugNode(DebugNode parent) {
			this.parent = parent;
		}

		public void addChild(String id, IDebugElement elem) {
			if (elem instanceof DebugNode) throw new RuntimeException("Cannot register nodes as children. Use DebugNode.createNode instead.");
			if (id.contains(".")) throw new RuntimeException("Cannot register elements with '.' in id.");
			String key = id.replaceAll(" ", "_");
			if (children.containsKey(key)) {
				keymap.remove(children.remove(key));
			}
			keymap.put(elem, key);
			children.put(key, elem);
		}

		public DebugNode getOrCreateNode(String id) {
			if (id.contains(".")) throw new RuntimeException("Cannot register elements with '.' in id.");
			String key = id.replaceAll(" ", "_");
			if (children.containsKey(key)) {
				IDebugElement node = children.get(key);
				if (node instanceof DebugNode) return (DebugNode) node;
				throw new RuntimeException("Address already registered to non-node element.");
			}
			DebugNode elem = new DebugNode(this);
			keymap.put(elem, key);
			children.put(key, elem);
			return elem;
		}

		public void removeChild(IDebugElement elem) {
			String key = keymap.remove(elem);
			if (key != null) children.remove(key);
		}

		public void removeChild(String key) {
			IDebugElement elem = children.remove(key);
			if (elem != null) keymap.remove(elem);
		}

		public Collection<String> getChildren() {
			return this.children.keySet();
		}

		public IDebugElement get(String key) {
			return children.get(key);
		}
	}

	public interface DebugValueCallback extends IDebugElement {
		String get(ICommandSender agent);

		void set(ICommandSender agent, boolean state);
	}

	public interface DebugTaskCallback extends IDebugElement {
		void run(ICommandSender agent, Object... args);
	}
}
