package com.xcompwiz.mystcraft.client.gui.element;

import java.util.List;

import com.xcompwiz.mystcraft.client.gui.element.GuiElementButton.IGuiOnClickHandler;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementLabel.IGuiLabelDataProvider;

public class GuiElementValueCounter extends GuiElement implements IGuiOnClickHandler, IGuiLabelDataProvider {
	public interface IGuiOnValueChangeHandler {
		public void setCounterValue(String key, int newval);
	}

	public interface IGuiValueProvider {
		public int getCounterValue(String key);
	}

	private String						key;
	private IGuiOnValueChangeHandler	handler;
	private IGuiValueProvider			valueprovider;

	private int							buttonWidth;
	private int							buttonYPadding;

	public GuiElementValueCounter(IGuiOnValueChangeHandler handler, IGuiValueProvider valueprovider, String key, int guiLeft, int guiTop, int xSize, int ySize) {
		super(guiLeft, guiTop, xSize, ySize);
		this.key = key;
		this.handler = handler;
		this.valueprovider = valueprovider;

		this.buttonWidth = this.xSize / 6;
		if (this.buttonWidth > this.ySize) {
			this.buttonWidth = this.ySize;
		}
		this.buttonYPadding = 0;
		if (this.buttonWidth < ySize) {
			this.buttonYPadding = (ySize - this.buttonWidth) / 2;
		}

		addElement(createButton(this, "--", 0, buttonYPadding, buttonWidth));
		addElement(createButton(this, "-", buttonWidth, buttonYPadding, buttonWidth));
		addElement(createButton(this, "+", this.xSize - buttonWidth - buttonWidth, buttonYPadding, buttonWidth));
		addElement(createButton(this, "++", this.xSize - buttonWidth, buttonYPadding, buttonWidth));
		addElement(new GuiElementLabel(this, (buttonWidth) * 2, buttonYPadding, this.xSize - (buttonWidth) * 4, ySize - buttonYPadding * 2, 0x7F000000, 0xFFFFFFFF));
	}

	private GuiElement createButton(IGuiOnClickHandler eventhandler, String id, int guiLeft, int guiTop, int width) {
		GuiElementButton button = new GuiElementButton(eventhandler, id, guiLeft, guiTop, width, width);
		button.setText(id);
		return button;
	}

	public void updateValue(int newvalue) {
		handler.setCounterValue(key, newvalue);
	}

	@Override
	public void onClick(GuiElementButton caller) {
		String id = caller.getId();
		if (id.equals("+")) {
			updateValue(this.getValue() + 1);
		}
		if (id.equals("++")) {
			updateValue(this.getValue() + 10);
		}
		if (id.equals("-")) {
			updateValue(this.getValue() - 1);
		}
		if (id.equals("--")) {
			updateValue(this.getValue() - 10);
		}
	}

	private int getValue() {
		return valueprovider.getCounterValue(key);
	}

	@Override
	public String getText() {
		return String.valueOf(this.getValue());
	}

	@Override
	public List<String> getTooltip() {
		return null;
	}

}
