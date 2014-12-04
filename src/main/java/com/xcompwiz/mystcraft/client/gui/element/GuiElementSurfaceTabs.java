package com.xcompwiz.mystcraft.client.gui.element;

import net.minecraft.item.ItemStack;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.xcompwiz.mystcraft.api.item.IItemRenameable;
import com.xcompwiz.mystcraft.client.gui.GuiUtils;
import com.xcompwiz.mystcraft.data.Assets.GUIs;
import com.xcompwiz.mystcraft.words.DrawableWordManager;

public class GuiElementSurfaceTabs extends GuiElement {
	public interface IGuiSurfaceTabsHandler {
		public ItemStack getItemInSlot(byte slot);

		public void setTopTabSlot(int topslot);

		public void onSurfaceTabClick(int button, byte slot);

		public byte getTopSlot();

		public byte getActiveTab();
	}

	private IGuiSurfaceTabsHandler	listener;
	private static final int		windowsizeY	= 166;	//XXX: (PageRender) Revise handling of gui textures so this isn't necessary

	private static final byte		tabCount	= 4;

	public GuiElementSurfaceTabs(IGuiSurfaceTabsHandler eventhandler, int guiLeft, int guiTop, int width, int height) {
		super(guiLeft, guiTop, width, height);
		this.listener = eventhandler;
	}

	private void cycleTabUp() {
		byte topslot = getTopSlot();
		if (topslot == 0) return;
		--topslot;
		listener.setTopTabSlot(topslot);
	}

	private void cycleTabDown() {
		byte topslot = getTopSlot();
		if (topslot == getMaxTabCount() - tabCount) return;
		++topslot;
		listener.setTopTabSlot(topslot);
	}

	@Override
	public boolean _onKeyPress(char c, int i) {
		if (i == Keyboard.KEY_UP || i == mc.gameSettings.keyBindForward.getKeyCode()) {
			cycleTabUp();
			return true;
		} else if (i == Keyboard.KEY_DOWN || i == mc.gameSettings.keyBindBack.getKeyCode()) {
			cycleTabDown();
			return true;
		}
		return false;
	}

	@Override
	public boolean _onMouseDown(int x, int y, int button) {
		byte topslot = getTopSlot();
		int guiLeft = getLeft();
		int guiTop = getTop();

		int tabY = guiTop;
		if (GuiUtils.contains(x, y, guiLeft, tabY, 58, 9)) {
			cycleTabUp();
			return true;
		}
		tabY += 9;
		for (byte slot = topslot; slot < topslot + tabCount; ++slot) {
			if (GuiUtils.contains(x, y, guiLeft, tabY + 1, 58, 35) && !GuiUtils.contains(x, y, guiLeft + 35, tabY + 2, 19, 19)) {
				listener.onSurfaceTabClick(button, slot);
				return true;
			}
			tabY += 37;
		}
		if (GuiUtils.contains(x, y, guiLeft, tabY, 58, 9)) {
			cycleTabDown();
			return true;
		}
		return false;
	}

	@Override
	public void _renderBackground(float f, int mouseX, int mouseY) {
		int guiLeft = getLeft();
		int guiTop = getTop();
		byte topslot = getTopSlot();
		// Begin surface slots
		int tabY = guiTop;
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
		mc.renderEngine.bindTexture(GUIs.desk);
		drawTexturedModalRect(guiLeft, tabY, 0, windowsizeY + ySizeTab, xSizeTab, 9);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		tabY += 9;
		for (byte i = topslot; i < topslot + tabCount; ++i) {
			if (i == activeslot) {
				GL11.glColor4f(0.5F, 0.5F, 1.0F, 1.0F);
			}
			mc.renderEngine.bindTexture(GUIs.desk);
			drawTexturedModalRect(guiLeft, tabY, 0, windowsizeY, xSizeTab, ySizeTab);
			GuiUtils.drawWord(mc.renderEngine, getZLevel(), DrawableWordManager.getDrawableWord("" + i), 19, guiLeft + 8, tabY + 3);

			ItemStack pagesource = this.getTabContents(i);
			if (pagesource != null) {
				String name = null;
				if (pagesource.getItem() instanceof IItemRenameable) {
					name = ((IItemRenameable) pagesource.getItem()).getDisplayName(mc.thePlayer, pagesource);
				} else {
					name = pagesource.getDisplayName();
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
		mc.renderEngine.bindTexture(GUIs.desk);
		if (activeslot >= topslot + tabCount) {
			GL11.glColor4f(0.5F, 0.5F, 1.0F, 1.0F);
		}
		if (topslot + tabCount == getMaxTabCount()) {
			GL11.glColor4f(0.4F, 0.4F, 0.4F, 1.0F);
		}
		drawTexturedModalRect(guiLeft, tabY, 0, windowsizeY + ySizeTab + 9, xSizeTab, 9);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		// End surface slots
	}

	private byte getTopSlot() {
		return this.listener.getTopSlot();
	}

	private byte getActiveTab() {
		return this.listener.getActiveTab();
	}

	private int getMaxTabCount() {
		return 25;
		//XXX: (GuiElementItemSlot) Alternatively, utilize slot gui elements and have the whole list of tab items available client-side from the get go
	}

	private ItemStack getTabContents(byte slot) {
		return this.listener.getItemInSlot(slot);
	}
}
