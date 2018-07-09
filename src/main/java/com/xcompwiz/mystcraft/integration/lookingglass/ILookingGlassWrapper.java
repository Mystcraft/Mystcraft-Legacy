package com.xcompwiz.mystcraft.integration.lookingglass;

import net.minecraft.util.math.BlockPos;

public interface ILookingGlassWrapper {

	IWorldViewWrapper createWorldView(Integer dimid, BlockPos spawn, int i, int j);

	void release(IWorldViewWrapper activeview);

}
