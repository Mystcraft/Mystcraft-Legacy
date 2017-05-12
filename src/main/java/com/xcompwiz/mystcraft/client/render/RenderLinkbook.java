package com.xcompwiz.mystcraft.client.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import org.lwjgl.opengl.GL11;

import com.xcompwiz.mystcraft.Mystcraft;
import com.xcompwiz.mystcraft.data.Assets.Entities;
import com.xcompwiz.mystcraft.data.ModItems;
import com.xcompwiz.mystcraft.entity.EntityLinkbook;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.model.ModelBook;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class RenderLinkbook extends Render<EntityLinkbook> {

	private ModelBook	bookmodel;

	public RenderLinkbook(RenderManager rm) {
		super(rm);
		bookmodel = new ModelBook();
	}

	@Override
	public void doRender(EntityLinkbook entity, double x, double y, double z, float entityYaw, float partialTicks) {
		ItemStack book = entity.getBook();
		if(book.getItem() == ModItems.agebook) {
			this.renderManager.renderEngine.bindTexture(Entities.agebook);
		} else {
			this.renderManager.renderEngine.bindTexture(Entities.linkbook);
		}
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y + 0.0625, z);
		GlStateManager.rotate(entityYaw + 90, 0, -1, 0);
		GlStateManager.rotate(90, 0, 0, 1);
		GlStateManager.scale(0.8, 0.8, 0.8);
		if(entity.hurtTime > 0) {
			GlStateManager.disableTexture2D();
			GlStateManager.disableAlpha();
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			GlStateManager.depthFunc(GL11.GL_LEQUAL);
			GlStateManager.color(0.7F, 0, 0, 0.4F);

			bookmodel.render(null, 0.0f, 0.0f, 0.0f, 1.2f /* Open */, 0.0f, 0.0625F);

			GlStateManager.depthFunc(GL11.GL_LEQUAL);
			GlStateManager.disableBlend();
			GlStateManager.enableAlpha();
			GlStateManager.enableTexture2D();
		}
		bookmodel.render(null, 0.0f, 0.0f, 0.0f, 1.2f /* Open */, 0.0f, 0.0625F);
		GlStateManager.popMatrix();
		if (Mystcraft.renderlabels && Mystcraft.serverLabels) {
			renderLabel(entity, "" + entity.getAgeName(), x, y, z, 25);
		}
	}

	private void renderLabel(Entity entity, String s, double x, double y, double z, int i) {
		double f = entity.getDistanceSq(renderManager.renderViewEntity.posX, renderManager.renderViewEntity.posY, renderManager.renderViewEntity.posZ);
		f = MathHelper.sqrt(f);
		if (f > i) {
			return;
		}
		FontRenderer fontrenderer = getFontRendererFromRenderManager();
		float f1 = 1.6F;
		float f2 = 0.01666667F * f1;

		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		GlStateManager.glNormal3f(0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(-renderManager.playerViewX, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(renderManager.playerViewY, 1.0F, 0.0F, 0.0F);
		GlStateManager.scale(-f2, -f2, f2);
		GlStateManager.disableLighting();
		GlStateManager.depthMask(false);
		GlStateManager.disableDepth();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.disableTexture2D();
		Tessellator tes = Tessellator.getInstance();
		VertexBuffer vb = tes.getBuffer();
		byte byte0 = 0;
		int j = fontrenderer.getStringWidth(s) / 2;
		vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
		vb.pos(-j - 1, -1 + byte0, 0.0D).color(0, 0, 0, 0.25F).endVertex();
		vb.pos(-j - 1,  8 + byte0, 0.0D).color(0, 0, 0, 0.25F).endVertex();
		vb.pos( j + 1,  8 + byte0, 0.0D).color(0, 0, 0, 0.25F).endVertex();
		vb.pos( j + 1, -1 + byte0, 0.0D).color(0, 0, 0, 0.25F).endVertex();
		tes.draw();
		GlStateManager.enableTexture2D();
		fontrenderer.drawString(s, -fontrenderer.getStringWidth(s) / 2, byte0, 0x20ffffff);
		GlStateManager.enableDepth();
		GlStateManager.depthMask(true);
		fontrenderer.drawString(s, -fontrenderer.getStringWidth(s) / 2, byte0, -1);
		GlStateManager.enableLighting();
		GlStateManager.disableBlend();
		GlStateManager.color(1F, 1F, 1F, 1F);
		GlStateManager.popMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityLinkbook entity) {
		return Entities.linkbook;
	}


	public static class Factory implements IRenderFactory<EntityLinkbook> {

		@Override
		public Render<EntityLinkbook> createRenderFor(RenderManager manager) {
			return new RenderLinkbook(manager);
		}

	}

}
