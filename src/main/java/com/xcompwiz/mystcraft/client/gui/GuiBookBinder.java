package com.xcompwiz.mystcraft.client.gui;

import java.util.Arrays;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.xcompwiz.mystcraft.client.gui.element.GuiElementIcon;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementScrollablePages;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementScrollablePages.IGuiPageListProvider;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementScrollablePages.IGuiScrollableClickHandler;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementTextField;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementTextField.IGuiOnTextChange;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementTextField.IGuiTextProvider;
import com.xcompwiz.mystcraft.client.gui.element.data.GuiIconResource;
import com.xcompwiz.mystcraft.client.gui.element.data.IGuiIcon;
import com.xcompwiz.mystcraft.data.Assets.GUIs;
import com.xcompwiz.mystcraft.inventory.ContainerBookBinder;
import com.xcompwiz.mystcraft.network.MystcraftPacketHandler;
import com.xcompwiz.mystcraft.network.packet.MPacketGuiMessage;
import com.xcompwiz.mystcraft.page.Page;
import com.xcompwiz.mystcraft.tileentity.TileEntityBookBinder;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class GuiBookBinder extends GuiContainerElements {

	private static final List<String> panel_tooltip = Arrays.asList("Missing Link Panel", "Add a link panel as the first page of the book.");

	private class GuiElementMissingPanel extends GuiElementIcon {

		private boolean hovered;
		private float alpha = 1;

		public GuiElementMissingPanel(IGuiIcon icon, int guiLeft, int guiTop, int xSize, int ySize) {
			super(icon, guiLeft, guiTop, xSize, ySize);
		}

		@Override
		public List<String> _getTooltipInfo() {
			if (this.hovered) {
				return panel_tooltip;
			}
			return super._getTooltipInfo();
		}

		@Override
		protected void _onTick() {
			super._onTick();
			long time = System.currentTimeMillis();
			alpha = (time % 4000) / 2000.F;
			if (alpha > 1)
				alpha = 2 - alpha;
			alpha += 0.3F;
		}

		@Override
		public boolean isVisible() {
			if (!super.isVisible())
				return false;
			List<ItemStack> pagelist = container.getPageList();
			if (pagelist.isEmpty())
				return true;
			if (Page.isLinkPanel(pagelist.get(0)))
				return false;
			return true;
		}

		@Override
		public void _renderBackground(float f, int mouseX, int mouseY) {
			hovered = this.contains(mouseX, mouseY);
			GlStateManager.color(1F, 0.5F, 0.5F, alpha);
			GlStateManager.enableBlend();
			GlStateManager.disableAlpha();
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			GlStateManager.shadeModel(GL11.GL_SMOOTH);
			super._renderBackground(f, mouseX, mouseY);
			GlStateManager.shadeModel(GL11.GL_FLAT);
			GlStateManager.disableBlend();
			GlStateManager.enableAlpha();
		}

	}

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
			MystcraftPacketHandler.CHANNEL.sendToServer(new MPacketGuiMessage(mc.player.openContainer.windowId, nbttagcompound));
			container.processMessage(mc.player, nbttagcompound);
		}

		@Override
		public void onItemRemove(GuiElementScrollablePages guiElementScrollablePages, int clickedpage) {
			NBTTagCompound nbttagcompound = new NBTTagCompound();
			nbttagcompound.setInteger(ContainerBookBinder.Messages.TakeFromSlider, clickedpage);
			MystcraftPacketHandler.CHANNEL.sendToServer(new MPacketGuiMessage(mc.player.openContainer.windowId, nbttagcompound));

			container.inventoryplayer.setItemStack(container.tileentity.removePage(clickedpage));
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
			MystcraftPacketHandler.CHANNEL.sendToServer(new MPacketGuiMessage(container.windowId, nbttagcompound));
			container.processMessage(mc.player, nbttagcompound);
		}

	}

	private ContainerBookBinder container;
	private GuiElementTextField txtBookName;
	private int errorcolor = 0xFFFF0000;
	private int txtcolor = 0xFFA0A0A0;

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
		addElement(new GuiElementMissingPanel(new GuiIconResource(GUIs.binder, 176, 0, 30, 40), 27, 26, 18, 18));
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		txtBookName.setBorderColor(txtBookName.getText().equals("") ? errorcolor : txtcolor);
		this.container.updateCraftResult();
	}

	@Override
	protected void _drawBackgroundLayer(int mouseX, int mouseY, float f) {
		GlStateManager.color(1F, 1F, 1F, 1F);
		mc.renderEngine.bindTexture(GUIs.binder);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		fontRenderer.drawString(I18n.format("container.inventory"), guiLeft + 8, guiTop + (ySize - 96) + 2, 0x404040);
	}
}
