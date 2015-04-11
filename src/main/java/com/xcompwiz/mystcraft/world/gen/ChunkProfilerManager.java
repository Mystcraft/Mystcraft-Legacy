package com.xcompwiz.mystcraft.world.gen;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;

import net.minecraft.command.ICommandSender;
import net.minecraft.world.chunk.Chunk;

import com.xcompwiz.mystcraft.debug.DebugUtils;
import com.xcompwiz.mystcraft.debug.DefaultValueCallback;
import com.xcompwiz.mystcraft.world.ChunkProfiler;

public class ChunkProfilerManager extends Thread {

	public static class ChunkProfileTask {

		private ChunkProfiler	profiler;
		private Chunk			chunk;

		public ChunkProfileTask(ChunkProfiler profiler, Chunk chunk) {
			this.profiler = profiler;
			this.chunk = chunk;
		}

		public void run() {
			//TODO: We need to handle the case of a chunk being marked to be profiled, but the age getting closed first.
			profiler.profile(chunk);
		}

	}

	private static List<ChunkProfileTask>	profilingqueue	= new LinkedList<ChunkProfileTask>();
	private static Semaphore				semaphore		= new Semaphore(1, true);

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
			throw new RuntimeException("Failed to aquire semaphore to add to chunk array (interrupted)!");
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
				throw new RuntimeException("Failed to acquire semaphore to swap chunk array (interrupted)!");
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
			throw new RuntimeException("Failed to acquire semaphore to add to chunk array (interrupted)!");
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
}
