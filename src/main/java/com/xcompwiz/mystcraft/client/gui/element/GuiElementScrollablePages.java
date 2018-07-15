package com.xcompwiz.mystcraft.client.gui.element;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.xcompwiz.mystcraft.api.symbol.IAgeSymbol;
import com.xcompwiz.mystcraft.client.gui.GuiUtils;
import com.xcompwiz.mystcraft.page.Page;
import com.xcompwiz.mystcraft.symbol.SymbolManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiElementScrollablePages extends GuiElement {

	public interface IGuiScrollableClickHandler {

		void onItemPlace(GuiElementScrollablePages guiElementScrollablePages, int index, int mousebutton);

		void onItemRemove(GuiElementScrollablePages guiElementScrollablePages, int clickedpage);

	}

	public interface IGuiPageListProvider {

		List<ItemStack> getPageList();

	}

	private IGuiPageListProvider pagesprovider;
	private IGuiScrollableClickHandler listener;

	private int firstElement;
	private int elementWidth;
	private int elementHeight;
	private int arrowWidth;
	private int hoverpage = -1;
	private int clickedpage = -1;
	private List<String> hovertext = new ArrayList<String>();
	private boolean mouseOver;

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
	public void _handleMouseInput() {
		if (!this.isEnabled()) {
			return;
		}
		if (!mouseOver)
			return;
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
	public boolean _onMouseUp(int mouseX, int mouseY, int button) {
		pickupHoveredPage();
		return false;
	}

	@Override
	public boolean _onMouseDrag(int mouseX, int mouseY, int clicked_id, long lastclick) {
		pickupHoveredPage();
		return false;
	}

	@Override
	public boolean _onMouseDown(int mouseX, int mouseY, int button) {
		if (!this.isEnabled()) {
			return false;
		}
		List<ItemStack> pageList = getPageList();
		if (pageList == null)
			return false;
		int guiLeft = getLeft();
		int guiTop = getTop();
		if (mouseX > guiLeft && mouseX < guiLeft + xSize && mouseY > guiTop && mouseY < guiTop + ySize) {
			if (mouseX > guiLeft && mouseX < guiLeft + arrowWidth && mouseY > guiTop && mouseY < guiTop + ySize) {
				cycleLeft();
				return true;
			}
			if (mouseX > guiLeft + xSize - arrowWidth && mouseX < guiLeft + xSize && mouseY > guiTop && mouseY < guiTop + ySize) {
				cycleRight();
				return true;
			}
			if (!mc.player.inventory.getItemStack().isEmpty()) {
				int index = hoverpage;
				if (index == -1)
					index = pageList.size();
				listener.onItemPlace(this, index, button);
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
		if (firstElement >= size)
			firstElement = size - 1;
		if (firstElement < 0)
			firstElement = 0;
	}

	private void cycleLeft() {
		firstElement -= 1;
		if (firstElement < 0)
			firstElement = 0;
	}

	private void pickupHoveredPage() {
		if (clickedpage != -1 && hoverpage == clickedpage) {
			listener.onItemRemove(this, clickedpage);
		}
		clickedpage = -1;
	}

	@Override
	public List<String> _getTooltipInfo() {
		if (hovertext != null && hovertext.size() > 0) {
			return hovertext;
		}
		return super._getTooltipInfo();
	}

	@Override
	public void _renderBackground(float f, int mouseX, int mouseY) {
		int guiLeft = getLeft();
		int guiTop = getTop();
		mouseOver = this.contains(mouseX, mouseY);
		GlStateManager.pushMatrix();
		GlStateManager.translate(guiLeft, guiTop, 0);
		mouseX -= guiLeft;
		mouseY -= guiTop;

		int color = 0xAA000000;
		drawRect(0, 0, xSize, ySize, color); // Back

		// Render pages
		GlStateManager.pushMatrix();
		GuiUtils.startGlScissor(guiLeft + 1, guiTop, xSize - 2, ySize);

		boolean noHover = true;
		List<ItemStack> pageList = getPageList();
		if (pageList != null) {
			float x = 2;
			float y = 3;
			float pagexSize = elementWidth;
			float pageySize = elementHeight;
			for (int i = firstElement; i < pageList.size(); ++i) {
				ItemStack page = pageList.get(i);
				GuiUtils.drawPage(mc.renderEngine, this.getZLevel(), page, pagexSize, pageySize, x, y);
				if (GuiUtils.contains(mouseX, mouseY, (int) x, (int) y, (int) pagexSize, (int) pageySize)) {
					noHover = false;
					if (hoverpage != i) {
						hoverpage = i;
						hovertext.clear();

						Page.getTooltip(page, hovertext);
						if (Page.getSymbol(page) != null) {
							IAgeSymbol symbol = SymbolManager.getAgeSymbol(Page.getSymbol(page));
							if (symbol != null)
								hovertext.add(symbol.getLocalizedName());
						}
						net.minecraftforge.event.ForgeEventFactory.onItemTooltip(page, this.mc.player, hovertext, ITooltipFlag.TooltipFlags.NORMAL);
					}
				}
				x += pagexSize + 2;
				if (x > xSize)
					break;
			}
		}
		if (noHover) {
			hoverpage = -1;
			hovertext.clear();
		}
		
		GuiUtils.endGlScissor();
		GlStateManager.popMatrix();
		if (firstElement == 0) {
			color = 0x33000000;
		}
		GuiUtils.drawGradientRect(0, 0, arrowWidth, ySize, color, color, getZLevel()); // Left arrow
		if (pageList == null || pageList.size() == 0 || pageList.size() - 1 == firstElement) {
			color = 0x33000000;
		} else {
			color = 0xAA000000;
		}
		GuiUtils.drawGradientRect(xSize - arrowWidth, 0, xSize, ySize, color, color, getZLevel()); // Right arrow
		GlStateManager.popMatrix();
	}

	@Override
	public boolean _onKeyPress(char c, int i) {
		if (!this.isEnabled()) {
			return false;
		}
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
