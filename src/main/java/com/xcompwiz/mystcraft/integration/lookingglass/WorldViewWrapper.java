package com.xcompwiz.mystcraft.integration.lookingglass;

import com.xcompwiz.lookingglass.api.animator.CameraAnimatorPivot;
import com.xcompwiz.lookingglass.api.view.IWorldView;

public class WorldViewWrapper implements IWorldViewWrapper {

	public final IWorldView view;
	
	public WorldViewWrapper(IWorldView createWorldView) {
		this.view = createWorldView;
	}

	@Override
	public void setAnimator() {
		view.setAnimator(new CameraAnimatorPivot(view.getCamera()));
	}

	@Override
	public int getTexture() {
		return view.getTexture();
	}

	@Override
	public boolean isReady() {
		return view.isReady();
	}

	@Override
	public void markDirty() {
		view.markDirty();
	}

}
