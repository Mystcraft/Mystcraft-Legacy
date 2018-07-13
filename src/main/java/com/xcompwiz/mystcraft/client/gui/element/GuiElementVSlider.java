package com.xcompwiz.mystcraft.client.gui.element;

import org.lwjgl.input.Mouse;

import com.xcompwiz.mystcraft.data.Assets;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;

public class GuiElementVSlider extends GuiElement {
	private int currentScroll;
	private int maxScroll;
	private boolean wasClicking;
	private boolean isScrolling;
	private boolean mouseOver;

	public GuiElementVSlider(int guiLeft, int guiTop, int xSize, int ySize) {
		super(guiLeft, guiTop, xSize, ySize);
	}

	public void setMaxScroll(int maxScroll) {
		this.maxScroll = maxScroll;
		if (currentScroll > maxScroll) {
			currentScroll = maxScroll;
		}
	}

	public int getCurrentPos() {
		return currentScroll;
	}

	/**
	 * Handles mouse input.
	 */
	@Override
	public void _handleMouseInput() {
		if (!mouseOver)
			return;
		handleMouseScroll();
	}

	public void handleMouseScroll() {
		int input = Mouse.getEventDWheel();

		if (input != 0) {
			if (input > 0) {
				input = 1;
			}
			if (input < 0) {
				input = -1;
			}

			this.currentScroll -= input * 20;

			if (this.currentScroll > maxScroll) {
				this.currentScroll = maxScroll;
			}
			if (this.currentScroll < 0) {
				this.currentScroll = 0;
			}
		}
	}

	@Override
	public void _renderBackground(float f, int mouseX, int mouseY) {
		mouseOver = this.contains(mouseX, mouseY);
		float xScale = xSize / 20.0F;

		int guiLeft = getLeft();
		int guiTop = getTop();

		boolean isClicking = Mouse.isButtonDown(0);
		int slidertop = guiTop + 4;
		int sliderbottom = guiTop + ySize - 19;
		float sliderpos = 0;
		if (maxScroll == 0) {
			currentScroll = 0;
		} else {
			sliderpos = currentScroll / (float) maxScroll;
		}
		if (sliderpos > 1)
			sliderpos = 1;
		if (currentScroll == 0)
			sliderpos = 0;
		if (!this.wasClicking && isClicking && mouseOver) {
			this.isScrolling = true;
		}

		if (!isClicking) {
			this.isScrolling = false;
		}
		this.wasClicking = isClicking;
		if (this.isScrolling) {
			sliderpos = (mouseY - slidertop - 7.5F) / (sliderbottom - slidertop);

			if (sliderpos < 0.0F) {
				sliderpos = 0.0F;
			}

			if (sliderpos > 1.0F) {
				sliderpos = 1.0F;
			}

			this.currentScroll = (int) (sliderpos * maxScroll);
		}

		//Draw the scrollbar
		//XXX: zLevels on Slider
		mc.renderEngine.bindTexture(Assets.GUIs.scrollbar);
		GlStateManager.color(1F, 1F, 1F, 1F);
		GlStateManager.pushMatrix();
		GlStateManager.translate(guiLeft, guiTop, 0);
		GlStateManager.scale(xScale, 1F, 1F);
		Gui.drawModalRectWithCustomSizedTexture(0, 0, 0, 0, 20, 4, 32, 32);
		for (int y = 4; y < ySize - 4; y += 22) {
			int ys = 22;
			if (ys > ySize - y - 4) {
				ys = ySize - y - 4;
			}
			Gui.drawModalRectWithCustomSizedTexture(0, 0 + y, 0, 4, 20, ys, 32, 32);
		}
		//Gui.func_146110_a
		Gui.drawModalRectWithCustomSizedTexture(0, 0 + ySize - 4, 0, 26, 20, 4, 32, 32);
		if (isScrolling) {
			Gui.drawModalRectWithCustomSizedTexture(4, 4 + (int) ((sliderbottom - slidertop) * sliderpos), 20, 15, 12, 15, 32, 32);
		} else {
			Gui.drawModalRectWithCustomSizedTexture(4, 4 + (int) ((sliderbottom - slidertop) * sliderpos), 20, 0, 12, 15, 32, 32);
		}
		GlStateManager.popMatrix();
	}
}
