package com.xcompwiz.mystcraft.client.gui.element;

public class GuiElementButton extends GuiElementButtonBase {

	public interface IGuiOnClickHandler {
		void onClick(GuiElementButton caller);
	}

	private IGuiOnClickHandler eventhandler;
	private final String id;

	public GuiElementButton(IGuiOnClickHandler eventhandler, String id, int guiLeft, int guiTop, int width, int height) {
		super(guiLeft, guiTop, width, height);
		this.eventhandler = eventhandler;
		this.id = id;
	}

	public String getId() {
		return id;
	}

	@Override
	protected void onClick(int i, int j, int k) {
		if (this.eventhandler != null) {
			this.eventhandler.onClick(this);
		}
	}

}
