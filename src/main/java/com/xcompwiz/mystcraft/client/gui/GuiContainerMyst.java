package com.xcompwiz.mystcraft.client.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;

import com.xcompwiz.mystcraft.client.gui.element.GuiElement;

public abstract class GuiContainerMyst extends GuiContainer {

	protected List<GuiElement>	elements	= new ArrayList<GuiElement>();

	public GuiContainerMyst(Container container) {
		super(container);
	}

	@Override
	public final void initGui() {
		super.initGui();
		this.elements.clear();
		this.validate();
	}

	public void validate() {}

	@Override
	public void updateScreen() {
		super.updateScreen();
		this.updateElements();
	}

	protected void updateElements() {
		for (GuiElement elem : elements) {
			elem.update();
		}
	}

	@Override
	protected void keyTyped(char c, int i) {
		for (GuiElement elem : elements) {
			if (elem.keyTyped(c, i)) { return; }
		}
		if (i == 1 || (i == mc.gameSettings.keyBindInventory.getKeyCode())) {
			mc.thePlayer.closeScreen();
		} else {
			super.keyTyped(c, i);
		}
	}

	@Override
	public void handleMouseInput() {
		super.handleMouseInput();
		this.handleElementMouseInput();
	}

	protected void handleElementMouseInput() {
		for (GuiElement elem : elements) {
			elem.handleMouseInput();
		}
	}

	@Override
	protected void mouseClicked(int i, int j, int k) {
		super.mouseClicked(i, j, k);
		this.handleElementMouseClicked(i, j, k);
	}

	protected void handleElementMouseClicked(int i, int j, int k) {
		for (GuiElement elem : elements) {
			elem.mouseClicked(i, j, k);
		}
	}

	@Override
	protected void mouseMovedOrUp(int i, int j, int k) {
		super.mouseMovedOrUp(i, j, k);
		this.handleElementMouseUp(i, j, k);
	}

	protected void handleElementMouseUp(int i, int j, int k) {
		for (GuiElement elem : elements) {
			elem.mouseUp(i, j, k);
		}
	}

	protected void drawElementBackgrounds(float f, int mouseX, int mouseY) {
		for (GuiElement elem : elements) {
			if (elem.isVisible()) {
				elem.render(f, mouseX, mouseY);
			}
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		this.drawToolTip(mouseX, mouseY);
		this.drawElementForegrounds(mouseX, mouseY);
	}

	protected void drawElementForegrounds(int mouseX, int mouseY) {
		for (GuiElement elem : elements) {
			if (elem.isVisible()) {
				elem.renderForeground(mouseX, mouseY);
			}
		}
	}

	protected void drawToolTip(int mouseX, int mouseY) {
		List<String> tooltip = null;
		for (GuiElement elem : elements) {
			if (elem.isVisible()) {
				tooltip = elem.getTooltipInfo();
				if (tooltip != null) break;
			}
		}
		if (tooltip == null) {
			tooltip = this.getTooltipInfo();
		}
		if (tooltip != null) {
			GuiUtils.drawTooltip(fontRendererObj, mouseX - guiLeft + 12, mouseY - guiTop - 12, zLevel, tooltip, guiLeft + xSize, guiTop + ySize);
		}
	}

	protected List<String> getTooltipInfo() {
		return null;
	}
}
