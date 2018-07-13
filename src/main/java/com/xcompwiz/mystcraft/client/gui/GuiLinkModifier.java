package com.xcompwiz.mystcraft.client.gui;

import java.util.List;

import com.xcompwiz.mystcraft.client.gui.element.GuiElement;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementButton;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementButton.IGuiOnClickHandler;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementButtonToggle;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementButtonToggle.IGuiStateProvider;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementTextField;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementTextField.IGuiOnTextChange;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementTextField.IGuiTextProvider;
import com.xcompwiz.mystcraft.data.Assets.GUIs;
import com.xcompwiz.mystcraft.data.InkEffects;
import com.xcompwiz.mystcraft.inventory.ContainerLinkModifier;
import com.xcompwiz.mystcraft.network.MystcraftPacketHandler;
import com.xcompwiz.mystcraft.network.packet.MPacketGuiMessage;
import com.xcompwiz.mystcraft.tileentity.TileEntityLinkModifier;
import com.xcompwiz.util.CollectionUtils;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class GuiLinkModifier extends GuiContainerElements {
	private class ButtonHandler implements IGuiOnClickHandler, IGuiStateProvider {
		@Override
		public boolean getState(String id) {
			return container.getLinkFlag(id);
		}

		@Override
		public void onClick(GuiElementButton guiElementButton) {
			String id = guiElementButton.getId();
			NBTTagCompound nbttagcompound = new NBTTagCompound();
			nbttagcompound.setString(ContainerLinkModifier.Messages.SetFlag, id);
			nbttagcompound.setBoolean("Value", !getState(id));
			MystcraftPacketHandler.CHANNEL.sendToServer(new MPacketGuiMessage(mc.player.openContainer.windowId, nbttagcompound));
			container.processMessage(mc.player, nbttagcompound);
		}
	}

	public class TextBoxHandler implements IGuiTextProvider, IGuiOnTextChange {

		@Override
		public String getText(GuiElementTextField caller) {
			if (caller.getId().equals("ItemName")) {
				return container.getBookTitle();
			}
			if (caller.getId().equals("Seed")) {
				return container.getItemSeed();
			}
			return "Huh?";
		}

		@Override
		public void onTextChange(GuiElementTextField caller, String text) {
			if (caller.getId().equals("ItemName")) {
				NBTTagCompound nbttagcompound = new NBTTagCompound();
				nbttagcompound.setString(ContainerLinkModifier.Messages.SetTitle, text);
				MystcraftPacketHandler.CHANNEL.sendToServer(new MPacketGuiMessage(container.windowId, nbttagcompound));
				container.processMessage(mc.player, nbttagcompound);
			}
			if (caller.getId().equals("Seed")) {
				NBTTagCompound nbttagcompound = new NBTTagCompound();
				nbttagcompound.setString(ContainerLinkModifier.Messages.SetSeed, text);
				MystcraftPacketHandler.CHANNEL.sendToServer(new MPacketGuiMessage(container.windowId, nbttagcompound));
				container.processMessage(mc.player, nbttagcompound);
			}
		}
	}

	private ContainerLinkModifier container;

	private GuiElementTextField txt_seed;

	public GuiLinkModifier(InventoryPlayer inventoryplayer, TileEntityLinkModifier tileentity) {
		super(new ContainerLinkModifier(inventoryplayer, tileentity));
		this.container = (ContainerLinkModifier) this.inventorySlots;
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		if (txt_seed != null) {
			txt_seed.setVisible(container.hasItemSeed());
		}
	}

	@Override
	public void validate() {
		TextBoxHandler txtbxhdnlr = new TextBoxHandler();
		txt_seed = new GuiElementTextField(txtbxhdnlr, txtbxhdnlr, "Seed", 80, 15, xSize - 80 - 9, 14);
		txt_seed.setMaxLength(21);
		addElement(txt_seed);
		GuiElementTextField txt_box = new GuiElementTextField(txtbxhdnlr, txtbxhdnlr, "ItemName", 80, 56, xSize - 80 - 9, 14);
		txt_box.setMaxLength(21);
		addElement(txt_box);

		ButtonHandler buttonhandler = new ButtonHandler();
		int x = 5, y = 10;
		int scale = 18;
		for (String prop : InkEffects.getProperties()) {
			addElement(createButton(buttonhandler, prop, CollectionUtils.buildList(InkEffects.getLocalizedName(prop)), x, y, scale));
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
	protected void _drawBackgroundLayer(int mouseX, int mouseY, float f) {
		this.mc.renderEngine.bindTexture(GUIs.single_slot);
		GlStateManager.color(1F, 1F, 1F, 1F);
		this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
		this.fontRenderer.drawStringWithShadow(container.getLinkDimensionUID(), guiLeft + 100, guiTop + 40, 0xFFFFFF);
	}
}
