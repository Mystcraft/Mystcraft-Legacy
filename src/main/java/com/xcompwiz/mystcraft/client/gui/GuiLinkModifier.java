package com.xcompwiz.mystcraft.client.gui;

import java.util.List;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;

import org.lwjgl.opengl.GL11;

import com.xcompwiz.mystcraft.client.gui.element.GuiElement;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementButton;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementButton.IGuiOnClickHandler;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementButtonToggle;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementButtonToggle.IGuiStateProvider;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementTextField;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementTextField.IGuiOnTextChange;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementTextField.IGuiTextProvider;
import com.xcompwiz.mystcraft.data.Assets;
import com.xcompwiz.mystcraft.data.InkEffects;
import com.xcompwiz.mystcraft.inventory.ContainerLinkModifier;
import com.xcompwiz.mystcraft.network.MPacketGuiMessage;
import com.xcompwiz.mystcraft.network.MystcraftPacketHandler;
import com.xcompwiz.mystcraft.tileentity.TileEntityLinkModifier;
import com.xcompwiz.util.CollectionUtils;

public class GuiLinkModifier extends GuiContainerMyst {
	private class ButtonHandler implements IGuiOnClickHandler, IGuiStateProvider {
		@Override
		public boolean getState(String id) {
			return container.getLinkFlag(id);
		}

		@Override
		public void onClick(GuiElementButton guiElementButton) {
			String id = guiElementButton.getId();
			NBTTagCompound nbttagcompound = new NBTTagCompound();
			nbttagcompound.setString("SetFlag", id);
			nbttagcompound.setBoolean("Value", !getState(id));
			MystcraftPacketHandler.bus.sendToServer(MPacketGuiMessage.createPacket(mc.thePlayer.openContainer.windowId, nbttagcompound));
			container.processMessage(mc.thePlayer, nbttagcompound);
		}
	}

	public class TextBoxHandler implements IGuiTextProvider, IGuiOnTextChange {
		@Override
		public String getText(GuiElementTextField caller) {
			return container.getBookTitle();
		}

		@Override
		public void onTextChange(GuiElementTextField caller, String text) {
			NBTTagCompound nbttagcompound = new NBTTagCompound();
			nbttagcompound.setString("SetTitle", text);
			MystcraftPacketHandler.bus.sendToServer(MPacketGuiMessage.createPacket(container.windowId, nbttagcompound));
			container.processMessage(mc.thePlayer, nbttagcompound);
		}
	}

	private ContainerLinkModifier	container;

	public GuiLinkModifier(InventoryPlayer inventoryplayer, TileEntityLinkModifier tileentity) {
		super(new ContainerLinkModifier(inventoryplayer, tileentity));
		this.container = (ContainerLinkModifier) this.inventorySlots;
	}

	@Override
	public void validate() {
		TextBoxHandler txtbxhdnlr = new TextBoxHandler();
		elements.add(new GuiElementTextField(txtbxhdnlr, txtbxhdnlr, guiLeft + 80, guiTop + 56, xSize - 80 - 9, 14));

		ButtonHandler buttonhandler = new ButtonHandler();
		int x = 5, y = 10;
		int scale = 18;
		for (String prop : InkEffects.getProperties()) {
			elements.add(createButton(buttonhandler, prop, CollectionUtils.buildList(InkEffects.getLocalizedName(prop)), this.guiLeft + x, this.guiTop + y, scale));
			y += scale + 2;
			if (y >= 60) {
				y = 10;
				x += scale + 2;
			}
		}
	}

	private <T extends IGuiOnClickHandler & IGuiStateProvider> GuiElement createButton(T buttonhandler, String id, List<String> tooltip, int x, int y, int size) {
		GuiElementButtonToggle button = new GuiElementButtonToggle(buttonhandler, buttonhandler, id, x, y, size, size);
		button.setTooltip(tooltip);
		return button;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY) {
		this.mc.renderEngine.bindTexture(Assets.gui_single_slot);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
		this.drawElementBackgrounds(f, mouseX, mouseY);

		this.fontRendererObj.drawStringWithShadow(container.getLinkDimensionUID(), guiLeft + 100, guiTop + 40, 0xFFFFFF);
	}
}
