package com.xcompwiz.mystcraft.client.gui.element;

public class GuiElementButtonToggle extends GuiElementButton {

	public interface IGuiStateProvider {
		public boolean getState(String id);
	}

	private IGuiStateProvider stateprovider;

	public GuiElementButtonToggle(IGuiOnClickHandler eventhandler, IGuiStateProvider stateprovider, String id, int guiLeft, int guiTop, int width, int height) {
		super(eventhandler, id, guiLeft, guiTop, width, height);
		this.stateprovider = stateprovider;
	}

	@Override
	protected boolean isDepressed() {
		return getState();
	}

	private boolean getState() {
		return stateprovider.getState(this.getId());
	}

}
