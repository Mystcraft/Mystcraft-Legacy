package com.xcompwiz.mystcraft.integration.lookingglass;

import net.minecraft.util.ChunkCoordinates;

import com.xcompwiz.lookingglass.api.view.IWorldView;
import com.xcompwiz.lookingglass.api.view.IWorldViewAPI2;

public class LookingGlassWrapper2 implements ILookingGlassWrapper {
	private final IWorldViewAPI2	apiinst;

	public LookingGlassWrapper2(IWorldViewAPI2 apiinst) {
		this.apiinst = apiinst;
	}

	@Override
	public IWorldView createWorldView(Integer dimid, ChunkCoordinates spawn, int i, int j) {
		return apiinst.createWorldView(dimid, spawn, 132, 83);
	}

	@Override
	public void release(Object activeview) {
		apiinst.cleanupWorldView((IWorldView)activeview);
	}
}
