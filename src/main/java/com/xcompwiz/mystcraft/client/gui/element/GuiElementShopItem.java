package com.xcompwiz.mystcraft.client.gui.element;

import java.util.List;

import com.xcompwiz.mystcraft.api.symbol.IAgeSymbol;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementButton.IGuiOnClickHandler;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementLabel.IGuiLabelDataProvider;
import com.xcompwiz.mystcraft.client.gui.element.data.GuiIconItemStack;
import com.xcompwiz.mystcraft.client.gui.element.data.GuiIconItemStack.IItemStackProvider;
import com.xcompwiz.mystcraft.item.ItemPage;
import com.xcompwiz.mystcraft.page.Page;
import com.xcompwiz.mystcraft.symbol.SymbolManager;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class GuiElementShopItem extends GuiElement implements IGuiOnClickHandler, IGuiLabelDataProvider, IItemStackProvider {

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
		float itemSizeX = xSize / 2;
		int paddingX = (int) (xSize - itemSizeX) / 2;
		int paddingY = (int) (panelY - itemSizeX) / 2;

		GuiElementPanel panel = new GuiElementPanel(0, 0, xSize, panelY);
		panel.setBackground(0xFF888888, 0xFF444444);
		addElement(panel);
		panel.addElement(new GuiElementIcon(new GuiIconItemStack(this, "itemstack"), paddingX, paddingY, (int) itemSizeX, (int) itemSizeX));
		addElement(new GuiElementLabel(this, "name", 0, ySize - buttonY - labelY - labelY, xSize, labelY, 0x7F000000, 0xFFDDDDDD));
		addElement(new GuiElementLabel(this, "price", 0, ySize - buttonY - labelY, xSize - labelY, labelY, 0x7F000000, 0xFF88FF88));
		addElement(new GuiElementIcon(new GuiIconItemStack(new ItemStack(Items.emerald)), xSize - labelY, ySize - buttonY - labelY, labelY, labelY));
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
	public ItemStack getItemStack(GuiIconItemStack caller) {
		return shop.getItemstack(this);
	}
}
