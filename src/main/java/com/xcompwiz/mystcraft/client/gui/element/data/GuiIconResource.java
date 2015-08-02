package com.xcompwiz.mystcraft.client.gui.element.data;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import com.xcompwiz.mystcraft.client.gui.GuiUtils;

public class GuiIconResource implements IGuiIcon {
	private ResourceLocation	resource;
	private int x,y,u,v;

	public GuiIconResource(ResourceLocation resource, int sourceX, int sourceY, int xSize, int ySize) {
		this.resource = resource;
		this.x = sourceX;
		this.y = sourceY;
		this.u = xSize;
		this.v = ySize;
	}

	@Override
	public void render(Minecraft mc, int guiLeft, int guiTop, int xSize, int ySize, float zLevel) {
		this.bindResource(mc);
		this.draw(guiLeft, guiTop, xSize, ySize, zLevel);
	}

	protected void draw(int guiLeft, int guiTop, int xSize, int ySize, float zLevel) {
		GuiUtils.drawTexturedModalRect(guiLeft, guiTop, x, y, u, v, zLevel, xSize, ySize);
	}

	protected void bindResource(Minecraft mc) {
		mc.renderEngine.bindTexture(resource);
	}
}
