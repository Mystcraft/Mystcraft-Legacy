package com.xcompwiz.mystcraft.integration.lookingglass;

import com.xcompwiz.lookingglass.api.view.IWorldView;

import net.minecraft.util.ChunkCoordinates;

public interface ILookingGlassWrapper {

	IWorldView createWorldView(Integer dimid, ChunkCoordinates spawn, int i, int j);

	void release(Object activeview);

}
