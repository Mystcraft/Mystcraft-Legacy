package com.xcompwiz.mystcraft.client.gui.element;

import java.awt.Color;
import java.util.List;

import com.xcompwiz.mystcraft.client.gui.GuiUtils;
import com.xcompwiz.mystcraft.client.gui.element.data.IGuiIcon;

import net.minecraft.client.renderer.GlStateManager;

public abstract class GuiElementButtonBase extends GuiElement {

	private IGuiIcon icon;
	private String text;
	private List<String> tooltip;

	private boolean clicked;
	private boolean hovered = false;

	public Color color = Color.WHITE;

	public GuiElementButtonBase(int guiLeft, int guiTop, int width, int height) {
		super(guiLeft, guiTop, width, height);
	}

	public void setIcon(IGuiIcon icon) {
		this.icon = icon;
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
		if (this.hovered) {
			return this.tooltip;
		}
		return super._getTooltipInfo();
	}

	@Override
	public void _renderBackground(float f, int mouseX, int mouseY) {
		hovered = this.contains(mouseX, mouseY);
		int guiLeft = getLeft();
		int guiTop = getTop();
		int fontcolor;
		float colorFactor = 1.0f;
		if (!isEnabled()) {
			fontcolor = 0xFF333333;
			colorFactor = 0.5f;
		} else {
			fontcolor = 0xFF000000;
		}
		GlStateManager.color(colorFactor * color.getRed(), colorFactor * color.getBlue(), colorFactor * color.getGreen(), color.getAlpha());
		// Render button
		if (xSize != ySize) {
			int color1 = 0xFF373737;
			int color2 = 0xFFFFFFFF;
			if (isDepressed()) {
				int temp = color1;
				color1 = color2;
				color2 = temp;
			}
			int innercolor = 0xFF8b8b8b;
			drawRect(guiLeft, guiTop, guiLeft + this.xSize, guiTop + ySize, innercolor);
			drawRect(guiLeft, guiTop, guiLeft + this.xSize - 1, guiTop + ySize - 1, color2);
			drawRect(guiLeft + 1, guiTop + 1, guiLeft + this.xSize, guiTop + ySize, color1);
			drawRect(guiLeft + 1, guiTop + 1, guiLeft + this.xSize - 1, guiTop + ySize - 1, innercolor);
		} else {
			if (isDepressed()) {
				GuiUtils.drawSprite(guiLeft, guiTop, xSize, ySize, 0, 0, this.getZLevel());
			} else {
				GuiUtils.drawSprite(guiLeft, guiTop, xSize, ySize, 0, 18, this.getZLevel());
			}
		}
		if (hovered) {
			GuiUtils.drawGradientRect(guiLeft, guiTop, guiLeft + xSize, guiTop + ySize, 0x90FFFFFF, 0x90FFFFFF, this.getZLevel());
		}
		// Render button icon/text
		if (icon != null) {
			icon.render(this.mc, guiLeft, guiTop, xSize, ySize, getZLevel());
		}
		if (text != null) {
			GlStateManager.disableDepth();
			GuiUtils.drawScaledText(text, guiLeft + 2, guiTop + 2, this.xSize - 4, this.ySize - 4, fontcolor);
			GlStateManager.enableDepth();
		}
	}
}
