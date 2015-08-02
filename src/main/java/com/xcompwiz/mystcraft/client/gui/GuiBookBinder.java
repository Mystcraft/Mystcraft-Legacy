package com.xcompwiz.mystcraft.client.gui;

import java.util.List;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

import com.xcompwiz.mystcraft.client.gui.element.GuiElementScrollablePages;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementScrollablePages.IGuiPageListProvider;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementScrollablePages.IGuiScrollableClickHandler;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementTextField;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementTextField.IGuiOnTextChange;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementTextField.IGuiTextProvider;
import com.xcompwiz.mystcraft.data.Assets.GUIs;
import com.xcompwiz.mystcraft.inventory.ContainerBookBinder;
import com.xcompwiz.mystcraft.network.MystcraftPacketHandler;
import com.xcompwiz.mystcraft.network.packet.MPacketGuiMessage;
import com.xcompwiz.mystcraft.tileentity.TileEntityBookBinder;

public class GuiBookBinder extends GuiContainerElements {

	public class PageListHandler implements IGuiPageListProvider, IGuiScrollableClickHandler {

		@Override
		public List<ItemStack> getPageList() {
			return container.getPageList();
		}

		@Override
		public void onItemPlace(GuiElementScrollablePages guiElementScrollablePages, int index, int mousebutton) {
			// Inform server container to remove the page from the 'hand' and put it in the page container at index
			NBTTagCompound nbttagcompound = new NBTTagCompound();
			nbttagcompound.setInteger(ContainerBookBinder.Messages.InsertHeldAt, index);
			nbttagcompound.setBoolean("Single", (mousebutton == 1));
			MystcraftPacketHandler.bus.sendToServer(MPacketGuiMessage.createPacket(mc.thePlayer.openContainer.windowId, nbttagcompound));
			container.processMessage(mc.thePlayer, nbttagcompound);
		}

		@Override
		public void onItemRemove(GuiElementScrollablePages guiElementScrollablePages, int clickedpage) {
			NBTTagCompound nbttagcompound = new NBTTagCompound();
			nbttagcompound.setInteger(ContainerBookBinder.Messages.TakeFromSlider, clickedpage);
			MystcraftPacketHandler.bus.sendToServer(MPacketGuiMessage.createPacket(mc.thePlayer.openContainer.windowId, nbttagcompound));
		}
	}

	public class TextBoxHandler implements IGuiTextProvider, IGuiOnTextChange {
		@Override
		public String getText(GuiElementTextField caller) {
			return container.getPendingTitle();
		}

		@Override
		public void onTextChange(GuiElementTextField caller, String text) {
			NBTTagCompound nbttagcompound = new NBTTagCompound();
			nbttagcompound.setString(ContainerBookBinder.Messages.SetTitle, text);
			MystcraftPacketHandler.bus.sendToServer(MPacketGuiMessage.createPacket(container.windowId, nbttagcompound));
			container.processMessage(mc.thePlayer, nbttagcompound);
		}
	}

	private ContainerBookBinder	container;

	private GuiElementTextField	txtBookName;
	private int					errorcolor	= 0xFFFF0000;
	private int					txtcolor	= 0xFFA0A0A0;

	public GuiBookBinder(InventoryPlayer inventoryplayer, TileEntityBookBinder tileentity) {
		super(new ContainerBookBinder(inventoryplayer, tileentity));
		this.container = (ContainerBookBinder) this.inventorySlots;
		guiLeft = 0;
	}

	@Override
	public void validate() {
		xSize = 176;
		ySize = 181;
		guiLeft = (width - xSize) / 2;
		guiTop = (height - ySize) / 2;
		TextBoxHandler txtbxhdnlr = new TextBoxHandler();
		txtBookName = new GuiElementTextField(txtbxhdnlr, txtbxhdnlr, "ItemName", 7, 9, xSize - 60, 14);
		txtBookName.setMaxLength(21);
		addElement(txtBookName);
		PageListHandler pagelistHandler = new PageListHandler();
		addElement(new GuiElementScrollablePages(pagelistHandler, pagelistHandler, mc, 7, 45, xSize - 14, 40));
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		txtBookName.setBorderColor(txtBookName.getText().equals("") ? errorcolor : txtcolor);
		this.container.updateCraftResult();
	}

	@Override
	protected void _drawBackgroundLayer(int mouseX, int mouseY, float f) {
		mc.renderEngine.bindTexture(GUIs.binder);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), guiLeft + 8, guiTop + (ySize - 96) + 2, 0x404040);
	}
}