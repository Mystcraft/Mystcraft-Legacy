package com.xcompwiz.mystcraft.client.gui;

import java.io.IOException;
import java.util.List;

import com.xcompwiz.mystcraft.client.gui.element.GuiElement;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementPanel;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;

public abstract class GuiContainerElements extends GuiContainer {

	private GuiElement	rootelement;

	public GuiContainerElements(Container container) {
		super(container);
	}

	@Override
	public final void initGui() {
		super.initGui();
		this.initialization();
		rootelement = new GuiElementPanel(this.guiLeft, this.guiTop, this.xSize, this.ySize);
		rootelement.setZLevel(10);
		this.validate();
		rootelement.setLeft(this.guiLeft);
		rootelement.setTop(this.guiTop);
		rootelement.setWidth(this.xSize);
		rootelement.setHeight(this.ySize);
	}

	public void initialization() {}

	public void validate() {}

	public void addElement(GuiElement element) {
		this.rootelement.addElement(element);
	}

	public void bringToFront(GuiElement element) {
		this.rootelement.bringToFront(element);
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		rootelement.onTick();
	}

	@Override
	protected final void keyTyped(char c, int i) throws IOException {
		boolean eaten = rootelement.onKeyPress(c, i);
		this._keyTyped(c, i, eaten);
		if (!eaten) super.keyTyped(c, i);
	}

	protected void _keyTyped(char c, int i, boolean eaten) {}

	@Override
	public final void handleMouseInput() throws IOException {
		super.handleMouseInput();
		rootelement.handleMouseInput();
	}

	@Override
	protected final void mouseClicked(int mouseX, int mouseY, int button) throws IOException {
		super.mouseClicked(mouseX, mouseY, button);
		this._onMouseDown(mouseX, mouseY, button, rootelement.onMouseDown(mouseX, mouseY, button));
	}

	protected void _onMouseDown(int mouseX, int mouseY, int button, boolean eaten) {}

	@Override
	protected final void mouseClickMove(int mouseX, int mouseY, int clicked_id, long lastclick) {
		super.mouseClickMove(mouseX, mouseY, clicked_id, lastclick);
		this._onMouseDrag(mouseX, mouseY, clicked_id, lastclick, rootelement.onMouseDrag(mouseX, mouseY, clicked_id, lastclick));
	}

	protected void _onMouseDrag(int mouseX, int mouseY, int clicked_id, long lastclick, boolean eaten) {};

	/**
	 * Draws the screen and all the components in it.
	 */
	@Override
	public final void drawGuiContainerBackgroundLayer(float partial, int mouseX, int mouseY) {
		_drawBackgroundLayer(mouseX, mouseY, partial);
		rootelement.renderBackground(partial, mouseX, mouseY);
	}

	protected void _drawBackgroundLayer(int mouseX, int mouseY, float partial) {};

	@Override
	public final void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		mouseX -= guiLeft;
		mouseY -= guiTop;
		this._drawForegroundLayer(mouseX, mouseY);
		rootelement.renderForeground(mouseX, mouseY);
		this.drawToolTip(mouseX, mouseY);
	}

	protected void _drawForegroundLayer(int mouseX, int mouseY) {};

	private final void drawToolTip(int mouseX, int mouseY) {
		List<String> tooltip = rootelement.getTooltipInfo();
		if (tooltip == null) {
			tooltip = this.getTooltipInfo();
		}
		if (tooltip != null) {
			GuiUtils.drawTooltip(fontRendererObj, mouseX + 12, mouseY - 12, zLevel, tooltip, this.width, this.height);
		}
	}

	protected List<String> getTooltipInfo() {
		return null;
	}
}
