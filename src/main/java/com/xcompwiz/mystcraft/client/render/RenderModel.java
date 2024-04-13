package com.xcompwiz.mystcraft.client.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.xcompwiz.mystcraft.tileentity.ITileEntityRotateable;

public class RenderModel extends TileEntitySpecialRenderer {

	private ModelBase			model;
	private ResourceLocation	texture;

	public RenderModel(ModelBase model, ResourceLocation texture) {
		this.model = model;
		this.texture = texture;
	}

	public void render(TileEntity tileentity, double d, double d1, double d2, float f) {
		d += 0.5;
		d2 += 0.5;
		this.bindTexture(texture);
		GL11.glPushMatrix();
		GL11.glTranslatef((float) d, (float) d1, (float) d2);
		GL11.glRotatef(180, 1, 0, 0);
		if (tileentity instanceof ITileEntityRotateable) {
			GL11.glRotatef(((ITileEntityRotateable) tileentity).getYaw(), 0, 1, 0);
		}
		GL11.glTranslatef(0, -1.5F, 0);
		//GL11.glRotatef(90 - tileentity.getYaw(), 0, 1, 0);
		model.render(null, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0625F);
		GL11.glPopMatrix();
	}

	@Override
	public void renderTileEntityAt(TileEntity tileentity, double d, double d1, double d2, float f) {
		render(tileentity, d, d1, d2, f);
	}
}
