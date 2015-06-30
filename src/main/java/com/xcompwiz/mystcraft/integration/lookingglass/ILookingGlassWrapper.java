package com.xcompwiz.mystcraft.integration.lookingglass;

import net.minecraft.util.ChunkCoordinates;

import com.xcompwiz.lookingglass.api.view.IWorldView;

public interface ILookingGlassWrapper {

	IWorldView createWorldView(Integer dimid, ChunkCoordinates spawn, int i, int j);

	void release(Object activeview);

}
