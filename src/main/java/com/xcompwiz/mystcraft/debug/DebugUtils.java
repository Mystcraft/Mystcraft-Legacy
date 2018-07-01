package com.xcompwiz.mystcraft.debug;

import com.xcompwiz.mystcraft.debug.DebugHierarchy.DebugNode;
import com.xcompwiz.mystcraft.debug.DebugHierarchy.IDebugElement;
import com.xcompwiz.mystcraft.world.agedata.AgeData;

public class DebugUtils {

	public static DebugNode getDebugNodeForAge(AgeData age) {
		DebugNode current = DebugHierarchy.root;
		current = current.getOrCreateNode("ages");
		current = current.getOrCreateNode(age.mapName);
		return current;
	}

	public static IDebugElement getElement(String address) {
		if (address == null)
			return null;
		String[] array = address.split("\\.");
		DebugNode current = DebugHierarchy.root;
		int i = 0;
		for (; i < array.length - 1; ++i) {
			IDebugElement next = current.get(array[i]);
			if (next == null) {
				return null;
			}
			if (!(next instanceof DebugNode))
				throw new RuntimeException(String.format("Cannot expand debug element to address %s as a non-node type lies in the address (@%s).", address, array[i]));
			current = (DebugHierarchy.DebugNode) next;
		}
		if (array.length <= i)
			return current;
		return current.get(array[i]);
	}

	public static void register(String address, IDebugElement elem) {
		if (elem instanceof DebugNode)
			throw new RuntimeException("Cannot register nodes as children. Use DebugNode.createNode instead.");
		String[] array = address.split("\\.");
		DebugNode current = DebugHierarchy.root;
		int i = 0;
		for (; i < array.length - 1; ++i) {
			IDebugElement next = current.get(array[i]);
			if (next == null) {
				next = current.getOrCreateNode(array[i]);
			}
			if (!(next instanceof DebugNode))
				throw new RuntimeException(String.format("Cannot add debug element to address %s as a non-node type lies in the address (@%s).", address, array[i]));
			current = (DebugHierarchy.DebugNode) next;
		}
		current.addChild(array[i], elem);
	}
}
