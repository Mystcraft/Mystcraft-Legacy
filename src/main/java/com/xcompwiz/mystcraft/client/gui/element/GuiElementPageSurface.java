package com.xcompwiz.mystcraft.client.gui.element;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import com.xcompwiz.mystcraft.api.symbol.IAgeSymbol;
import com.xcompwiz.mystcraft.client.gui.GuiUtils;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementTextField.IGuiOnTextChange;
import com.xcompwiz.mystcraft.page.Page;
import com.xcompwiz.mystcraft.symbol.SymbolManager;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

@SideOnly(Side.CLIENT)
public class GuiElementPageSurface extends GuiElement implements IGuiOnTextChange {

	public static class PositionableItem {

		public int slotId;
		@Nonnull
		public ItemStack itemstack = ItemStack.EMPTY;
		public float x;
		public float y;

		public int count = 1;
	}

	public final static float pagewidth = 30;
	public final static float pageheight = pagewidth * 4 / 3;

	public interface IGuiPositionedPagesProvider {

		List<PositionableItem> getPositionedPages();

		void place(int index, boolean single);

		void pickup(PositionableItem hoverpage);

		void copy(PositionableItem hoverpage);

	}

	private IGuiPositionedPagesProvider pagesProvider;

	private float pageWidth;
	private float pageHeight;

	private PositionableItem hoverpage;
	private List<String> hovertext = new ArrayList<String>();

	private GuiElementVSlider scrollbar;

	private boolean mousedown;
	private String searchtext;
	private boolean mouseOverPageArea = false;

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
		if (!mouseOverPageArea)
			return;
		scrollbar.handleMouseScroll();
	}

	@Override
	public boolean _onMouseDown(int mouseX, int mouseY, int button) {
		if (this.contains(mouseX, mouseY)) {
			if (pagesProvider == null)
				return false;
			if (!mc.player.inventory.getItemStack().isEmpty()) {
				List<PositionableItem> pages = getPages();
				if (pages == null)
					return false;
				int index = pages.size();
				if (hoverpage != null) {
					index = hoverpage.slotId;
				}
				pagesProvider.place(index, button == 1);
				return true;
			}
			if (hoverpage != null && button == 2) {
				//TODO: (Surface) Pickblock on page -> clone
				//pagesProvider.clone(hoverpage);
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
		if (pagesProvider != null && GuiUtils.contains(i, j, guiLeft, guiTop, xSize - 20, ySize) && hoverpage != null && k == 1 && mousedown) {
			pagesProvider.copy(hoverpage);
		}
		mousedown = false;
		return false;
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
		mouseOverPageArea = GuiUtils.contains(mouseX, mouseY, guiLeft, guiTop, xSize - 20, ySize);

		int color = 0xAA000000;
		drawRect(guiLeft, guiTop, guiLeft + xSize - 20, guiTop + ySize, color); // Back

		// Render pages
		GuiUtils.drawGradientRect(guiLeft, guiTop, guiLeft + xSize - 20, guiTop + ySize, 0x000000, 0x000000, this.getZLevel());
		GlStateManager.depthFunc(GL11.GL_GEQUAL);
		GuiUtils.startGlScissor(guiLeft, guiTop, xSize, ySize - 1);
		this.setZLevel(1.0F);
		GlStateManager.pushMatrix();
		
		boolean noHover = true;
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
				if (y + pageY < guiTop - pageHeight) {
					continue;
				}
				if (y + pageY > guiTop + ySize) {
					continue;
				}
				String displayname = getDisplayName(page);
				if (displayname != null && searchtext != null && searchtext.length() > 0) {
					if (!displayname.toLowerCase().contains(searchtext.toLowerCase())) {
						page = ItemStack.EMPTY;
					}
				}
				if (positionable.count > 0) {
					GuiUtils.drawPage(mc.renderEngine, this.getZLevel(), page, pagexSize, pageySize, x + pageX, y + pageY);
				} else {
					GuiUtils.drawPage(mc.renderEngine, this.getZLevel(), ItemStack.EMPTY, pagexSize, pageySize, x + pageX, y + pageY);
				}
				if (positionable.count > 1) {
					GuiUtils.drawScaledText("" + positionable.count, (int) (x + pageX), (int) (y + pageY + pageHeight - 7), 20, 10, 0xFFFFFF);
				}
				if (mouseOverPageArea) {
					if (testMouseOver(positionable, page, displayname, mouseX, mouseY, (int) (x + pageX), (int) (y + pageY), (int) pagexSize, (int) pageySize))
						noHover = false;
				}
			}
		}
		if (noHover) {
			hoverpage = null;
			hovertext.clear();
		}
		
		scrollbar.setMaxScroll(maxScroll);

		GlStateManager.popMatrix();
		GlStateManager.depthFunc(GL11.GL_LEQUAL);
		this.setZLevel(2.0F);
		GuiUtils.endGlScissor();
	}

	private String getDisplayName(ItemStack page) {
		ResourceLocation symbolRes = Page.getSymbol(page);
		if (symbolRes == null)
			return null;

		IAgeSymbol symbol = SymbolManager.getAgeSymbol(symbolRes);
		if (symbol != null) {
			return symbol.getLocalizedName();
		}
		return symbolRes.getResourcePath();
	}

	private boolean testMouseOver(PositionableItem positionable, ItemStack page, String displayname, int mouseX, int mouseY, int i, int j, int pagexSize, int pageySize) {
		if (!GuiUtils.contains(mouseX, mouseY, i, j, (int) pagexSize, (int) pageySize))
			return false;
		if (hoverpage == positionable)
			return true;

		hoverpage = positionable;
		hovertext.clear();

		Page.getTooltip(page, hovertext);
		if (displayname != null)
			hovertext.add(displayname);
		net.minecraftforge.event.ForgeEventFactory.onItemTooltip(page, this.mc.player, hovertext, ITooltipFlag.TooltipFlags.NORMAL);
		return true;
	}

	private List<PositionableItem> getPages() {
		if (pagesProvider == null) {
			return null;
		}
		return this.pagesProvider.getPositionedPages();
	}

	@Override
	public void onTextChange(GuiElementTextField caller, String text) {
		this.searchtext = text;
	}
}
