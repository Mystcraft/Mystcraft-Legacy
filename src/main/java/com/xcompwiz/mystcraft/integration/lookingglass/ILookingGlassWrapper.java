package com.xcompwiz.mystcraft.integration.lookingglass;

import com.xcompwiz.lookingglass.api.view.IWorldView;

import net.minecraft.util.math.BlockPos;

public interface ILookingGlassWrapper {

	IWorldView createWorldView(Integer dimid, BlockPos spawn, int i, int j);

	void release(Object activeview);

}
