package com.xcompwiz.mystcraft.client.gui.element;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.xcompwiz.mystcraft.api.client.ILinkPanelEffect;
import com.xcompwiz.mystcraft.api.linking.ILinkInfo;
import com.xcompwiz.mystcraft.api.symbol.IAgeSymbol;
import com.xcompwiz.mystcraft.client.gui.GuiUtils;
import com.xcompwiz.mystcraft.client.linkeffects.LinkPanelEffectManager;
import com.xcompwiz.mystcraft.data.Assets.GUIs;
import com.xcompwiz.mystcraft.data.ModItems;
import com.xcompwiz.mystcraft.inventory.IBookContainer;
import com.xcompwiz.mystcraft.page.Page;
import com.xcompwiz.mystcraft.symbol.SymbolManager;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiElementBook extends GuiElement {
	public interface IGuiOnLinkHandler {
		public void onLink(GuiElement elem);
	}

	private IBookContainer		bookcontainer;
	private IGuiOnLinkHandler	linkhandler;
	private List<String>		hovertext	= new ArrayList<String>();
	private float				xScale;
	private float				yScale;

	public GuiElementBook(IBookContainer container, IGuiOnLinkHandler linkhandler, int left, int top, int width, int height) {
		super(left, top, width, height);
		this.bookcontainer = container;
		this.linkhandler = linkhandler;
		this.xScale = xSize / 327.0F;
		this.yScale = ySize / 199.0F;
		bookcontainer.setCurrentPageIndex(0);
		Collection<ILinkPanelEffect> effects = LinkPanelEffectManager.getEffects();
		for (ILinkPanelEffect effect : effects) {
			effect.onOpen();
		}
	}

	@Override
	public boolean isEnabled() {
		return super.isEnabled() && isBook();
	}

	@Override
	public boolean isVisible() {
		return super.isVisible() && isBook();
	}

	@Override
	public boolean _onMouseDown(int i, int j, int k) {
		if (this.isEnabled() == false) { return false; }
		if (k == 0) {
			int guiLeft = getLeft();
			int guiTop = getTop();
			if (getCurrentPageIndex() == 0 && i >= 173 * xScale + guiLeft && i <= 305 * xScale + guiLeft && j >= 20 * yScale + guiTop && j <= 103 * yScale + guiTop) {
				linkhandler.onLink(this);
				return true;
			} else if (i >= guiLeft && i <= 156 * xScale + guiLeft && j >= guiTop && j <= 195 * yScale + guiTop) {
				pageLeft();
				return true;
			} else if (i >= 158 * xScale + guiLeft && i <= 312 * xScale + guiLeft && j >= guiTop && j <= 195 * yScale + guiTop) {
				pageRight();
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean _onKeyPress(char c, int i) {
		if (this.isEnabled() == false) { return false; }
		if (i == Keyboard.KEY_LEFT || i == mc.gameSettings.keyBindLeft.getKeyCode()) {
			pageLeft();
			return true;
		}
		if (i == Keyboard.KEY_RIGHT || i == mc.gameSettings.keyBindRight.getKeyCode()) {
			pageRight();
			return true;
		}
		return false;
	}

	@Override
	public List<String> _getTooltipInfo() {
		if (hovertext.size() > 0) { return hovertext; }
		return null;
	}

	@Override
	public void _renderBackground(float f, int mouseX, int mouseY) {
		if (this.isVisible() == false) { return; }
		int guiLeft = getLeft();
		int guiTop = getTop();
		GL11.glPushMatrix();
		GL11.glTranslatef(guiLeft, guiTop, 0);
		GL11.glScalef(xScale, yScale, 1);
		hovertext.clear();
		//TODO: (PageRender) When redoing book/link item renders, might want to consider restructuring the texture sheets used for drawing here
		mc.renderEngine.bindTexture(GUIs.book_cover); // book backing
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		drawTexturedModalRect(0, 7, 152, 0, 34, 192); // Left border
		drawTexturedModalRect(34, 7, 49, 0, 103, 192); // Left panel
		drawTexturedModalRect(137, 7, 45, 0, 4, 192); // Left panel
		drawTexturedModalRect(141, 7, 0, 0, 186, 192); // Spine + right
		if (isAgebook()) { // XXX: (PageRender) Better gui spine colorization handling
			drawTexturedModalRect(0, 7, 186, 0, 34, 192); // Gold border
			drawTexturedModalRect(293, 7, 186, 0, 34, 192); // Gold border
		}
		if (getCurrentPageIndex() > 0) {
			mc.renderEngine.bindTexture(GUIs.book_page_left); // Left Page
			drawTexturedModalRect(7, 0, 0, 0, 156, 195);
		}

		//XXX: (PageRender) Revise how pages are rendered to improve plugability and clean this up
		ItemStack page = bookcontainer.getCurrentPage();
		if (page != null && Page.isLinkPanel(page)) { // Render link panel
			// Render Panel
			GL11.glPushMatrix(); // Panel matrix
			GL11.glTranslatef(173, 20, 0);

			drawLinkPanel(0, 0, 132, 83);
			drawLinkPanelOverlays(132, 83);

			GL11.glPopMatrix(); // Panel matrix

			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			mc.renderEngine.bindTexture(GUIs.book_page_right); // Right Page w/ panel
			drawTexturedModalRect(163, 0, 0, 0, 156, 195);
		} else if (page != null) {
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			mc.renderEngine.bindTexture(GUIs.book_page_right_solid); // Full Right Page
			drawTexturedModalRect(163, 0, 0, 0, 156, 195);

			if (Page.getSymbol(page) != null) {
				int x = 171;
				int y = 25;
				int scale = 140;
				IAgeSymbol symbol = SymbolManager.getAgeSymbol(Page.getSymbol(page));
				GuiUtils.drawSymbol(mc.renderEngine, getZLevel(), symbol, scale, x, y);
				if (GuiUtils.contains(mouseX, mouseY, (int) (x * xScale) + guiLeft, (int) (y * yScale) + guiTop, (int) (scale * xScale), (int) (scale * yScale))) {
					hovertext.add(GuiUtils.getHoverText(symbol));
				}
			}
		}
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		if (getCurrentPageIndex() == 0) {
			if (isSlotVisible()) { // Draw slot
				drawTexturedModalRect(40, 20, 156, 0, 18, 18);
			}
			Collection<String> authors = bookcontainer.getBookAuthors();
			mc.fontRenderer.drawString(bookcontainer.getBookTitle(), 40, 40, 0x000000);
			int y = 50;
			if (authors != null) for (String author : authors) {
				GL11.glPushMatrix();
				GL11.glTranslatef(50, y, 0);
				GL11.glScalef(0.5F, 0.5F, 1);
				mc.fontRenderer.drawString(author, 0, 0, 0x000000);
				GL11.glPopMatrix();
				y += 5;
			}
		}
		String s = "" + (getCurrentPageIndex()) + "/" + (bookcontainer.getPageCount());
		int j = mc.fontRenderer.getStringWidth(s) / 2;
		mc.fontRenderer.drawString(s, 165 - j, 0 + 185, 0x000000);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glPopMatrix();
	}

	// XXX: (PageRender) Make link panel its own element?
	private void drawLinkPanel(int x, int y, int width, int height) {
		if (bookcontainer.isTargetWorldVisited()) {
			drawGradientRect(x, y, width, height, 0xFF000044, 0xFF006666);
		} else {
			drawGradientRect(x, y, width, height, 0xFF000000, 0xFF000000);
		}
	}

	private void drawLinkPanelOverlays(int width, int height) {
		ILinkInfo linkinfo = bookcontainer.getLinkInfo();
		ItemStack bookclone = bookcontainer.getBook();
		if (bookclone != null) bookclone = bookclone.copy();

		if (linkinfo != null) {
			Collection<ILinkPanelEffect> effects = LinkPanelEffectManager.getEffects();
			for (ILinkPanelEffect effect : effects) {
				effect.render(0, 0, width, height, linkinfo, bookclone); // TODO: (Visuals) zLevels
			}
		}
		if (!bookcontainer.isLinkPermitted()) {
			drawGradientRect(0, 0, width, height, 0xBB888888, 0xBB888888);
		}
	}

	private void pageLeft() {
		int currentpage = getCurrentPageIndex() - 1;
		if (currentpage < 0) currentpage = 0;
		bookcontainer.setCurrentPageIndex(currentpage);
	}

	private void pageRight() {
		int currentpage = getCurrentPageIndex() + 1;
		if (currentpage > bookcontainer.getPageCount()) currentpage = bookcontainer.getPageCount();
		bookcontainer.setCurrentPageIndex(currentpage);
	}

	private int getCurrentPageIndex() {
		return bookcontainer.getCurrentPageIndex();
	}

	// XXX: (GuiElementItemSlot) This is kind of why I want a GUI-based slot system
	private boolean isSlotVisible() {
		return bookcontainer.hasBookSlot();
	}

	private boolean isBook() {
		return bookcontainer.getBook() != null;
	}

	private boolean isAgebook() {
		ItemStack book = bookcontainer.getBook();
		if (book == null) return false;
		if (book.stackTagCompound == null) return false;
		if (book.getItem() == ModItems.agebook) return true;
		return false;
	}
}
