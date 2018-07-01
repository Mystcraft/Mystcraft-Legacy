package com.xcompwiz.mystcraft.client.gui.element;

import com.xcompwiz.mystcraft.client.gui.element.data.IGuiIcon;

public class GuiElementIcon extends GuiElement {

	private IGuiIcon icon;

	public GuiElementIcon(IGuiIcon icon, int guiLeft, int guiTop, int xSize, int ySize) {
		super(guiLeft, guiTop, xSize, ySize);
		this.icon = icon;
	}

	@Override
	protected void _renderBackground(float f, int mouseX, int mouseY) {
		if (this.icon == null) {
			return;
		}
		this.icon.render(mc, getLeft(), getTop(), xSize, ySize, getZLevel());
	}
}
