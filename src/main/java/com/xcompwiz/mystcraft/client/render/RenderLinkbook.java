package com.xcompwiz.mystcraft.client.render;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.model.ModelBook;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.xcompwiz.mystcraft.Mystcraft;
import com.xcompwiz.mystcraft.data.Assets;
import com.xcompwiz.mystcraft.entity.EntityLinkbook;
import com.xcompwiz.mystcraft.item.ItemAgebook;

public class RenderLinkbook extends Render {
	private ModelBook	bookmodel;

	public RenderLinkbook() {
		bookmodel = new ModelBook();
	}

	@Override
	public void doRender(Entity entity, double d, double d1, double d2, float f, float f1) {
		EntityLinkbook linkbook = (EntityLinkbook) entity;
		if (linkbook.getItem() == ItemAgebook.instance) {
			this.renderManager.renderEngine.bindTexture(Assets.agebook_tex);
		} else {
			this.renderManager.renderEngine.bindTexture(Assets.linkbook_tex);
		}
		GL11.glPushMatrix();
		GL11.glTranslatef((float) d, (float) d1 + 0.0625F, (float) d2);
		GL11.glRotatef(f + 90, 0, -1, 0);
		GL11.glRotatef(90, 0, 0, 1);
		GL11.glScalef(0.8F, 0.8F, 0.8F);
		if (linkbook.hurtTime > 0) {
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_ALPHA_TEST);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glDepthFunc(GL11.GL_EQUAL);
			GL11.glColor4f(0.7f, 0.0F, 0.0F, 0.4F);

			bookmodel.render(null, 0.0f, 0.0f, 0.0f, 1.2f /* Open */, 0.0f, 0.0625F);

			GL11.glDepthFunc(GL11.GL_LEQUAL);
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glEnable(GL11.GL_ALPHA_TEST);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
		}
		bookmodel.render(null, 0.0f, 0.0f, 0.0f, 1.2f /* Open */, 0.0f, 0.0625F);
		GL11.glPopMatrix();
		if (Mystcraft.renderlabels && Mystcraft.serverLabels) renderLabel(entity, "" + linkbook.getAgeName(), d, d1, d2, 25);
	}

	protected void renderLabel(Entity entity, String s, double d, double d1, double d2, int i) {
		double f = entity.getDistanceSq(renderManager.livingPlayer.posX, renderManager.livingPlayer.posY, renderManager.livingPlayer.posZ);
		if (f > i) { return; }
		FontRenderer fontrenderer = getFontRendererFromRenderManager();
		float f1 = 1.6F;
		float f2 = 0.01666667F * f1;
		GL11.glPushMatrix();
		GL11.glTranslatef((float) d, (float) d1 + 0.3F, (float) d2);
		GL11.glNormal3f(0.0F, 1.0F, 0.0F);
		GL11.glRotatef(-renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
		GL11.glScalef(-f2, -f2, f2);
		GL11.glDisable(2896 /* GL_LIGHTING */);
		GL11.glDepthMask(false);
		GL11.glDisable(2929 /* GL_DEPTH_TEST */);
		GL11.glEnable(3042 /* GL_BLEND */);
		GL11.glBlendFunc(770, 771);
		Tessellator tessellator = Tessellator.instance;
		byte byte0 = 0;
		GL11.glDisable(3553 /* GL_TEXTURE_2D */);
		tessellator.startDrawingQuads();
		int j = fontrenderer.getStringWidth(s) / 2;
		tessellator.setColorRGBA_F(0.0F, 0.0F, 0.0F, 0.25F);
		tessellator.addVertex(-j - 1, -1 + byte0, 0.0D);
		tessellator.addVertex(-j - 1, 8 + byte0, 0.0D);
		tessellator.addVertex(j + 1, 8 + byte0, 0.0D);
		tessellator.addVertex(j + 1, -1 + byte0, 0.0D);
		tessellator.draw();
		GL11.glEnable(3553 /* GL_TEXTURE_2D */);
		fontrenderer.drawString(s, -fontrenderer.getStringWidth(s) / 2, byte0, 0x20ffffff);
		GL11.glEnable(2929 /* GL_DEPTH_TEST */);
		GL11.glDepthMask(true);
		fontrenderer.drawString(s, -fontrenderer.getStringWidth(s) / 2, byte0, -1);
		GL11.glEnable(2896 /* GL_LIGHTING */);
		GL11.glDisable(3042 /* GL_BLEND */);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glPopMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return Assets.linkbook_tex;
	}
}
