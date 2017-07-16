package com.xcompwiz.mystcraft.client.model;

import net.minecraft.client.renderer.BufferBuilder;

public abstract class ModelElement {

	//BufferBuilder passed in has to be the BufferBuilder from Tessellator.getInstance
	public abstract void render(BufferBuilder vb, float partialTicks);

}
