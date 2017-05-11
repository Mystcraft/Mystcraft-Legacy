package com.xcompwiz.mystcraft.client.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.fml.client.registry.IRenderFactory;

import com.xcompwiz.mystcraft.client.model.ModelMeteor;
import com.xcompwiz.mystcraft.data.Assets.Vanilla;
import com.xcompwiz.mystcraft.entity.EntityMeteor;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.util.ResourceLocation;

public class RenderMeteor extends Render<EntityMeteor> {

	private ModelBase model;

	public RenderMeteor(RenderManager renderManager) {
		super(renderManager);
		model = new ModelMeteor();
	}

	@Override
	public void doRender(EntityMeteor meteor, double x, double y, double z, float entityYaw, float partialTicks) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		GlStateManager.scale(meteor.getScale() / 10F, meteor.getScale() / 10F, meteor.getScale() / 10F);
		GlStateManager.rotate(meteor.rotationYaw, 0, -1, 0);
		GlStateManager.rotate(meteor.rotationPitch, 0, 0, 1);
		GlStateManager.enableTexture2D();
		renderManager.renderEngine.bindTexture(Vanilla.end_portal);
		model.render(null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 1F);
		GlStateManager.popMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityMeteor entity) {
		return Vanilla.end_portal;
	}

	public static class Factory implements IRenderFactory<EntityMeteor> {

		@Override
		public Render<? super EntityMeteor> createRenderFor(RenderManager manager) {
			return new RenderMeteor(manager);
		}

	}

}
