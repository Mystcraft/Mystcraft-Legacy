package com.xcompwiz.mystcraft.client.gui.element;

import net.minecraft.item.ItemStack;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.xcompwiz.mystcraft.client.gui.GuiUtils;
import com.xcompwiz.mystcraft.data.Assets;
import com.xcompwiz.mystcraft.item.IItemWritable;
import com.xcompwiz.mystcraft.words.DrawableWordManager;

public class GuiElementNotebookTabs extends GuiElement {
	public interface IGuiNotebookTabsHandler {
		public ItemStack getItemInSlot(byte slot);

		public void setTopNotebookSlot(int topslot);

		public void onNotebookTabClick(int button, byte slot);

		public byte getTopSlot();

		public byte getActiveTab();
	}

	private IGuiNotebookTabsHandler	listener;
	private static final int		windowsizeY	= 166;	//XXX: (PageRender) Revise handling of gui textures so this isn't necessary

	private static final byte		tabCount	= 4;

	public GuiElementNotebookTabs(IGuiNotebookTabsHandler eventhandler, int guiLeft, int guiTop, int width, int height) {
		super(guiLeft, guiTop, width, height);
		this.listener = eventhandler;
	}

	private void cycleNotebookUp() {
		byte topslot = getTopSlot();
		if (topslot == 0) return;
		--topslot;
		listener.setTopNotebookSlot(topslot);
	}

	private void cycleNotebookDown() {
		byte topslot = getTopSlot();
		if (topslot == getMaxNotebookCount() - tabCount) return;
		++topslot;
		listener.setTopNotebookSlot(topslot);
	}

	@Override
	public boolean keyTyped(char c, int i) {
		super.keyTyped(c, i);
		if (i == Keyboard.KEY_UP || i == mc.gameSettings.keyBindForward.getKeyCode()) {
			cycleNotebookUp();
			return true;
		} else if (i == Keyboard.KEY_DOWN || i == mc.gameSettings.keyBindBack.getKeyCode()) {
			cycleNotebookDown();
			return true;
		}
		return false;
	}

	@Override
	public boolean mouseClicked(int x, int y, int button) {
		super.mouseClicked(x, y, button);
		byte topslot = getTopSlot();

		int tabY = guiTop;
		if (GuiUtils.contains(x, y, guiLeft, tabY, 58, 9)) {
			cycleNotebookUp();
			return true;
		}
		tabY += 9;
		for (byte slot = topslot; slot < topslot + tabCount; ++slot) {
			if (GuiUtils.contains(x, y, guiLeft, tabY + 1, 58, 35) && !GuiUtils.contains(x, y, guiLeft + 35, tabY + 2, 19, 19)) {
				listener.onNotebookTabClick(button, slot);
				return true;
			}
			tabY += 37;
		}
		if (GuiUtils.contains(x, y, guiLeft, tabY, 58, 9)) {
			cycleNotebookDown();
			return true;
		}
		return false;
	}

	@Override
	public void render(float f, int mouseX, int mouseY) {
		byte topslot = getTopSlot();
		// Begin Notebook slots
		int tabY = this.guiTop;
		int xSizeTab = 58;
		int ySizeTab = 37;
		byte activeslot = getActiveTab();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		if (activeslot < topslot) {
			GL11.glColor4f(0.5F, 0.5F, 1.0F, 1.0F);
		}
		if (topslot == 0) {
			GL11.glColor4f(0.4F, 0.4F, 0.4F, 1.0F);
		}
		mc.renderEngine.bindTexture(Assets.gui_desk);
		drawTexturedModalRect(guiLeft, tabY, 0, windowsizeY + ySizeTab, xSizeTab, 9);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		tabY += 9;
		for (byte i = topslot; i < topslot + tabCount; ++i) {
			if (i == activeslot) {
				GL11.glColor4f(0.5F, 0.5F, 1.0F, 1.0F);
			}
			mc.renderEngine.bindTexture(Assets.gui_desk);
			drawTexturedModalRect(guiLeft, tabY, 0, windowsizeY, xSizeTab, ySizeTab);
			GuiUtils.drawWord(mc.renderEngine, zLevel, DrawableWordManager.getDrawableWord("" + i), 19, guiLeft + 8, tabY + 3);

			ItemStack pagesource = this.getTabContents(i);
			if (pagesource != null) {
				String name = null;
				if (pagesource.getItem() instanceof IItemWritable) {
					name = ((IItemWritable) pagesource.getItem()).getDisplayName(mc.thePlayer, pagesource);
				}
				if (name != null) {
					GL11.glPushMatrix();
					float scale = 1;
					int j = mc.fontRenderer.getStringWidth(name) + 16;
					if (j > xSizeTab) {
						scale = (float) xSizeTab / (float) j;
					}
					GL11.glTranslatef(guiLeft + 4, tabY + 25, 0);
					GL11.glScalef(scale, scale, 1);
					mc.fontRenderer.drawString(name, 0, 0, 0x404040);
					GL11.glPopMatrix();
				}
			}

			tabY += ySizeTab;
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		}
		mc.renderEngine.bindTexture(Assets.gui_desk);
		if (activeslot >= topslot + tabCount) {
			GL11.glColor4f(0.5F, 0.5F, 1.0F, 1.0F);
		}
		if (topslot + tabCount == getMaxNotebookCount()) {
			GL11.glColor4f(0.4F, 0.4F, 0.4F, 1.0F);
		}
		drawTexturedModalRect(guiLeft, tabY, 0, windowsizeY + ySizeTab + 9, xSizeTab, 9);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		// End Notebook slots
	}

	private byte getTopSlot() {
		return this.listener.getTopSlot();
	}

	private byte getActiveTab() {
		return this.listener.getActiveTab();
	}

	private int getMaxNotebookCount() {
		return 25;
		//XXX: (GuiElementItemSlot) Alternatively, utilize slot gui elements and have the whole list of notebooks available client-side from the get go
	}

	private ItemStack getTabContents(byte slot) {
		return this.listener.getItemInSlot(slot);
	}
}
