package com.xcompwiz.mystcraft.client.gui.element;

import java.util.List;

import com.xcompwiz.mystcraft.client.gui.GuiUtils;

public class GuiElementLabel extends GuiElement {

	public interface IGuiLabelDataProvider {

		String getText(GuiElementLabel caller);

		List<String> getTooltip(GuiElementLabel caller);

	}

	//TODO: Alignment options

	private IGuiLabelDataProvider provider;
	private String id;
	private int bkgdcolor;
	private int textcolor;

	private boolean hovered = false;

	public GuiElementLabel(IGuiLabelDataProvider provider, String id, int guiLeft, int guiTop, int width, int height, int bkgdcolor, int textcolor) {
		super(guiLeft, guiTop, width, height);
		this.provider = provider;
		this.id = id;
		this.bkgdcolor = bkgdcolor;
		this.textcolor = textcolor;
	}

	public String getId() {
		return id;
	}

	@Override
	public List<String> _getTooltipInfo() {
		if (this.hovered) {
			return provider.getTooltip(this);
		}
		return super._getTooltipInfo();
	}

	@Override
	public void _renderBackground(float f, int mouseX, int mouseY) {
		hovered = this.contains(mouseX, mouseY);
		int guiLeft = getLeft();
		int guiTop = getTop();
		drawRect(guiLeft, guiTop, guiLeft + this.xSize, guiTop + ySize, bkgdcolor);
		GuiUtils.drawScaledText(provider.getText(this), guiLeft + 2, guiTop + 2, this.xSize - 4, this.ySize - 4, textcolor);
	}
}
