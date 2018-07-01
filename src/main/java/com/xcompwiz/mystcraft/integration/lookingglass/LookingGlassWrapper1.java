package com.xcompwiz.mystcraft.integration.lookingglass;

import com.xcompwiz.lookingglass.api.IWorldViewAPI;
import com.xcompwiz.lookingglass.api.view.IWorldView;

import net.minecraft.util.math.BlockPos;

@SuppressWarnings("deprecation")
public class LookingGlassWrapper1 implements ILookingGlassWrapper {
	private final IWorldViewAPI apiinst;

	public LookingGlassWrapper1(IWorldViewAPI apiinst) {
		this.apiinst = apiinst;
	}

	@Override
	public IWorldView createWorldView(Integer dimid, BlockPos spawn, int i, int j) {
		return apiinst.createWorldView(dimid, spawn, 132, 83);
	}

	@Override
	public void release(Object activeview) {
		((IWorldView) activeview).release();
	}
}
