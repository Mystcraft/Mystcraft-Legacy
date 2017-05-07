package com.xcompwiz.mystcraft.client.gui.element.data;

import com.xcompwiz.mystcraft.client.gui.GuiUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.util.IIcon;

public class GuiIconIcon implements IGuiIcon {
	public interface IIconProvider {
		public IIcon getIcon(GuiIconIcon caller);
	}

	private IIconProvider	provider;
	private String			id;
	private IIcon			icon;

	public GuiIconIcon(IIcon icon) {
		this.icon = icon;
	}

	public GuiIconIcon(IIconProvider provider, String id) {
		this.provider = provider;
		this.id = id;
	}

	public String getId() {
		return id;
	}

	private IIcon getIcon() {
		if (provider != null) return provider.getIcon(this);
		return icon;
	}

	@Override
	public void render(Minecraft mc, int guiLeft, int guiTop, int xSize, int ySize, float zLevel) {
		GuiUtils.drawIcon(guiLeft, guiTop, getIcon(), xSize, ySize, zLevel);
	}

}
