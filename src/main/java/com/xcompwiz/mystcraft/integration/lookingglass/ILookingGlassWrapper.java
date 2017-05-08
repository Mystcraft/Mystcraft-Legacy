package com.xcompwiz.mystcraft.integration.lookingglass;

import com.xcompwiz.lookingglass.api.view.IWorldView;

import net.minecraft.util.math.ChunkPos;

public interface ILookingGlassWrapper {

	IWorldView createWorldView(Integer dimid, ChunkPos spawn, int i, int j);

	void release(Object activeview);

}
