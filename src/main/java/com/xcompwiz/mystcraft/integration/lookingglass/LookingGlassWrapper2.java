package com.xcompwiz.mystcraft.integration.lookingglass;

import com.xcompwiz.lookingglass.api.hook.WorldViewAPI2;
import com.xcompwiz.lookingglass.api.view.IWorldView;

import net.minecraft.util.ChunkCoordinates;

public class LookingGlassWrapper2 implements ILookingGlassWrapper {
	private final WorldViewAPI2	apiinst;

	public LookingGlassWrapper2(WorldViewAPI2 apiinst) {
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
