package com.xcompwiz.mystcraft.world.profiling;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;

import net.minecraft.command.ICommandSender;
import net.minecraft.world.chunk.Chunk;

import com.xcompwiz.mystcraft.debug.DebugUtils;
import com.xcompwiz.mystcraft.debug.DefaultValueCallback;

public class ChunkProfilerManager extends Thread {

	public static class ChunkProfileTask {

		private ChunkProfiler	profiler;
		private Chunk			chunk;

		public ChunkProfileTask(ChunkProfiler profiler, Chunk chunk) {
			this.profiler = profiler;
			this.chunk = chunk;
		}

		public void run() {
			profiler.profile(chunk);
		}

	}

	private static List<ChunkProfileTask>	profilingqueue	= new LinkedList<ChunkProfileTask>();
	private static Semaphore				semaphore		= new Semaphore(1, true);
	private static boolean					safesaveenabled	= false;

	static {
		//@formatter:off
		DebugUtils.register("global.profilerqueue.size", new DefaultValueCallback() { @Override public String get(ICommandSender agent) { return Integer.toString(profilingqueue.size()); }});
		//@formatter:on
	}

	private boolean							isRunning		= true;

	public static void addChunk(ChunkProfiler profiler, Chunk chunk) {
		try {
			semaphore.acquire();
		} catch (InterruptedException e) {
			throw new RuntimeException("Failed to acquire semaphore to add to chunk queue (interrupted)!");
		}
		profilingqueue.add(new ChunkProfileTask(profiler, chunk));
		semaphore.release();
	}

	public static int getSize() {
		return profilingqueue.size();
	}

	private void processQueue() {
		while (!profilingqueue.isEmpty()) {
			try {
				semaphore.acquire();
			} catch (InterruptedException e) {
				throw new RuntimeException("Failed to acquire semaphore to swap chunk queue (interrupted)!");
			}
			ChunkProfileTask task = profilingqueue.remove(0);
			semaphore.release();
			task.run();
		}
	}

	public void halt() {
		try {
			semaphore.acquire();
		} catch (InterruptedException e) {
			throw new RuntimeException("Failed to acquire semaphore to clear to chunk queue (interrupted)!");
		}
		profilingqueue.clear();
		semaphore.release();
		isRunning = false;
	}

	@Override
	public void run() {
		while (isRunning) {
			synchronized (this) {
				processQueue();
				try {
					this.wait(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	//TODO: 1.8 Once dimension (generation?) is threaded, we'll need to handle this on a per-dimension basis.
	// I _think_ that this method will force all generated chunks to be profiled before a dimension can be shutdown,
	// so long as saving and gen are on the same thread.
	public static void ensureSafeSave() {
		while (true) {
			try {
				semaphore.acquire();
			} catch (InterruptedException e) {
				throw new RuntimeException("Failed to acquire semaphore to add to chunk array (interrupted)!");
			}
			if (profilingqueue.isEmpty()) {
				safesaveenabled = true;
				return;
			}
			semaphore.release();
		}
	}

	public static void releaseSaveSafe() {
		if (!safesaveenabled) throw new RuntimeException("Attempted to release SafeSave while not in SafeSave.");
		safesaveenabled = false;
		semaphore.release();
	}
}
