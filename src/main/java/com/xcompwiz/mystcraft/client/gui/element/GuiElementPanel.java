package com.xcompwiz.mystcraft.client.gui.element;

import net.minecraft.client.renderer.Tessellator;

import org.lwjgl.opengl.GL11;

import com.xcompwiz.mystcraft.client.gui.GuiUtils;

public class GuiElementPanel extends GuiElement {

	private int	bgcolor1	= 0x00000000;
	private int	bgcolor2	= 0x00000000;

	public GuiElementPanel(int guiLeft, int guiTop, int xSize, int ySize) {
		super(guiLeft, guiTop, xSize, ySize);
	}

	public void setBackground(int color1, int color2) {
		this.bgcolor1 = color1;
		this.bgcolor2 = color2;
	}

	@Override
	public float getZLevel() {
		return super.getZLevel() + 0.1F;
	}

	@Override
	public void _renderBackground(float f, int mouseX, int mouseY) {
		int guiLeft = this.getLeft();
		int guiTop = this.getTop();
		GL11.glPushMatrix();
		GuiUtils.startGlScissor(guiLeft, guiTop, this.xSize, ySize);
		if ((bgcolor1 | bgcolor2) == 0) return;
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glShadeModel(GL11.GL_SMOOTH);
		this.drawGradientRect(guiLeft, guiTop, guiLeft + this.xSize, guiTop + ySize, bgcolor1, bgcolor2, this.getZLevel());
		GL11.glShadeModel(GL11.GL_FLAT);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
	}

	@Override
	protected void _renderBackgroundPost(float f, int mouseX, int mouseY) {
		GuiUtils.endGlScissor();
		GL11.glPopMatrix();
	}

	/**
	 * Draws a rectangle with a vertical gradient between the specified colors.
	 */
	//XXX: Move to GuiUtils
	public void drawGradientRect(float x1, float y1, float x2, float y2, int color1, int color2, float zLevel) {
		float a1 = (color1 >> 24 & 255) / 255.0F;
		float r1 = (color1 >> 16 & 255) / 255.0F;
		float g1 = (color1 >> 8 & 255) / 255.0F;
		float b1 = (color1 & 255) / 255.0F;
		float a2 = (color2 >> 24 & 255) / 255.0F;
		float r2 = (color2 >> 16 & 255) / 255.0F;
		float g2 = (color2 >> 8 & 255) / 255.0F;
		float b2 = (color2 & 255) / 255.0F;
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		Tessellator var15 = Tessellator.instance;
		var15.startDrawingQuads();
		var15.setColorRGBA_F(r1, g1, b1, a1);
		var15.addVertex(x2, y1, zLevel);
		var15.addVertex(x1, y1, zLevel);
		var15.setColorRGBA_F(r2, g2, b2, a2);
		var15.addVertex(x1, y2, zLevel);
		var15.addVertex(x2, y2, zLevel);
		var15.draw();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}
}
