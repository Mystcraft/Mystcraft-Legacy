package com.xcompwiz.mystcraft.client.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import org.lwjgl.opengl.GL11;

import com.xcompwiz.mystcraft.client.gui.element.GuiElement;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementBook;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementBook.IGuiOnLinkHandler;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementButton;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementButton.IGuiOnClickHandler;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementFluidTank;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementNotebookTabs;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementNotebookTabs.IGuiNotebookTabsHandler;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementPage;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementPage.IGuiPageProvider;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementPageSurface;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementPageSurface.IGuiPositionedPagesProvider;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementScrollablePages;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementScrollablePages.IGuiPageListProvider;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementScrollablePages.IGuiScrollableClickHandler;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementTextField;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementTextField.IGuiOnTextChange;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementTextField.IGuiTextProvider;
import com.xcompwiz.mystcraft.data.Assets;
import com.xcompwiz.mystcraft.inventory.ContainerWritingDesk;
import com.xcompwiz.mystcraft.inventory.IFluidTankProvider;
import com.xcompwiz.mystcraft.item.IItemWritable;
import com.xcompwiz.mystcraft.item.ItemPage;
import com.xcompwiz.mystcraft.network.MPacketGuiMessage;
import com.xcompwiz.mystcraft.network.MystcraftPacketHandler;
import com.xcompwiz.mystcraft.oldapi.PositionableItem;
import com.xcompwiz.mystcraft.tileentity.TileEntityDesk;

public class GuiWritingDesk extends GuiContainerElements {
	public class PageListHandler implements IGuiPageListProvider, IGuiScrollableClickHandler {
		@Override
		public List<ItemStack> getPageList() {
			ItemStack target = container.getTarget();
			if (target == null || target.getItem() == null) return null;
			if (container.getBook() != null) return null;
			if (target.getItem() == ItemPage.instance) return null;
			if (target.getItem() instanceof IItemWritable) { return container.getBookPageList(); }
			return null;
		}

		@Override
		public void onItemPlace(GuiElementScrollablePages guiElementScrollablePages, int index, int mousebutton) {
			// Inform server container to remove the page from the 'hand' and put it in the page container at index
			NBTTagCompound nbttagcompound = new NBTTagCompound();
			nbttagcompound.setInteger("InsertHeldAt", index);
			nbttagcompound.setBoolean("Single", (mousebutton == 1));
			MystcraftPacketHandler.bus.sendToServer(MPacketGuiMessage.createPacket(mc.thePlayer.openContainer.windowId, nbttagcompound));
			container.processMessage(mc.thePlayer, nbttagcompound);
		}

		@Override
		public void onItemRemove(GuiElementScrollablePages guiElementScrollablePages, int clickedpage) {
			NBTTagCompound nbttagcompound = new NBTTagCompound();
			nbttagcompound.setInteger("TakeFromSlider", clickedpage);
			MystcraftPacketHandler.bus.sendToServer(MPacketGuiMessage.createPacket(mc.thePlayer.openContainer.windowId, nbttagcompound));
			container.processMessage(mc.thePlayer, nbttagcompound);
		}
	}

	public class NotebookTabsHandler implements IGuiNotebookTabsHandler {
		@Override
		public void onNotebookTabClick(int button, byte slot) {
			if (mc.thePlayer.inventory.getItemStack() != null) {
				NBTTagCompound nbttagcompound = new NBTTagCompound();
				nbttagcompound.setByte("AddToNotebook", slot);
				nbttagcompound.setBoolean("Single", (button == 1));
				MystcraftPacketHandler.bus.sendToServer(MPacketGuiMessage.createPacket(inventorySlots.windowId, nbttagcompound));
				container.processMessage(mc.thePlayer, nbttagcompound);
			} else if (getActiveTab() != slot) {
				NBTTagCompound nbttagcompound = new NBTTagCompound();
				nbttagcompound.setByte("SetActiveNotebook", slot);
				MystcraftPacketHandler.bus.sendToServer(MPacketGuiMessage.createPacket(inventorySlots.windowId, nbttagcompound));
				container.processMessage(mc.thePlayer, nbttagcompound);
			}
		}

		@Override
		public ItemStack getItemInSlot(byte slot) {
			return container.getNotebook(slot);
		}

		@Override
		public byte getTopSlot() {
			return container.getFirstNotebookSlot();
		}

		@Override
		public byte getActiveTab() {
			return container.getActiveNotebookSlot();
		}

		@Override
		public void setTopNotebookSlot(int topslot) {
			NBTTagCompound nbttagcompound = new NBTTagCompound();
			nbttagcompound.setByte("SetFirstNotebook", (byte) topslot);
			MystcraftPacketHandler.bus.sendToServer(MPacketGuiMessage.createPacket(inventorySlots.windowId, nbttagcompound));
			container.processMessage(mc.thePlayer, nbttagcompound);
		}
	}

	public class LinkHandler implements IGuiOnLinkHandler {
		@Override
		public void onLink(GuiElement elem) {
			NBTTagCompound nbttagcompound = new NBTTagCompound();
			nbttagcompound.setByte("Link", (byte) 0);
			MystcraftPacketHandler.bus.sendToServer(MPacketGuiMessage.createPacket(container.windowId, nbttagcompound));
		}
	}

	public class TextBoxHandlerSearch implements IGuiTextProvider, IGuiOnTextChange {
		private List<IGuiOnTextChange>	listeners	= new ArrayList<IGuiOnTextChange>();

		@Override
		public String getText(GuiElementTextField caller) {
			return text;
		}

