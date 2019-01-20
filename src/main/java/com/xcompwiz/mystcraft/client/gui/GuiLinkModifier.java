package com.xcompwiz.mystcraft.client.gui;

import java.awt.Color;
import java.util.List;

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
	private class MarkDeadButtonHandler implements IGuiOnClickHandler, IGuiStateProvider {
		@Override
		public boolean getState(String id) {
			return container.isLinkDead();
		}

		@Override
		public void onClick(GuiElementButton guiElementButton) {
			NBTTagCompound nbttagcompound = new NBTTagCompound();
			nbttagcompound.setString(ContainerLinkModifier.Messages.RecycleDim, "");
			MystcraftPacketHandler.CHANNEL.sendToServer(new MPacketGuiMessage(mc.player.openContainer.windowId, nbttagcompound));
		}
	}

	private class MarkDeadArmButtonHandler implements IGuiOnClickHandler, IGuiStateProvider {
		@Override
		public boolean getState(String id) {
			return false;
		}

		@Override
		public void onClick(GuiElementButton guiElementButton) {
			isArmed = true;
		}
	}

	private class FlagButtonHandler implements IGuiOnClickHandler, IGuiStateProvider {
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
	private GuiElementButtonToggle deadarm_btn;
	private GuiElementButtonToggle dead_btn;
	
	boolean isArmed = false;

	public GuiLinkModifier(InventoryPlayer inventoryplayer, TileEntityLinkModifier tileentity) {
		super(new ContainerLinkModifier(inventoryplayer, tileentity));
		this.container = (ContainerLinkModifier) this.inventorySlots;
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		boolean hasSeed = container.hasItemSeed();
		boolean isDead = container.isLinkDead();
		if (txt_seed != null) {
			txt_seed.setVisible(hasSeed);
		}
		if (deadarm_btn != null) {
			deadarm_btn.setVisible(hasSeed && !isArmed && !isDead);
		}
		if (dead_btn != null) {
			dead_btn.setVisible(hasSeed && (isArmed || isDead));
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

		FlagButtonHandler buttonhandler = new FlagButtonHandler();
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
		MarkDeadButtonHandler killbuttonhandler = new MarkDeadButtonHandler();
		dead_btn = createButton(killbuttonhandler, "confirmkill", CollectionUtils.buildList("Confirm mark book as dead. (NO UNDO!)"), 140, 32, scale);
		dead_btn.color = Color.RED;
		addElement(dead_btn);
		dead_btn.setVisible(false);

		MarkDeadArmButtonHandler armkillbuttonhandler = new MarkDeadArmButtonHandler();
		deadarm_btn = createButton(armkillbuttonhandler, "kill", CollectionUtils.buildList("Mark book as dead. (NO UNDO!)"), 120, 32, scale);
		addElement(deadarm_btn);
	}

	private <T extends IGuiOnClickHandler & IGuiStateProvider> GuiElementButtonToggle createButton(T buttonhandler, String id, List<String> tooltip, int x, int y, int size) {
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
