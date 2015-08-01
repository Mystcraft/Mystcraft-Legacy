package com.xcompwiz.mystcraft.client.gui;

import java.util.Random;

import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import org.lwjgl.opengl.GL11;

import com.xcompwiz.mystcraft.api.symbol.IAgeSymbol;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementButton;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementButton.IGuiOnClickHandler;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementIcon;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementShopItem;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementShopItem.IGuiShopHandler;
import com.xcompwiz.mystcraft.data.Assets.GUIs;
import com.xcompwiz.mystcraft.data.ModItems;
import com.xcompwiz.mystcraft.inventory.ContainerVillagerShop;
import com.xcompwiz.mystcraft.network.MystcraftPacketHandler;
import com.xcompwiz.mystcraft.network.packet.MPacketGuiMessage;
import com.xcompwiz.mystcraft.page.Page;
import com.xcompwiz.mystcraft.symbol.SymbolManager;
import com.xcompwiz.mystcraft.treasure.WeightProviderSymbolItem;
import com.xcompwiz.mystcraft.utility.WeightedItemSelector;
import com.xcompwiz.mystcraft.villager.VillagerTradeSystem;

public class GuiVillagerShop extends GuiContainerElements implements IGuiOnClickHandler {

	public class ShopHandler implements IGuiShopHandler {

		private ItemStack	pageitems[] = new ItemStack[3];

		@Override
		public void onPurchase(GuiElementShopItem caller) {
			// TODO Auto-generated method stub

		}

		@Override
		public String getPriceText(GuiElementShopItem caller) {
			ItemStack itemstack = this.getItemstack(caller);
			return Integer.toString(VillagerTradeSystem.getCardCost(itemstack));
		}

		@Override
		public ItemStack getItemstack(GuiElementShopItem caller) {
			if (this.pageitems[caller.getId()] == null) {
				IAgeSymbol symbol = WeightedItemSelector.getRandomItem(new Random(), SymbolManager.getSymbolsByRank(caller.getId()+1, null), WeightProviderSymbolItem.instance);
				this.pageitems[caller.getId()] = Page.createSymbolPage(symbol.identifier());
			}
			return this.pageitems[caller.getId()];
		}

	}

	private ContainerVillagerShop	container;

	public GuiVillagerShop(InventoryPlayer playerinv, EntityVillager entity) {
		super(new ContainerVillagerShop(playerinv, entity));
		this.container = (ContainerVillagerShop) this.inventorySlots;
	}

	@Override
	public void validate() {
		xSize = 176;
		ySize = 181;

		int slotleft = 28;
		int padding = 3;
		int shop_slot_width = (xSize-slotleft-padding-3)/3-1;
		int shop_slot_height = shop_slot_width * 3 / 2;

		IGuiShopHandler shophandler = new ShopHandler();
		addElement(new GuiElementShopItem(shophandler, 0, slotleft, 4, shop_slot_width, shop_slot_height));
		slotleft += shop_slot_width + padding;
		addElement(new GuiElementShopItem(shophandler, 1, slotleft, 4, shop_slot_width, shop_slot_height));
		slotleft += shop_slot_width + padding;
		addElement(new GuiElementShopItem(shophandler, 2, slotleft, 4, shop_slot_width, shop_slot_height));

		addElement(new GuiElementIcon(new ItemStack(ModItems.booster, 3), 8, 9, 16, 16)); //TODO: Dynamic display of boosters remaining
		GuiElementButton button = new GuiElementButton(this, "booster", 7, 27, 18, 18);
		button.setIcon(new ItemStack(Items.emerald, 10)); //TODO: Dynamic booster pack price
		button.setText("Buy");
		addElement(button);
	}

	@Override
	protected void _drawBackgroundLayer(int mouseX, int mouseY, float partial) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.renderEngine.bindTexture(GUIs.villagershop);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
	}

	@Override
	public void onClick(GuiElementButton caller) {
		if (caller.getId().equals("booster")) {
			NBTTagCompound nbttagcompound = new NBTTagCompound();
			nbttagcompound.setBoolean(ContainerVillagerShop.Messages.PurchaseBooster, true);
			MystcraftPacketHandler.bus.sendToServer(MPacketGuiMessage.createPacket(mc.thePlayer.openContainer.windowId, nbttagcompound));
			container.processMessage(mc.thePlayer, nbttagcompound);
		}
	}
}
