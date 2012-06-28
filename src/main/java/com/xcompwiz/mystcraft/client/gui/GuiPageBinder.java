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
import com.xcompwiz.mystcraft.data.Assets;
import com.xcompwiz.mystcraft.inventory.ContainerPageBinder;
import com.xcompwiz.mystcraft.network.MPacketGuiMessage;
import com.xcompwiz.mystcraft.network.MystcraftPacketHandler;
import com.xcompwiz.mystcraft.tileentity.TileEntityBookBinder;

public class GuiPageBinder extends GuiContainerMyst {

	public class PageListHandler implements IGuiPageListProvider, IGuiScrollableClickHandler {

		@Override
		public List<ItemStack> getPageList() {
			return container.getPageList();
		}

		@Override
		public void onItemPlace(GuiElementScrollablePages guiElementScrollablePages, int index, int mousebutton) {
			// Inform server container to remove the page from the 'hand' and put it in the page container at index
			NBTTagCompound nbttagcompound = new NBTTagCompound();
			nbttagcompound.setInteger("InsertHeldAt", index);
			nbttagcompound.setBoolean("Single", (mousebutton == 1));
			MystcraftPacketHandler.bus.sendToServer(MPacketGuiMessage.createPacket(mc.thePlayer.openContainer.windowId, nbttagcompound));
		}

		@Override
		public void onItemRemove(GuiElementScrollablePages guiElementScrollablePages, int clickedpage) {
			NBTTagCompound nbttagcompound = new NBTTagCompound();
			nbttagcompound.setInteger("TakeFromSlider", clickedpage);
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
			nbttagcompound.setString("SetTitle", text);
			MystcraftPacketHandler.bus.sendToServer(MPacketGuiMessage.createPacket(container.windowId, nbttagcompound));
			container.processMessage(mc.thePlayer, nbttagcompound);
		}
	}

	private ContainerPageBinder			container;

	public GuiPageBinder(InventoryPlayer inventoryplayer, TileEntityBookBinder tileentity) {
		super(new ContainerPageBinder(inventoryplayer, tileentity));
		this.container = (ContainerPageBinder) this.inventorySlots;
		guiLeft = 0;
	}

	@Override
	public void validate() {
		xSize = 176;
		ySize = 181;
		guiLeft = (width - xSize) / 2;
		guiTop = (height - ySize) / 2;
		TextBoxHandler txtbxhdnlr = new TextBoxHandler();
		elements.add(new GuiElementTextField(txtbxhdnlr, txtbxhdnlr, guiLeft + 7, guiTop + 9, xSize - 60, 14));
		PageListHandler pagelistHandler = new PageListHandler();
		elements.add(new GuiElementScrollablePages(pagelistHandler, pagelistHandler , mc, guiLeft + 7, guiTop + 45, xSize - 14, 40));
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		this.container.updateCraftResult();
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY) {
		mc.renderEngine.bindTexture(Assets.gui_binder);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), guiLeft + 8, guiTop + (ySize - 96) + 2, 0x404040);
		this.drawElementBackgrounds(f, mouseX, mouseY);
	}
}
