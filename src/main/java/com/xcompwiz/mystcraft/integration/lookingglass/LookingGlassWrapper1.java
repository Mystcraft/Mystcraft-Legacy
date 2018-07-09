package com.xcompwiz.mystcraft.integration.lookingglass;

import com.xcompwiz.lookingglass.api.IWorldViewAPI;

import net.minecraft.util.math.BlockPos;

@SuppressWarnings("deprecation")
public class LookingGlassWrapper1 implements ILookingGlassWrapper {
	private final IWorldViewAPI apiinst;

	public LookingGlassWrapper1(IWorldViewAPI apiinst) {
		this.apiinst = apiinst;
	}

	@Override
	public IWorldViewWrapper createWorldView(Integer dimid, BlockPos spawn, int i, int j) {
		return new WorldViewWrapper(apiinst.createWorldView(dimid, spawn, 132, 83));
	}

	@Override
	public void release(IWorldViewWrapper activeview) {
		((WorldViewWrapper) activeview).view.release();
	}
}
