package com.xcompwiz.mystcraft.client.gui.element;

import java.util.List;

import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import org.lwjgl.opengl.GL11;

import com.xcompwiz.mystcraft.client.gui.GuiUtils;

public abstract class GuiElementButtonBase extends GuiElement {

	private static RenderItem	itemrenderer	= new RenderItem();

	private IIcon			icon;
	private ItemStack		itemstack;
	private String			text;
	private List<String>	tooltip;

	private boolean			clicked;
	private boolean			hovered	= false;

	public GuiElementButtonBase(int guiLeft, int guiTop, int width, int height) {
		super(guiLeft, guiTop, width, height);
	}

	public void setIcon(IIcon icon) {
		this.icon = icon;
		this.itemstack = null;
	}

	public void setIcon(ItemStack itemstack) {
		this.icon = null;
		this.itemstack = itemstack;
	}

	public void setText(String string) {
		this.text = string;
	}

	public void setTooltip(List<String> string) {
		this.tooltip = string;
	}

	@Override
	public boolean _onMouseUp(int mouseX, int mouseY, int button) {
		if (clicked && this.contains(mouseX, mouseY)) {
			this.onClick(mouseX, mouseY, button);
			clicked = false;
			return true;
		}
		//XXX: We may not get here, if something else eats the event
		clicked = false;
		return false;
	}

	protected abstract void onClick(int i, int j, int k);

	protected boolean isDepressed() {
		return clicked;
	}

	@Override
	public boolean _onMouseDown(int mouseX, int mouseY, int button) {
		if (this.contains(mouseX, mouseY)) {
			clicked = true;
			return true;
		}
		return false;
	}

	@Override
	public List<String> _getTooltipInfo() {
		if (this.hovered) { return this.tooltip; }
		return super._getTooltipInfo();
	}

	@Override
	public void _renderBackground(float f, int mouseX, int mouseY) {
		hovered = this.contains(mouseX, mouseY);
		int guiLeft = getLeft();
		int guiTop = getTop();
		int fontcolor;
		if (!isEnabled()) {
			fontcolor = 0xFF333333;
			GL11.glColor4f(0.5F, 0.5F, 0.5F, 1.0F);
		} else {
			fontcolor = 0xFF000000;
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		}
		// Render button
		if (isDepressed()) {
			GuiUtils.drawSprite(guiLeft, guiTop, xSize, ySize, 0, 0, this.getZLevel());
		} else {
			GuiUtils.drawSprite(guiLeft, guiTop, xSize, ySize, 0, 18, this.getZLevel());
		}
		if (hovered) {
			GuiUtils.drawGradientRect(guiLeft, guiTop, guiLeft + xSize, guiTop + ySize, 0x90FFFFFF, 0x90FFFFFF, this.getZLevel());
		}
		// Render button icon/text
		if (icon != null) {
			GuiUtils.drawIcon(guiLeft, guiTop, icon, xSize, ySize, getZLevel());
		}
		if (itemstack != null) {
			//TODO: Make this a helper function (see also GuiElementIcon)
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
		if (text != null) {
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			GuiUtils.drawScaledText(text, guiLeft + 2, guiTop + 2, this.xSize - 4, this.ySize - 4, fontcolor);
			GL11.glEnable(GL11.GL_DEPTH_TEST);
		}
	}
}
