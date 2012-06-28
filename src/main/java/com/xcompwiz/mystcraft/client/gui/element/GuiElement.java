package com.xcompwiz.mystcraft.client.gui.element;

import java.util.HashSet;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class GuiElement extends Gui {

	protected Minecraft				mc;

	private boolean					visible		= true;
	private boolean					enabled		= true;

	protected int					guiLeft;
	protected int					guiTop;
	protected int					xSize;
	protected int					ySize;

	protected HashSet<GuiElement>	elements	= new HashSet<GuiElement>();

	public int getXPos() {
		return guiLeft;
	}

	public int getYPos() {
		return guiTop;
	}

	public int getXSize() {
		return xSize;
	}

	public int getYSize() {
		return ySize;
	}

	public GuiElement(int guiLeft, int guiTop, int xSize, int ySize) {
		this.mc = Minecraft.getMinecraft();
		this.guiLeft = guiLeft;
		this.guiTop = guiTop;
		this.xSize = xSize;
		this.ySize = ySize;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public boolean isEnabled() {
		return enabled && isVisible();
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void update() {
		for (GuiElement elem : elements) {
			elem.update();
		}
	}

	public void handleMouseInput() {
		for (GuiElement elem : elements) {
			elem.handleMouseInput();
		}
	}

	public boolean keyTyped(char c, int i) {
		for (GuiElement elem : elements) {
			if (elem.keyTyped(c, i)) { return true; }
		}
		return false;
	}

	public void mouseUp(int i, int j, int k) {
		for (GuiElement elem : elements) {
			elem.mouseUp(i, j, k);
		}
	}

	public boolean mouseClicked(int i, int j, int k) {
		for (GuiElement elem : elements) {
			if (elem.mouseClicked(i, j, k)) { return true; }
		}
		return false;
	}

	public void render(float f, int mouseX, int mouseY) {
		if (!isVisible()) return;
		for (GuiElement elem : elements) {
			elem.render(f, mouseX, mouseY);
		}
	}

	public void renderForeground(int mouseX, int mouseY) {
		if (!isVisible()) return;
		for (GuiElement elem : elements) {
			elem.renderForeground(mouseX, mouseY);
		}
	}

	public List<String> getTooltipInfo() {
		List<String> list = null;
		for (GuiElement elem : elements) {
			list = elem.getTooltipInfo();
			if (list != null) { return list; }
		}
		return list;
	}
}
