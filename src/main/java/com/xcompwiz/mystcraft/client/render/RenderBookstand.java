package com.xcompwiz.mystcraft.client.render;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.model.ModelBook;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

import com.xcompwiz.mystcraft.Mystcraft;
import com.xcompwiz.mystcraft.client.model.ModelBookstand;
import com.xcompwiz.mystcraft.data.Assets.Entities;
import com.xcompwiz.mystcraft.data.ModItems;
import com.xcompwiz.mystcraft.tileentity.TileEntityBookstand;

public class RenderBookstand extends TileEntitySpecialRenderer {

	private ModelBook		book;
	private ModelBookstand	stand;

	public RenderBookstand() {
		stand = new ModelBookstand();
		book = new ModelBook();
	}

	public void render(TileEntityBookstand tileentity, double d, double d1, double d2, float f) {
		d += 0.5;
		d2 += 0.5;
		bindTexture(Entities.bookstand);
		GL11.glPushMatrix();
		GL11.glTranslatef((float) d, (float) d1 + 0.5F, (float) d2);
		GL11.glRotatef(180, 0, 0, 1);
		GL11.glRotatef(180 + tileentity.getYaw(), 0, 1, 0);
		stand.render(null, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0625F);
		GL11.glPopMatrix();
		if (tileentity.getDisplayItem() == null) return;
		if (tileentity.getDisplayItem().getItem() == ModItems.agebook) {
			bindTexture(Entities.agebook);
		} else if (tileentity.getDisplayItem().getItem() == ModItems.linkbook) {
			bindTexture(Entities.linkbook);
		} else {
			return;
		}
		GL11.glPushMatrix();
		GL11.glTranslatef((float) d, (float) d1 + 0.55F, (float) d2);
		GL11.glRotatef(270 + tileentity.getYaw(), 0, -1, 0);
		GL11.glRotatef(120F, 0, 0, 1);
		GL11.glScalef(0.8F, 0.8F, 0.8F);
		book.render(null, 0.0f, 0.0f, 0.0f, 1.05f /* Open */, 0.0f, 0.0625F);
		GL11.glPopMatrix();

		if (Mystcraft.renderlabels && Mystcraft.serverLabels) renderLabel(tileentity, tileentity.getBookTitle(), d, d1 + 1.25F, d2, 25);
	}

	protected void renderLabel(TileEntity entity, String s, double d, double d1, double d2, int i) {
		if (s == null) return;
		double f = entity.getDistanceFrom(field_147501_a.field_147560_j, field_147501_a.field_147561_k, field_147501_a.field_147558_l);
		if (f > i) { return; }
		FontRenderer fontrenderer = field_147501_a.getFontRenderer();
		float f1 = 1.6F;
		float f2 = 0.01666667F * f1;
		GL11.glPushMatrix();
		GL11.glTranslatef((float) d, (float) d1, (float) d2);
		GL11.glNormal3f(0.0F, 1.0F, 0.0F);
		GL11.glRotatef(-field_147501_a.field_147562_h, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(field_147501_a.field_147563_i, 1.0F, 0.0F, 0.0F);
		GL11.glScalef(-f2, -f2, f2);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDepthMask(false);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(770, 771);
		Tessellator tessellator = Tessellator.instance;
		byte byte0 = 0;
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		tessellator.startDrawingQuads();
		int j = fontrenderer.getStringWidth(s) / 2;
		tessellator.setColorRGBA_F(0.0F, 0.0F, 0.0F, 0.25F);
		tessellator.addVertex(-j - 1, -1 + byte0, 0.0D);
		tessellator.addVertex(-j - 1, 8 + byte0, 0.0D);
		tessellator.addVertex(j + 1, 8 + byte0, 0.0D);
		tessellator.addVertex(j + 1, -1 + byte0, 0.0D);
		tessellator.draw();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		fontrenderer.drawString(s, -fontrenderer.getStringWidth(s) / 2, byte0, 0x20ffffff);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(true);
		fontrenderer.drawString(s, -fontrenderer.getStringWidth(s) / 2, byte0, -1);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glPopMatrix();
	}

	@Override
	public void renderTileEntityAt(TileEntity tileentity, double d, double d1, double d2, float f) {
		render((TileEntityBookstand) tileentity, d, d1, d2, f);
	}
}
