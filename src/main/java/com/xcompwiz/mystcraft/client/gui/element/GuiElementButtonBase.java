package com.xcompwiz.mystcraft.client.gui.element;

import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.util.IIcon;

import com.xcompwiz.mystcraft.client.gui.GuiUtils;

public abstract class GuiElementButtonBase extends GuiElement {

	private IIcon			icon;
	private String			text;
	private List<String>	tooltip;

	private boolean			clicked;
	private boolean			hovered	= false;

	public GuiElementButtonBase(int guiLeft, int guiTop, int width, int height) {
		super(guiLeft, guiTop, width, height);
	}

	public void setIcon(IIcon icon) {
		this.icon = icon;
	}

	public void setText(String string) {
		this.text = string;
	}

	public void setTooltip(List<String> string) {
		this.tooltip = string;
	}

	@Override
	public void mouseUp(int i, int j, int k) {
		if (!isEnabled()) return;
		if (clicked && GuiUtils.contains(i, j, guiLeft, guiTop, xSize, ySize)) {
			this.onClick(i, j, k);
		}
		clicked = false;
	}

	protected abstract void onClick(int i, int j, int k);

	protected boolean isDepressed() {
		return clicked;
	}

	@Override
	public boolean mouseClicked(int i, int j, int k) {
		if (!isEnabled()) return false;
		if (GuiUtils.contains(i, j, guiLeft, guiTop, xSize, ySize)) {
			clicked = true;
			return true;
		}
		return false;
	}

	@Override
	public List<String> getTooltipInfo() {
		if (this.hovered) { return this.tooltip; }
		return super.getTooltipInfo();
	}

	@Override
	public void renderForeground(int mouseX, int mouseY) {}

	@Override
	public void render(float f, int mouseX, int mouseY) {
		hovered = GuiUtils.contains(mouseX, mouseY, guiLeft, guiTop, xSize, ySize);
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
			GuiUtils.drawSprite(this.guiLeft, this.guiTop, xSize, ySize, 0, 0, this.zLevel);
		} else {
			GuiUtils.drawSprite(this.guiLeft, this.guiTop, xSize, ySize, 0, 18, this.zLevel);
		}
		if (hovered) {
			GuiUtils.drawGradientRect(this.guiLeft, this.guiTop, this.guiLeft + xSize, this.guiTop + ySize, 0x90FFFFFF, 0x90FFFFFF, this.zLevel);
		}
		// Render button icon/text
		if (icon != null) {
			GuiUtils.drawIcon(guiLeft, guiTop, icon, xSize, ySize, zLevel);
		}
		if (text != null) {
			GuiUtils.drawScaledText(text, this.guiLeft + 2, this.guiTop + 2, this.xSize - 4, this.ySize - 4, fontcolor);
		}
	}
}
