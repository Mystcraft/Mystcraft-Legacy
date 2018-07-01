package com.xcompwiz.mystcraft.client.gui.element;

import java.util.Collection;

public class GuiElementListBox extends GuiElement {

	private GuiElementVSlider scrollbar;
	private GuiElementPanel container;

	public GuiElementListBox(int guiLeft, int guiTop, int xSize, int ySize) {
		super(guiLeft, guiTop, xSize, ySize);
		this.container = new GuiElementPanel(0, 0, xSize - 20, ySize);
		this.container.setBackground(0xCC880088, 0x22880088);
		this.scrollbar = new GuiElementVSlider(xSize - 20, 0, 16, ySize);
		super.addElement(container);
		super.addElement(scrollbar);
	}

	@Override
	public void addElement(GuiElement element) {
		this.container.addElement(element);
		updatePositions();
	}

	@Override
	public void addElements(Collection<GuiElement> elements) {
		this.container.addElements(elements);
		updatePositions();
	}

	@Override
	public void removeElement(GuiElement element) {
		this.container.removeElement(element);
		updatePositions();
	}

	@Override
	public void clearAllElements() {
		this.container.clearAllElements();
	}

	@Override
	public void _onTick() {
		updatePositions();
	}

	private void updatePositions() {
		int maxScroll = -ySize;
		int y = -scrollbar.getCurrentPos();
		for (GuiElement elem : this.container.getElements()) {
			if (!elem.isVisible())
				continue;
			maxScroll += elem.getHeight();
			elem.setPosition(0, y);
			y += elem.getHeight();
		}
		if (maxScroll < 0)
			maxScroll = 0;
		scrollbar.setMaxScroll(maxScroll);
	}
}
