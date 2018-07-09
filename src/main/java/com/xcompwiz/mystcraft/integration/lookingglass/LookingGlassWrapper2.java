package com.xcompwiz.mystcraft.integration.lookingglass;

import com.xcompwiz.lookingglass.api.hook.WorldViewAPI2;

import net.minecraft.util.math.BlockPos;

public class LookingGlassWrapper2 implements ILookingGlassWrapper {
	private final WorldViewAPI2 apiinst;

	public LookingGlassWrapper2(WorldViewAPI2 apiinst) {
		this.apiinst = apiinst;
	}

	@Override
	public IWorldViewWrapper createWorldView(Integer dimid, BlockPos spawn, int i, int j) {
		return new WorldViewWrapper(apiinst.createWorldView(dimid, spawn, 132, 83));
	}

	@Override
	public void release(IWorldViewWrapper activeview) {
		apiinst.cleanupWorldView(((WorldViewWrapper)activeview).view);
	}
}
