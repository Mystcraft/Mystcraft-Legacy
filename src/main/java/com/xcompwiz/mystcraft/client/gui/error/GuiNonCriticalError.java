/*
 * This class is based on code originally written by CovertJaguar
 */
package com.xcompwiz.mystcraft.client.gui.error;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.resources.I18n;

public class GuiNonCriticalError extends GuiYesNo {

	private final List<String>	message;

	public GuiNonCriticalError(List<String> message) {
		super(null, "", "", 0);
		this.message = message;
		this.confirmButtonText = I18n.format("OK", new Object[0]);
		this.cancelButtonText = I18n.format("menu.quit", new Object[0]);
	}

	/**
	 * Returns true if this GUI should pause the game when it is displayed in single-player
	 */
	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	@Override
	protected void actionPerformed(GuiButton par1GuiButton) {
		if (par1GuiButton.id == 0) {
			Minecraft.getMinecraft().displayGuiScreen(null);
		} else {
			Minecraft.getMinecraft().shutdown();
		}
	}

	@Override
	public void drawScreen(int par1, int par2, float par3) {
		super.drawScreen(par1, par2, par3);
		int y = 20;
		for (String msg : message) {
			this.drawCenteredString(this.fontRendererObj, msg, this.width / 2, y, 0xFFFFFF);
			y += 10;
		}
	}
}
