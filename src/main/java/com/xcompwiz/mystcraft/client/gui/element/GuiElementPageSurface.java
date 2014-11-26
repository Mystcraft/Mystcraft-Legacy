package com.xcompwiz.mystcraft.client.gui.element;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;

import com.xcompwiz.mystcraft.client.gui.GuiUtils;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementTextField.IGuiOnTextChange;
import com.xcompwiz.mystcraft.page.Page;
import com.xcompwiz.mystcraft.symbol.IAgeSymbol;
import com.xcompwiz.mystcraft.symbol.SymbolManager;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiElementPageSurface extends GuiElement implements IGuiOnTextChange {
	public static class PositionableItem {

		public int			slotId;
		public ItemStack	itemstack;
		public float		x;
		public float		y;

		public int			count	= 1;
	}

	public final static float	pagewidth	= 30;
	public final static float	pageheight	= pagewidth * 4 / 3;

	public interface IGuiPositionedPagesProvider {
		List<PositionableItem> getPositionedPages();

		void place(int index, boolean single);

		void pickup(PositionableItem hoverpage);

		void copy(PositionableItem hoverpage);
	}

	private IGuiPositionedPagesProvider	pagesProvider;

	private float						pageWidth;
	private float						pageHeight;

	private PositionableItem			hoverpage;
	private List<String>				hovertext			= new ArrayList<String>();

	private GuiElementVSlider			scrollbar;

	private boolean						mousedown;
	private String						searchtext;
	private boolean						mouseOverPageArea	= false;

	public GuiElementPageSurface(IGuiPositionedPagesProvider pagesProvider, Minecraft mc, int left, int top, int width, int height) {
		super(left, top, width, height);
		this.mc = mc;
		this.pagesProvider = pagesProvider;
		pageWidth = GuiElementPageSurface.pagewidth;
		pageHeight = GuiElementPageSurface.pageheight;
		mousedown = false;
		this.scrollbar = new GuiElementVSlider(xSize - 20, 0, 20, ySize);
		this.addElement(scrollbar);
	}

	/**
	 * Handles mouse input.
	 */
	@Override
	public void _handleMouseInput() {
		if (!mouseOverPageArea) return;
		scrollbar.handleMouseScroll();
	}

	@Override
	public boolean _onMouseDown(int mouseX, int mouseY, int button) {
		if (this.contains(mouseX, mouseY)) {
			if (mc.thePlayer.inventory.getItemStack() != null) {
				int index = getPages().size();
				if (hoverpage != null) index = hoverpage.slotId;
				pagesProvider.place(index, button == 1);
				return true;
			}
			if (button == 2) {
				//FIXME: (Surface) Pickblock on page -> clone
				return true;
			}
			if (hoverpage != null && button == 0) {
				pagesProvider.pickup(hoverpage);
				return true;
			}
			mousedown = true;
		}
		return false;
	}

	@Override
	public boolean _onMouseUp(int i, int j, int k) {
		int guiLeft = getLeft();
		int guiTop = getTop();
		if (GuiUtils.contains(i, j, guiLeft, guiTop, xSize - 20, ySize) && hoverpage != null && k == 1 && mousedown) {
			pagesProvider.copy(hoverpage);
			mousedown = false;
		}
		return false;
	}

	@Override
	public List<String> _getTooltipInfo() {
		if (hovertext != null && hovertext.size() > 0) { return hovertext; }
		return super._getTooltipInfo();
	}

	@Override
	public void _renderBackground(float f, int mouseX, int mouseY) {
		int guiLeft = getLeft();
		int guiTop = getTop();
		mouseOverPageArea = GuiUtils.contains(mouseX, mouseY, guiLeft, guiTop, xSize - 20, ySize);

		hovertext.clear();
		hoverpage = null;
		int color = 0xAA000000;
		drawRect(guiLeft, guiTop, guiLeft + xSize - 20, guiTop + ySize, color); // Back

		// Render pages
		GuiUtils.drawGradientRect(guiLeft, guiTop, guiLeft + xSize - 20, guiTop + ySize, 0x000000, 0x000000, this.getZLevel());
		//GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDepthFunc(GL11.GL_GREATER);
		this.setZLevel(1.0F);
		GL11.glPushMatrix();
		int currentScroll = -scrollbar.getCurrentPos();
		List<PositionableItem> pages = getPages();
		int maxScroll = 0;
		//XXX: (PageRender) If these were rendered as individual sub-elements, we could do some fancy things, like making them appear to move on sort (animated sort)
		if (pages != null) {
			float x = guiLeft;
			float y = guiTop + currentScroll;
			float pagexSize = pageWidth;
			float pageySize = pageHeight;
			for (PositionableItem positionable : pages) {
				ItemStack page = positionable.itemstack;
				float pageX = positionable.x;
				float pageY = positionable.y;
				if (pageY + pageHeight - ySize > maxScroll) {
					maxScroll = (int) (pageY + pageHeight + 6 - ySize);
				}
				if (y + pageY < guiTop - pageHeight) continue;
				if (y + pageY > guiTop + ySize) continue;
				String displayname = null;
				if (Page.getSymbol(page) != null) {
					IAgeSymbol symbol = SymbolManager.getAgeSymbol(Page.getSymbol(page));
					if (symbol != null) displayname = symbol.displayName();
					if (displayname == null) displayname = Page.getSymbol(page);
				}
				if (displayname != null && searchtext != null && searchtext.length() > 0) {
					if (!displayname.toLowerCase().contains(searchtext.toLowerCase())) {
						page = null;
					}
				}
				if (positionable.count > 0) {
					GuiUtils.drawPage(mc.renderEngine, this.getZLevel(), page, pagexSize, pageySize, x + pageX, y + pageY, false);
				} else {
					GuiUtils.drawPage(mc.renderEngine, this.getZLevel(), null, pagexSize, pageySize, x + pageX, y + pageY, false);
				}
				if (positionable.count > 1) {
					GuiUtils.drawScaledText("" + positionable.count, (int) (x + pageX), (int) (y + pageY + pageHeight - 7), 20, 10, 0xFFFFFF);
				}
				if (mouseOverPageArea && GuiUtils.contains(mouseX, mouseY, (int) (x + pageX), (int) (y + pageY), (int) pagexSize, (int) pageySize)) {
					hoverpage = positionable;
					Page.getTooltip(page, hovertext);
					if (displayname != null) {
						hovertext.add(displayname);
					} else if (Page.getSymbol(page) != null) {
						IAgeSymbol symbol = SymbolManager.getAgeSymbol(Page.getSymbol(page));
						if (symbol != null) hovertext.add(symbol.displayName());
					}
				}
			}
		}
		scrollbar.setMaxScroll(maxScroll);
		GL11.glPopMatrix();
		GL11.glDepthFunc(GL11.GL_LEQUAL);
		this.setZLevel(2.0F);
	}

	private List<PositionableItem> getPages() {
		if (pagesProvider == null) { return null; }
		return this.pagesProvider.getPositionedPages();
	}

	@Override
	public void onTextChange(GuiElementTextField caller, String text) {
		this.searchtext = text;
	}
}
