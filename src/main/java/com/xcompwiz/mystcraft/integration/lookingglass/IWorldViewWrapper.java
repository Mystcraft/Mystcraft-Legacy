package com.xcompwiz.mystcraft.integration.lookingglass;

public interface IWorldViewWrapper {

	void setAnimator();

	int getTexture();

	boolean isReady();

	void markDirty();

}
