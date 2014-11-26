package com.xcompwiz.mystcraft.client.gui.element;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;

import com.xcompwiz.mystcraft.client.gui.GuiUtils;
import com.xcompwiz.mystcraft.item.ItemPage;
import com.xcompwiz.mystcraft.page.Page;
import com.xcompwiz.mystcraft.symbol.IAgeSymbol;
import com.xcompwiz.mystcraft.symbol.SymbolManager;

public class GuiElementPage extends GuiElement {
	public interface IGuiPageProvider {
		ItemStack getPageItemStack(GuiElementPage elem);
	}

	private IGuiPageProvider	provider;

	private List<String>		hovertext	= new ArrayList<String>();

	private float				xSizePage;
	private float				ySizePage;

	public GuiElementPage(IGuiPageProvider provider, int guiLeft, int guiTop, float xSize, float ySize) {
		super(guiLeft, guiTop, (int) xSize, (int) ySize);
		this.provider = provider;
		this.xSizePage = xSize;
		this.ySizePage = ySize;
	}

	@Override
	public boolean isVisible() {
		ItemStack target = provider.getPageItemStack(this);
		return super.isVisible() && target != null && target.getItem() instanceof ItemPage;
	}

	@Override
	public void _renderBackground(float f, int mouseX, int mouseY) {
		int guiLeft = getLeft();
		int guiTop = getTop();
		hovertext.clear();
		ItemStack target = provider.getPageItemStack(this);
		GuiUtils.drawPage(mc.renderEngine, getZLevel(), target, xSizePage, ySizePage, guiLeft, guiTop);
		if (GuiUtils.contains(mouseX, mouseY, guiLeft, guiTop, xSize, ySize) && Page.getSymbol(target) != null) {
			IAgeSymbol symbol = SymbolManager.getAgeSymbol(Page.getSymbol(target));
			if (symbol != null) {
				hovertext.add(GuiUtils.getHoverText(symbol));
			}
		}
	}

	@Override
	public List<String> _getTooltipInfo() {
		if (!hovertext.isEmpty()) {
			return hovertext;
		}
		return super._getTooltipInfo();
	}
}