		@Override
		public void onTextChange(GuiElementTextField caller, String newtext) {
			text = newtext;
			for (IGuiOnTextChange listener : listeners) {
				listener.onTextChange(caller, text);
			}
		}

		public void addListener(IGuiOnTextChange listener) {
			listeners.add(listener);
		}
	}

	public class ButtonHandlerSort implements IGuiOnClickHandler {
		@Override
		public void onClick(GuiElementButton caller) {
			if (caller.getId().equals("AZ")) {
				NBTTagCompound nbttagcompound = new NBTTagCompound();
				nbttagcompound.setByte("SortNotebook", (byte) 0);
				nbttagcompound.setString("SortType", caller.getId());
				MystcraftPacketHandler.bus.sendToServer(MPacketGuiMessage.createPacket(mc.thePlayer.openContainer.windowId, nbttagcompound));
				container.processMessage(mc.thePlayer, nbttagcompound);
			}
		}
	}

	public class PositionedPagesProvider implements IGuiPositionedPagesProvider {
		@Override
		public List<PositionableItem> getPositionedPages() {
			return container.getSurfacePages(container.getActiveNotebookSlot());
		}
	}

	public class TextBoxHandlerTargetName implements IGuiTextProvider, IGuiOnTextChange {
		@Override
		public String getText(GuiElementTextField caller) {
			return container.getTargetName();
		}

		@Override
		public void onTextChange(GuiElementTextField caller, String text) {
			NBTTagCompound nbttagcompound = new NBTTagCompound();
			nbttagcompound.setString("SetTitle", text);
			MystcraftPacketHandler.bus.sendToServer(MPacketGuiMessage.createPacket(container.windowId, nbttagcompound));
			container.processMessage(mc.thePlayer, nbttagcompound);
		}
	}

	public class PageHandlerTarget implements IGuiPageProvider {
		@Override
		public ItemStack getPageItemStack(GuiElementPage elem) {
			return container.getTarget();
		}
	}

	private ContainerWritingDesk	container;

	private int						mainTop;
	private int						guiCenter;

	String							text		= null;

	private static final int		leftsize		= 228;
	private static final int		windowsizeX		= 176;
	private static final int		windowsizeY		= 166;
	private static final int		buttonssizeY	= 18;

	public GuiWritingDesk(InventoryPlayer inventoryplayer, TileEntityDesk tileentity) {
		super(new ContainerWritingDesk(inventoryplayer, tileentity));
		this.container = (ContainerWritingDesk) this.inventorySlots;
	}

	@Override
	public void validate() {
		guiLeft = (width / 2) - (leftsize / 2) - (windowsizeX / 2);
		guiCenter = guiLeft + leftsize + 5;
		xSize = leftsize + windowsizeX;
		ySize = windowsizeY + buttonssizeY;
		guiTop = (height - ySize) / 2;
		mainTop = guiTop + buttonssizeY + 2;

		TextBoxHandlerSearch txt_search = new TextBoxHandlerSearch();
		elements.add(new GuiElementTextField(txt_search, txt_search, guiLeft + 58 + (buttonssizeY + 2) * 2, guiTop, leftsize - 53 - (buttonssizeY + 2) * 2, buttonssizeY));

		TextBoxHandlerTargetName txt_targname = new TextBoxHandlerTargetName();
		elements.add(new GuiElementTextField(txt_targname, txt_targname, guiCenter + 28, mainTop + 61, windowsizeX - 48 - 9 - 20, 14));

		IFluidTankProvider fluidprovider = container.getInkTankProvider();
		elements.add(new GuiElementFluidTank(this.container, mc, guiCenter + windowsizeX - 44, mainTop + 7, 16, 70, fluidprovider));

		elements.add(new GuiElementBook(this.container, new LinkHandler(), guiCenter + 30, mainTop + 6, 90, 50));

		PageListHandler pagelistHandler = new PageListHandler();
		elements.add(new GuiElementScrollablePages(pagelistHandler, pagelistHandler, mc, guiCenter + 30, mainTop + 6, 90, 50));

		elements.add(new GuiElementPage(new PageHandlerTarget(), guiCenter + 32, mainTop + 6, 37.5F, 50F));

		GuiElementNotebookTabs notebooktabs = new GuiElementNotebookTabs(new NotebookTabsHandler(), guiLeft, mainTop, 58, ySize);
		//txt_search.addListener(notebooktabs);
		elements.add(notebooktabs);

		GuiElementPageSurface surface = new GuiElementPageSurface(new PositionedPagesProvider(), this.container, this.mc, guiLeft + 58, mainTop, leftsize - 53, windowsizeY);
		txt_search.addListener(surface);
		elements.add(surface);

		IGuiOnClickHandler btnHandlerSort = new ButtonHandlerSort();
		GuiElementButton btn_sortA = new GuiElementButton(btnHandlerSort, "AZ", guiLeft + 58, guiTop, buttonssizeY, buttonssizeY);
		btn_sortA.setText("AZ");
		btn_sortA.setTooltip(Arrays.asList("Sort Alphabetically"));
		elements.add(btn_sortA);
		//GuiElementButton btn_sortO = new GuiElementButton(btnHandlerSort, "123", guiLeft + 58, guiTop, buttonssizeY, buttonssizeY);
		//btn_sortO.setText("123");
		//btn_sortO.setTooltip(Arrays.asList("Sort By Slot Number"));
		//elements.add(btn_sortO);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.renderEngine.bindTexture(Assets.gui_desk);
		drawTexturedModalRect(guiCenter, mainTop, 0, 0, windowsizeX, windowsizeY);
		this.drawElementBackgrounds(f, mouseX, mouseY);
	}
}
