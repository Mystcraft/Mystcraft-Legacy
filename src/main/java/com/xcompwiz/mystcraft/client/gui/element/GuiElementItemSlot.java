package com.xcompwiz.mystcraft.client.gui.element;

import java.util.List;

import net.minecraft.util.IIcon;

import com.xcompwiz.mystcraft.client.gui.GuiUtils;

public abstract class GuiElementItemSlot extends GuiElement {

	private IIcon			icon;
	private List<String>	tooltip;

	private boolean			hovered	= false;

	public GuiElementItemSlot(int guiLeft, int guiTop, int width, int height) {
		super(guiLeft, guiTop, width, height);
	}

	public void setIcon(IIcon icon) {
		this.icon = icon;
	}

	public void setTooltip(List<String> string) {
		this.tooltip = string;
	}

	@Override
	public void mouseUp(int i, int j, int k) {
	}

	@Override
	public boolean mouseClicked(int i, int j, int k) {
		if (GuiUtils.contains(i, j, guiLeft, guiTop, xSize, ySize)) {
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
	public void render(float f, int mouseX, int mouseY) {
		hovered = GuiUtils.contains(mouseX, mouseY, guiLeft, guiTop, xSize, ySize);
		// Render button
		GuiUtils.drawSprite(this.guiLeft, this.guiTop, xSize, ySize, 0, 0, this.zLevel);
		if (hovered) {
			GuiUtils.drawGradientRect(this.guiLeft, this.guiTop, this.guiLeft + xSize, this.guiTop + ySize, 0x90FFFFFF, 0x90FFFFFF, this.zLevel);
		}
		// Render button icon/text
		if (icon != null) {
			GuiUtils.drawIcon(guiLeft, guiTop, icon, xSize, ySize, zLevel);
		}
	}

	@Override
	public void renderForeground(int mouseX, int mouseY) {}

}
