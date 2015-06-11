package com.xcompwiz.mystcraft.world.profiling;

public interface IMystcraftProfilingCallback {

	public void setCompleted(int count);

	public void setRemaining(int chunksremaining);

	public void setQueued(int size);

	public void onFinished();

}
