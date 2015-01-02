package com.xcompwiz.mystcraft.client.gui;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;

import org.lwjgl.opengl.GL11;

import com.xcompwiz.mystcraft.client.gui.element.GuiElement;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementBook;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementBook.IGuiOnLinkHandler;
import com.xcompwiz.mystcraft.data.Assets.GUIs;
import com.xcompwiz.mystcraft.entity.EntityLinkbook;
import com.xcompwiz.mystcraft.inventory.ContainerBook;
import com.xcompwiz.mystcraft.network.MystcraftPacketHandler;
import com.xcompwiz.mystcraft.network.packet.MPacketGuiMessage;
import com.xcompwiz.mystcraft.tileentity.TileEntityBookDisplay;

public class GuiBook extends GuiContainerElements implements IGuiOnLinkHandler {

	private ContainerBook	container;
	private boolean			widget;
	private GuiElementBook	bookelem;

	private GuiBook(ContainerBook container) {
		super(container);
		this.container = container;
	}

	public GuiBook(InventoryPlayer inventoryplayer, TileEntityBookDisplay tileentity) {
		this(new ContainerBook(inventoryplayer, tileentity));
	}

	public GuiBook(InventoryPlayer inventoryplayer, byte slot) {
		this(new ContainerBook(inventoryplayer, slot));
	}

	public GuiBook(InventoryPlayer inventoryplayer, EntityLinkbook entity) {
		this(new ContainerBook(inventoryplayer, entity.inventory));
	}

	@Override
	public void onLink(GuiElement elem) {
		NBTTagCompound nbttagcompound = new NBTTagCompound();
		nbttagcompound.setByte("Link", (byte) 0);
		MystcraftPacketHandler.bus.sendToServer(MPacketGuiMessage.createPacket(container.windowId, nbttagcompound));
	}

	@Override
	public void validate() {
		xSize = 327;
		ySize = 199;
		guiLeft = (width - xSize) / 2;
		guiTop = (height - ySize) / 2;
		bookelem = new GuiElementBook(container, this, 0, 0, xSize, ySize);
		addElement(bookelem);
		recalcPosition();
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		container.updateSlots();
		recalcPosition();
	}

	private void recalcPosition() {
		xSize = 176;
		ySize = 166;
		widget = false;
		if (bookelem.isVisible()) {
			xSize = bookelem.getWidth();
			ySize = bookelem.getHeight();
			widget = true;
		}
		guiLeft = (width - xSize) / 2;
		guiTop = (height - ySize) / 2;
	}

	@Override
	protected void _drawBackgroundLayer(int mouseX, int mouseY, float f) {
		if (!widget) {
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.mc.getTextureManager().bindTexture(GUIs.slot);
			this.drawTexturedModalRect(guiLeft, guiTop, 0, 0, this.xSize, this.ySize);

			if (!isSlotVisible()) { // XXX: (GuiElementItemSlot)Replace with renderable slot? Replace slot system entirely?
				this.drawTexturedModalRect(guiLeft + 79, guiTop + 34, 10, 10, 18, 18);
			}
		}
	}

	private boolean isSlotVisible() {
		return container.getInventorySize() != 0;
	}
}
