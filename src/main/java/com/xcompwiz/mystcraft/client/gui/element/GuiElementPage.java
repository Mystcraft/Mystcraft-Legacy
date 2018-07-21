package com.xcompwiz.mystcraft.client.gui.element;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.xcompwiz.mystcraft.api.symbol.IAgeSymbol;
import com.xcompwiz.mystcraft.client.gui.GuiUtils;
import com.xcompwiz.mystcraft.item.ItemPage;
import com.xcompwiz.mystcraft.page.Page;
import com.xcompwiz.mystcraft.symbol.SymbolManager;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class GuiElementPage extends GuiElement {

	public interface IGuiPageProvider {

		@Nonnull
		ItemStack getPageItemStack(GuiElementPage elem);

		void interact(GuiElementPage elem);

	}

	private IGuiPageProvider provider;

	private List<String> hovertext = new ArrayList<>();

	private float xSizePage;
	private float ySizePage;

	public GuiElementPage(IGuiPageProvider provider, int guiLeft, int guiTop, float xSize, float ySize) {
		super(guiLeft, guiTop, (int) xSize, (int) ySize);
		this.provider = provider;
		this.xSizePage = xSize;
		this.ySizePage = ySize;
	}

	@Override
	public boolean isVisible() {
		ItemStack target = provider.getPageItemStack(this);
		return super.isVisible() && !target.isEmpty() && target.getItem() instanceof ItemPage;
	}
	
	@Override
	protected boolean _onMouseDown(int mouseX, int mouseY, int button) {
		if (this.contains(mouseX, mouseY)) {
			provider.interact(this);
			return true;
		}
		return false;
	}

	@Override
	public void _renderBackground(float f, int mouseX, int mouseY) {
		int guiLeft = getLeft();
		int guiTop = getTop();
		ItemStack target = provider.getPageItemStack(this);
		ResourceLocation symbolRes = Page.getSymbol(target);
		GuiUtils.drawPage(mc.renderEngine, getZLevel(), target, xSizePage, ySizePage, guiLeft, guiTop);
		if (GuiUtils.contains(mouseX, mouseY, guiLeft, guiTop, xSize, ySize) && symbolRes != null) {
			if (hovertext.isEmpty()) {
				IAgeSymbol symbol = SymbolManager.getAgeSymbol(symbolRes);
				if (symbol != null) {
					hovertext.add(symbol.getLocalizedName());
					net.minecraftforge.event.ForgeEventFactory.onItemTooltip(target, this.mc.player, hovertext, ITooltipFlag.TooltipFlags.NORMAL);
				}
			}
		} else {
			hovertext.clear();
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
