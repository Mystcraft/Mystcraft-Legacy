package com.xcompwiz.mystcraft.client.gui.element;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.xcompwiz.mystcraft.client.gui.GuiUtils;
import com.xcompwiz.mystcraft.page.Page;
import com.xcompwiz.mystcraft.symbol.IAgeSymbol;
import com.xcompwiz.mystcraft.symbol.SymbolManager;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiElementScrollablePages extends GuiElement {

	public interface IGuiScrollableClickHandler {

		void onItemPlace(GuiElementScrollablePages guiElementScrollablePages, int index, int mousebutton);

		void onItemRemove(GuiElementScrollablePages guiElementScrollablePages, int clickedpage);

	}

	public interface IGuiPageListProvider {
		List<ItemStack> getPageList();
	}

	private IGuiPageListProvider		pagesprovider;
	private IGuiScrollableClickHandler	listener;

	private int							firstElement;
	private int							elementWidth;
	private int							elementHeight;
	private int							arrowWidth;
	private int							hoverpage	= -1;
	private int							clickedpage	= -1;
	private List<String>				hovertext	= new ArrayList<String>();
	private boolean						mouseOver;

	public GuiElementScrollablePages(IGuiScrollableClickHandler handler, IGuiPageListProvider pagesprovider, Minecraft mc, int left, int top, int width, int height) {
		super(left, top, width, height);
		this.mc = mc;
		this.listener = handler;
		this.pagesprovider = pagesprovider;
		elementHeight = ySize - 6;
		elementWidth = elementHeight * 3 / 4;
		arrowWidth = ySize / 5;
	}

	@Override
	public boolean isVisible() {
		return super.isVisible() && getPageList() != null;
	}

	/**
	 * Handles mouse input.
	 */
	@Override
	public void handleMouseInput() {
		if (this.isEnabled() == false) { return; }
		if (!mouseOver) return;
		int input = Mouse.getEventDWheel();

		if (input != 0) {
			if (input > 0) {
				cycleLeft();
			} else {
				cycleRight();
			}
		}
	}

	@Override
	public boolean mouseClicked(int i, int j, int k) {
		if (this.isEnabled() == false) { return false; }
		List<ItemStack> pageList = getPageList();
		if (pageList == null) return false;
		if (i > guiLeft && i < guiLeft + xSize && j > guiTop && j < guiTop + ySize) {
			if (i > guiLeft && i < guiLeft + arrowWidth && j > guiTop && j < guiTop + ySize) {
				cycleLeft();
				return true;
			}
			if (i > guiLeft + xSize - arrowWidth && i < guiLeft + xSize && j > guiTop && j < guiTop + ySize) {
				cycleRight();
				return true;
			}
			if (mc.thePlayer.inventory.getItemStack() != null) {
				int index = hoverpage;
				if (index == -1) index = pageList.size();
				listener.onItemPlace(this, index, k);
				return true;
			}
			if (hoverpage != -1) {
				clickedpage = hoverpage;
				return true;
			}
		}
		return false;
	}

	private List<ItemStack> getPageList() {
		return pagesprovider.getPageList();
	}

	private void cycleRight() {
		firstElement += 1;
		int size = getPageList().size();
		if (firstElement >= size) firstElement = size - 1;
		if (firstElement < 0) firstElement = 0;
	}

	private void cycleLeft() {
		firstElement -= 1;
		if (firstElement < 0) firstElement = 0;
	}

	@Override
	public void mouseUp(int i, int j, int k) {
		if (this.isEnabled() == false) { return; }
		if (clickedpage != -1 && hoverpage == clickedpage) {
			listener.onItemRemove(this, clickedpage);
		}
		clickedpage = -1;
	}

	@Override
	public List<String> getTooltipInfo() {
		if (hovertext != null && hovertext.size() > 0) { return hovertext; }
		return super.getTooltipInfo();
	}

	@Override
	public void render(float f, int mouseX, int mouseY) {
		mouseOver = GuiUtils.contains(mouseX, mouseY, guiLeft, guiTop, xSize, ySize);
		hovertext.clear();
		GL11.glPushMatrix();
		GL11.glTranslatef(guiLeft, guiTop, 0);
		mouseX -= guiLeft;
		mouseY -= guiTop;

		int color = 0xAA000000;
		drawRect(0, 0, xSize, ySize, color); // Back
		if (firstElement == 0) {
			color = 0x33000000;
		}
		drawRect(0, 0, arrowWidth, ySize, color); // Left arrow
		List<ItemStack> pageList = getPageList();
		if (pageList == null || pageList.size() == 0 || pageList.size() - 1 == firstElement) {
			color = 0x33000000;
		} else {
			color = 0xAA000000;
		}
		drawRect(xSize, 0, xSize - arrowWidth, ySize, color); // Right arrow

		// Render pages
		this.drawGradientRect(arrowWidth, 0, xSize - arrowWidth, ySize, 0x000000, 0x000000);
		GL11.glDepthFunc(GL11.GL_GREATER);
		this.zLevel = 20.0F;
		GL11.glPushMatrix();
		hoverpage = -1;
		if (pageList != null) {
			float x = arrowWidth + 2;
			float y = 3;
			float pagexSize = elementWidth;
			float pageySize = elementHeight;
			for (int i = firstElement; i < pageList.size(); ++i) {
				ItemStack page = pageList.get(i);
				GuiUtils.drawPage(mc.renderEngine, zLevel, page, pagexSize, pageySize, x, y, false);
				if (GuiUtils.contains(mouseX, mouseY, (int) x, (int) y, (int) pagexSize, (int) pageySize)) {
					hoverpage = i;
					Page.getTooltip(page, hovertext);
					if (Page.getSymbol(page) != null) {
						IAgeSymbol symbol = SymbolManager.getAgeSymbol(Page.getSymbol(page));
						if (symbol != null) hovertext.add(symbol.displayName());
					}
				}
				x += pagexSize + 2;
				if (x > xSize) break;
			}
		}
		GL11.glPopMatrix();
		GL11.glDepthFunc(GL11.GL_LEQUAL);
		this.zLevel = 30.0F;
		GL11.glPopMatrix();
	}

	@Override
	public boolean keyTyped(char c, int i) {
		if (this.isEnabled() == false) { return false; }
		if (i == Keyboard.KEY_LEFT || i == mc.gameSettings.keyBindLeft.getKeyCode()) {
			cycleLeft();
			return true;
		}
		if (i == Keyboard.KEY_RIGHT || i == mc.gameSettings.keyBindRight.getKeyCode()) {
			cycleRight();
			return true;
		}
		return false;
	}

}
