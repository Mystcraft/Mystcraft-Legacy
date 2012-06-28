package com.xcompwiz.mystcraft.client.gui.element;

import java.util.List;

import com.xcompwiz.mystcraft.client.gui.GuiUtils;

public class GuiElementLabel extends GuiElement {

	public interface IGuiLabelDataProvider {
		String getText();
		List<String> getTooltip();
	}

	private IGuiLabelDataProvider	provider;
	private int						bkgdcolor;
	private int						textcolor;

	private boolean					hovered	= false;

	public GuiElementLabel(IGuiLabelDataProvider provider, int guiLeft, int guiTop, int width, int height, int bkgdcolor, int textcolor) {
		super(guiLeft, guiTop, width, height);
		this.provider = provider;
		this.bkgdcolor = bkgdcolor;
		this.textcolor = textcolor;
	}

	@Override
	public List<String> getTooltipInfo() {
		if (this.hovered) { return provider.getTooltip(); }
		return super.getTooltipInfo();
	}

	@Override
	public void render(float f, int mouseX, int mouseY) {
		hovered = GuiUtils.contains(mouseX, mouseY, guiLeft, guiTop, xSize, ySize);
		drawRect(this.guiLeft, this.guiTop, this.guiLeft + this.xSize, this.guiTop + ySize, bkgdcolor);
		GuiUtils.drawScaledText(provider.getText(), this.guiLeft + 2, this.guiTop + 2, this.xSize - 4, this.ySize - 4, textcolor);
	}
}
