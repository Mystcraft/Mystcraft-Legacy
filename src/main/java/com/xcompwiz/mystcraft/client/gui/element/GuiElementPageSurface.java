package com.xcompwiz.mystcraft.client.gui.element;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.xcompwiz.mystcraft.client.gui.GuiUtils;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementTextField.IGuiOnTextChange;
import com.xcompwiz.mystcraft.data.Assets.GUIs;
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

		void place(boolean single);

		void pickup(PositionableItem hoverpage);

		void copy(PositionableItem hoverpage);
	}

	private IGuiPositionedPagesProvider	pagesProvider;

	private float						pageWidth;
	private float						pageHeight;

	private PositionableItem			hoverpage;
	private List<String>				hovertext	= new ArrayList<String>();

	private float						currentScroll;
	private float						maxScroll;
	private boolean						wasClicking;
	private boolean						isScrolling;
	private boolean						mouseOver;
	private boolean						mousedown;

	private String						searchtext;

	public GuiElementPageSurface(IGuiPositionedPagesProvider pagesProvider, Minecraft mc, int left, int top, int width, int height) {
		super(left, top, width, height);
		this.mc = mc;
		this.pagesProvider = pagesProvider;
		pageWidth = GuiElementPageSurface.pagewidth;
		pageHeight = GuiElementPageSurface.pageheight;
		mousedown = false;
	}

	/**
	 * Handles mouse input.
	 */
	@Override
	public void handleMouseInput() {
		if (!mouseOver) return;
		int input = Mouse.getEventDWheel();

		if (input != 0) {
			if (input > 0) {
				input = 1;
			}
			if (input < 0) {
				input = -1;
			}

			this.currentScroll -= input * 20;

			if (this.currentScroll > maxScroll) {
				this.currentScroll = maxScroll;
			}
			if (this.currentScroll < 0) {
				this.currentScroll = 0;
			}
		}
	}

	@Override
	public boolean mouseClicked(int mouseX, int mouseY, int button) {
		if (GuiUtils.contains(mouseX, mouseY, guiLeft, guiTop, xSize, ySize)) {
			if (mc.thePlayer.inventory.getItemStack() != null) {
				pagesProvider.place(button == 1);
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
	public void mouseUp(int i, int j, int k) {
		if (GuiUtils.contains(i, j, guiLeft, guiTop, xSize - 20, ySize) && hoverpage != null && k == 1 && mousedown) {
			pagesProvider.copy(hoverpage);
			mousedown = false;
		}
	}

	@Override
	public List<String> getTooltipInfo() {
		if (hovertext != null && hovertext.size() > 0) { return hovertext; }
		return super.getTooltipInfo();
	}

	@Override
	public void render(float f, int mouseX, int mouseY) {
		mouseOver = GuiUtils.contains(mouseX, mouseY, guiLeft, guiTop, xSize, ySize);
		boolean mouseOverPageArea = GuiUtils.contains(mouseX, mouseY, guiLeft, guiTop, xSize - 20, ySize);
		boolean isClicking = Mouse.isButtonDown(0);
		int sliderleft = guiLeft + xSize - 16;
		int slidertop = guiTop + 4;
		int sliderbottom = guiTop + ySize - 19;
		int sliderright = sliderleft + 14;
		float sliderpos = currentScroll / maxScroll;
		if (maxScroll == 0) currentScroll = 0;
		if (sliderpos > 1) sliderpos = 1;
		if (currentScroll == 0) sliderpos = 0;
		if (!this.wasClicking && isClicking && mouseX >= sliderleft && mouseY >= slidertop && mouseX < sliderright && mouseY < sliderbottom + 15) {
			this.isScrolling = true;
		}

		if (!isClicking) {
			this.isScrolling = false;
		}
		this.wasClicking = isClicking;
		if (this.isScrolling) {
			sliderpos = (mouseY - slidertop - 7.5F) / (sliderbottom - slidertop);

			if (sliderpos < 0.0F) {
				sliderpos = 0.0F;
			}

			if (sliderpos > 1.0F) {
				sliderpos = 1.0F;
			}

			this.currentScroll = (int) (sliderpos * maxScroll);
		}

		hovertext.clear();
		hoverpage = null;
		int color = 0xAA000000;
		drawRect(guiLeft, guiTop, guiLeft + xSize - 20, guiTop + ySize, color); // Back

		mc.renderEngine.bindTexture(GUIs.desk);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.drawTexturedModalRect(guiLeft + xSize - 21, guiTop, xSize, 0, 21, ySize);
		this.drawTexturedModalRect(sliderleft, slidertop + (int) ((sliderbottom - slidertop) * sliderpos), xSize + 21, 0, 12, 15);

		// Render pages
		this.drawGradientRect(this.guiLeft, this.guiTop, this.guiLeft + xSize - 20, this.guiTop + ySize, 0x000000, 0x000000);
		GL11.glDepthFunc(GL11.GL_GREATER);
		this.zLevel = 20.0F;
		GL11.glPushMatrix();
		maxScroll = 0;
		List<PositionableItem> pages = getPages();
		//XXX: (PageRender) If these were rendered as individual sub-elements, we could do some fancy things, like making them appear to move on sort (animated sort)
		if (pages != null) {
			float x = guiLeft;
			float y = guiTop - currentScroll;
			float pagexSize = pageWidth;
			float pageySize = pageHeight;
			for (PositionableItem positionable : pages) {
				ItemStack page = positionable.itemstack;
				float pageX = positionable.x;
				float pageY = positionable.y;
				if (pageY + pageHeight - ySize / 2 > maxScroll) {
					maxScroll = pageY + pageHeight - ySize / 2;
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
					GuiUtils.drawPage(mc.renderEngine, zLevel, page, pagexSize, pageySize, x + pageX, y + pageY, false);
				} else {
					GuiUtils.drawPage(mc.renderEngine, zLevel, null, pagexSize, pageySize, x + pageX, y + pageY, false);
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
		GL11.glPopMatrix();
		GL11.glDepthFunc(GL11.GL_LEQUAL);
		this.zLevel = 30.0F;
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
