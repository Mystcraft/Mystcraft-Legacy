package com.xcompwiz.mystcraft.client.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.xcompwiz.mystcraft.client.model.ModelMeteor;
import com.xcompwiz.mystcraft.data.Assets.Vanilla;
import com.xcompwiz.mystcraft.entity.EntityMeteor;

public class RenderMeteor extends Render {
	private ModelBase	model;

	public RenderMeteor() {
		model = new ModelMeteor();
	}

	@Override
	public void doRender(Entity entity, double d, double d1, double d2, float f, float f1) {
		EntityMeteor meteor = (EntityMeteor) entity;
		GL11.glPushMatrix();
		GL11.glTranslatef((float) d, (float) d1, (float) d2);
		GL11.glScalef(meteor.getScale()/10F, meteor.getScale()/10F, meteor.getScale()/10F);
		GL11.glRotatef(entity.rotationYaw, 0, -1, 0);
		GL11.glRotatef(entity.rotationPitch, 0, 0, 1);
		this.renderManager.renderEngine.bindTexture(Vanilla.end_portal);
		model.render(null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 1F);
		GL11.glPopMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return Vanilla.end_portal;
	}
}
