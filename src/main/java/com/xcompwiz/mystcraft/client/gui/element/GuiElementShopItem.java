package com.xcompwiz.mystcraft.client.gui.element;

import java.util.List;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import com.xcompwiz.mystcraft.api.symbol.IAgeSymbol;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementButton.IGuiOnClickHandler;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementLabel.IGuiLabelDataProvider;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementPage.IGuiPageProvider;
import com.xcompwiz.mystcraft.item.ItemPage;
import com.xcompwiz.mystcraft.page.Page;
import com.xcompwiz.mystcraft.symbol.SymbolManager;

public class GuiElementShopItem extends GuiElement implements IGuiOnClickHandler, IGuiLabelDataProvider, IGuiPageProvider {

	public interface IGuiShopHandler {
		public void onPurchase(GuiElementShopItem caller);

		public String getPriceText(GuiElementShopItem caller);

		public ItemStack getItemstack(GuiElementShopItem caller);
	}

	private IGuiShopHandler	shop;
	private int				id;

	public GuiElementShopItem(IGuiShopHandler shop, int id, int guiLeft, int guiTop, int xSize, int ySize) {
		super(guiLeft, guiTop, xSize, ySize);
		this.shop = shop;
		this.id = id;

		int buttonY = 10;
		int labelY = 10;
		int panelY = ySize - buttonY - labelY - labelY;
		float pageSizeX = xSize / 2;
		float pageSizeY = panelY * 3.F / 5.F;
		int paddingX = (int) (xSize - pageSizeX) / 2;
		int paddingY = (int) (panelY - pageSizeY) / 2;

		GuiElementPanel panel = new GuiElementPanel(0, 0, xSize, panelY);
		panel.setBackground(0xFF888888, 0xFF444444);
		addElement(panel);
		//TODO: Handle non-page shop items
		GuiElement currentitem = new GuiElementIcon(null, paddingX, (int) (panelY - pageSizeX) / 2, (int) pageSizeX, (int) pageSizeX);
		panel.addElement(currentitem);
		panel.addElement(new GuiElementPage(this, paddingX, paddingY, pageSizeX, pageSizeY));
		addElement(new GuiElementLabel(this, "name", 0, ySize - buttonY - labelY - labelY, xSize, labelY, 0x7F000000, 0xFFDDDDDD));
		addElement(new GuiElementLabel(this, "price", 0, ySize - buttonY - labelY, xSize - labelY, labelY, 0x7F000000, 0xFF88FF88));
		addElement(new GuiElementIcon(new ItemStack(Items.emerald), xSize - labelY, ySize - buttonY - labelY, labelY, labelY));
		addElement(createButton(this, "Buy", 0, ySize - buttonY, xSize, buttonY));
	}

	private GuiElement createButton(IGuiOnClickHandler eventhandler, String id, int guiLeft, int guiTop, int width, int height) {
		GuiElementButton button = new GuiElementButton(eventhandler, id, guiLeft, guiTop, width, height);
		button.setText(id);
		return button;
	}

	public int getId() {
		return id;
	}

	@Override
	public void onClick(GuiElementButton caller) {
		if (caller.getId().equals("Buy")) {
			shop.onPurchase(this);
		}
	}

	@Override
	public String getText(GuiElementLabel caller) {
		if (caller.getId().equals("price")) {
			return shop.getPriceText(this);
		} else if (caller.getId().equals("name")) {
			ItemStack itemstack = shop.getItemstack(this);
			if (itemstack == null) return "";
			if (itemstack.getItem() instanceof ItemPage) {
				String symbolId = Page.getSymbol(itemstack);
				IAgeSymbol symbol = SymbolManager.getAgeSymbol(symbolId);
				if (symbol == null) return "Unknown: " + symbolId;
				return symbol.displayName();
			}
			return itemstack.getDisplayName();
		}
		return "???";
	}

	@Override
	public List<String> getTooltip(GuiElementLabel caller) {
		return null;
	}

	@Override
	public ItemStack getPageItemStack(GuiElementPage elem) {
		ItemStack itemstack = shop.getItemstack(this);
		if (itemstack == null) return null;
		if (itemstack.getItem() instanceof ItemPage) return itemstack;
		return null;
	}
}
