package com.xcompwiz.mystcraft.client.render;

import com.xcompwiz.mystcraft.tileentity.ITileEntityRotateable;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class RenderModel<T extends TileEntity> extends TileEntitySpecialRenderer<T> {

	private ModelBase			model;
	private ResourceLocation	texture;

	public RenderModel(ModelBase model, ResourceLocation texture) {
		this.model = model;
		this.texture = texture;
	}

	@Override
	public void render(T te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		x += 0.5;
		z += 0.5;
		this.bindTexture(texture);
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		GlStateManager.rotate(180, 1, 0, 0);
		if (te instanceof ITileEntityRotateable) {
			GlStateManager.rotate(((ITileEntityRotateable) te).getYaw(), 0, 1, 0);
		}
		GlStateManager.translate(0, -1.5F, 0);
		model.render(null, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0625F);
		GlStateManager.popMatrix();
	}

}
