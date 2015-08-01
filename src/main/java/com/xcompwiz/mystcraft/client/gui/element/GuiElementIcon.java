package com.xcompwiz.mystcraft.client.gui.element;

import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;

import com.xcompwiz.mystcraft.client.gui.GuiUtils;

public class GuiElementIcon extends GuiElement {

	private static RenderItem	itemrenderer	= new RenderItem();

	private ItemStack			itemstack;

	public GuiElementIcon(ItemStack itemstack, int guiLeft, int guiTop, int xSize, int ySize) {
		super(guiLeft, guiTop, xSize, ySize);
		this.itemstack = itemstack;
	}

	@Override
	protected void _renderBackground(float f, int mouseX, int mouseY) {
		if (this.itemstack == null) return;
		int slotX = getLeft();
		int slotY = getTop();
		float smallest = Math.min(xSize, ySize);
		float scale = smallest/16.F;

		GuiUtils.startGlScissor(slotX, slotY, this.xSize, this.ySize);

		GL11.glPushMatrix();
		GL11.glTranslatef(slotX, slotY, 1);
		GL11.glScalef(scale, scale, 1);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		itemrenderer.renderItemAndEffectIntoGUI(this.mc.fontRenderer, this.mc.getTextureManager(), this.itemstack, 0, 0);
		itemrenderer.renderItemOverlayIntoGUI(this.mc.fontRenderer, this.mc.getTextureManager(), itemstack, 0, 0, null);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glPopMatrix();

		GuiUtils.endGlScissor();
	}

	public void setItemstack(ItemStack itemstack) {
		this.itemstack = itemstack;
	}
}
