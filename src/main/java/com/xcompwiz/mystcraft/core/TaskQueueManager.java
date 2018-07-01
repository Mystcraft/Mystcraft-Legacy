package com.xcompwiz.mystcraft.core;

import java.util.Collection;
import java.util.HashSet;

public class TaskQueueManager {
	private static Collection<Runnable> onServerShutdown = new HashSet<Runnable>();

	public static void runOnServerShutdown(Runnable runnable) {
		onServerShutdown.add(runnable);
	}

	public synchronized static void onServerStop() {
		Collection<Runnable> collection = onServerShutdown;
		onServerShutdown = new HashSet<Runnable>();
		for (Runnable runnable : collection) {
			runnable.run();
		}
		collection.clear();
	}
}
