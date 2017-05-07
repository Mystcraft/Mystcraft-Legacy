package com.xcompwiz.mystcraft.world.profiling;

import java.util.ArrayList;
import java.util.List;

import com.xcompwiz.mystcraft.MystcraftFirstRun;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiOptionButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

public class GuiMystcraftProfiling extends GuiScreen implements IMystcraftProfilingCallback {

	private GuiScreen			parentscreen;
	private final List<String>	message;
	private String				buttonText;
	private int					valTotal;
	private int					valQueued;
	private int					valComplete;

	private int					borderColor		= 0xFFA0A0A0;
	private int					backgroundColor	= 0xFF000000;
	private int					fillColor		= 0xFF50EE50;
	private int					fillColor2		= 0xFF80CCC0;
	private boolean				finished		= false;

	public GuiMystcraftProfiling(GuiScreen parentscreen) {
		this.parentscreen = parentscreen;
		ArrayList<String> messages = new ArrayList<String>();
		messages.add("Mystcraft is setting up. It requires a profile be made of the mods in use.");
		messages.add("The profiling runs once each time you add and/or remove mods.");
		messages.add("The process must complete before you can play a single-player game.");
		messages.add("Sorry for the wait!");
		messages.add("");
		messages.add("This can be run in the background without affecting the profile.");
		messages.add("You can still join a multiplayer game.");
		this.message = messages;
		this.buttonText = I18n.format("gui.toMenu", new Object[0]);
	}

	/**
	 * Returns true if this GUI should pause the game when it is displayed in single-player
	 */
	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	/**
	 * Adds the buttons (and other controls) to the screen in question.
	 */
	@Override
	public void initGui() {
		this.buttonList.add(new GuiOptionButton(0, this.width / 2 - 70, this.height / 6 + 96, this.buttonText));
	}

	@Override
	public void drawScreen(int par1, int par2, float par3) {
		if (finished) {
			MystcraftFirstRun.end();
			if (Minecraft.getMinecraft().currentScreen == this) Minecraft.getMinecraft().displayGuiScreen(parentscreen);
			return;
		}
		super.drawScreen(par1, par2, par3);
		int y = 20;
		for (String msg : message) {
			this.drawCenteredString(this.fontRendererObj, msg, this.width / 2, y, 0xFFFFFF);
			y += 10;
		}
		y += 10;
		float filled = valComplete / (float) valTotal;
		float underfilled = (valComplete + valQueued) / (float) valTotal;
		int xSize = this.width / 2;
		int ySize = 20;
		int guiLeft = this.width / 2 - xSize / 2;
		int guiTop = y;
		int fill1 = (int) ((xSize - 1) * filled);
		int fill2 = (int) ((xSize - 1) * underfilled);
		drawRect(guiLeft, guiTop, guiLeft + xSize, guiTop + ySize, this.borderColor);
		drawRect(guiLeft + 1, guiTop + 1, guiLeft + xSize - 1, guiTop + ySize - 1, this.backgroundColor);
		drawRect(guiLeft + 1, guiTop + 1, guiLeft + fill2, guiTop + ySize - 1, this.fillColor2);
		drawRect(guiLeft + 1, guiTop + 1, guiLeft + fill1, guiTop + ySize - 1, this.fillColor);
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		if (button.id == 0) {
			Minecraft.getMinecraft().displayGuiScreen(parentscreen);
		}
	}

	@Override
	public void setCompleted(int count) {
		this.valComplete = count;
	}

	@Override
	public void setRemaining(int remaining) {
		this.valTotal = this.valComplete + remaining;
	}

	@Override
	public void setQueued(int queued) {
		this.valQueued = queued;
	}

	@Override
	public void onFinished() {
		this.finished = true;
	}
}
