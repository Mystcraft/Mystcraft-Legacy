package com.xcompwiz.mystcraft.client.gui;

import java.util.List;

import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import org.lwjgl.opengl.GL11;

import com.xcompwiz.mystcraft.client.gui.element.GuiElementButton;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementButton.IGuiOnClickHandler;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementIcon;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementLabel;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementLabel.IGuiLabelDataProvider;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementShopItem;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementShopItem.IGuiShopHandler;
import com.xcompwiz.mystcraft.client.gui.element.data.GuiIconItemStack;
import com.xcompwiz.mystcraft.client.gui.element.data.GuiIconItemStack.IItemStackProvider;
import com.xcompwiz.mystcraft.data.Assets.GUIs;
import com.xcompwiz.mystcraft.data.ModItems;
import com.xcompwiz.mystcraft.inventory.ContainerVillagerShop;
import com.xcompwiz.mystcraft.network.MystcraftPacketHandler;
import com.xcompwiz.mystcraft.network.packet.MPacketGuiMessage;

public class GuiVillagerShop extends GuiContainerElements implements IGuiOnClickHandler, IItemStackProvider {

	public class EmeraldsHandler implements IGuiLabelDataProvider {

		@Override
		public String getText(GuiElementLabel caller) {
			return Integer.toString(container.getPlayerEmeralds());
		}

		@Override
		public List<String> getTooltip(GuiElementLabel caller) {
			return null;
		}

	}

	public class ShopHandler implements IGuiShopHandler {

		@Override
		public void onPurchase(GuiElementShopItem caller) {
			NBTTagCompound nbttagcompound = new NBTTagCompound();
			nbttagcompound.setInteger(ContainerVillagerShop.Messages.PurchaseItem, caller.getId());
			MystcraftPacketHandler.bus.sendToServer(MPacketGuiMessage.createPacket(mc.thePlayer.openContainer.windowId, nbttagcompound));
			container.processMessage(mc.thePlayer, nbttagcompound);
		}

		@Override
		public String getPriceText(GuiElementShopItem caller) {
			return Integer.toString(container.getShopItemPrice(caller.getId()));
		}

		@Override
		public ItemStack getItemstack(GuiElementShopItem caller) {
			return container.getShopItem(caller.getId());
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
		int labelY = 10;
		int labelX = 40;

		IGuiShopHandler shophandler = new ShopHandler();
		addElement(new GuiElementShopItem(shophandler, 0, slotleft, 4, shop_slot_width, shop_slot_height));
		slotleft += shop_slot_width + padding;
		addElement(new GuiElementShopItem(shophandler, 1, slotleft, 4, shop_slot_width, shop_slot_height));
		slotleft += shop_slot_width + padding;
		addElement(new GuiElementShopItem(shophandler, 2, slotleft, 4, shop_slot_width, shop_slot_height));

		addElement(new GuiElementIcon(new GuiIconItemStack(this, "booster"), 8, 9, 16, 16));
		GuiElementButton button = new GuiElementButton(this, "booster", 7, 27, 18, 18);
		button.setIcon(new GuiIconItemStack(this, "buybtnb"));
		button.setText("Buy");
		addElement(button);

		addElement(new GuiElementLabel(new EmeraldsHandler(), "emeralds", xSize - 4 - labelX, 81, labelX, labelY, 0x44000000, 0xFF88FF88));
		addElement(new GuiElementIcon(new GuiIconItemStack(new ItemStack(Items.emerald)), xSize - 4 - labelX - labelY, 81, labelY, labelY));
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

	@Override
	public ItemStack getItemStack(GuiIconItemStack caller) {
		if (caller.getId().equals("booster")) return new ItemStack(ModItems.booster, container.getBoosterCount());
		if (caller.getId().equals("buybtnb")) return new ItemStack(Items.emerald, container.getBoosterCost());
		return null;
	}
}
