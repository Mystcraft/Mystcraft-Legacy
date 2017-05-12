package com.xcompwiz.mystcraft.client.model;

import net.minecraft.client.renderer.VertexBuffer;

public abstract class ModelElement {

	//VertexBuffer passed in has to be the VertexBuffer from Tessellator.getInstance
	public abstract void render(VertexBuffer vb, float partialTicks);

}
