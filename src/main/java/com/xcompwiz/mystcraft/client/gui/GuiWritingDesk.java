package com.xcompwiz.mystcraft.client.gui;

import java.util.Arrays;
import java.util.List;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import com.xcompwiz.mystcraft.api.item.IItemPageCollection;
import com.xcompwiz.mystcraft.api.item.IItemWritable;
import com.xcompwiz.mystcraft.client.gui.element.GuiElement;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementBook;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementBook.IGuiOnLinkHandler;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementButton;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementButtonToggle;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementFluidTank;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementPage;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementPage.IGuiPageProvider;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementPageSurface;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementPageSurface.PositionableItem;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementScrollablePages;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementScrollablePages.IGuiPageListProvider;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementScrollablePages.IGuiScrollableClickHandler;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementSurfaceTabs;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementSurfaceTabs.IGuiSurfaceTabsHandler;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementTextField;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementTextField.IGuiOnTextChange;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementTextField.IGuiTextProvider;
import com.xcompwiz.mystcraft.data.Assets.GUIs;
import com.xcompwiz.mystcraft.data.ModItems;
import com.xcompwiz.mystcraft.inventory.ContainerWritingDesk;
import com.xcompwiz.mystcraft.inventory.IFluidTankProvider;
import com.xcompwiz.mystcraft.network.MystcraftPacketHandler;
import com.xcompwiz.mystcraft.network.packet.MPacketGuiMessage;
import com.xcompwiz.mystcraft.page.Page;
import com.xcompwiz.mystcraft.tileentity.TileEntityDesk;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;

public class GuiWritingDesk extends GuiContainerElements {

	public class GuiElementSurfaceControls extends GuiElementSurfaceControlsBase {

		public GuiElementSurfaceControls(Minecraft mc, int guiLeft, int guiTop, int width, int height) {
			super(mc, guiLeft, guiTop, width, height);
		}

		@Override
		public ItemStack getItemStack() {
			return container.getTabSlot(container.getActiveTabSlot());
		}

		@Override
		public void place(int index, boolean single) {
			NBTTagCompound nbttagcompound = new NBTTagCompound();
			nbttagcompound.setByte(ContainerWritingDesk.Messages.AddToSurface, container.getActiveTabSlot());
			nbttagcompound.setBoolean("Single", single);
			nbttagcompound.setInteger("Index", index);
			MystcraftPacketHandler.CHANNEL.sendToServer(new MPacketGuiMessage(mc.player.openContainer.windowId, nbttagcompound));
			container.processMessage(mc.player, nbttagcompound);
		}

		@Override
		public void pickup(PositionableItem collectionelement) {
			if (collectionelement.count <= 0)
				return;
			boolean iscollection = getItemStack().getItem() instanceof IItemPageCollection;
			if (iscollection) {
				NBTTagCompound itemdata = new NBTTagCompound();
				ItemStack page = collectionelement.itemstack;
				if (GuiWritingDesk.isShiftKeyDown()) {
					page = page.copy();
					page.setCount(64);
				}
				page.writeToNBT(itemdata);
				NBTTagCompound nbttagcompound = new NBTTagCompound();
				nbttagcompound.setTag(ContainerWritingDesk.Messages.RemoveFromCollection, itemdata);
				MystcraftPacketHandler.CHANNEL.sendToServer(new MPacketGuiMessage(mc.player.openContainer.windowId, nbttagcompound));
				container.processMessage(mc.player, nbttagcompound);
			} else {
				int index = collectionelement.slotId;
				NBTTagCompound nbttagcompound = new NBTTagCompound();
				nbttagcompound.setInteger(ContainerWritingDesk.Messages.RemoveFromOrderedCollection, index);
				MystcraftPacketHandler.CHANNEL.sendToServer(new MPacketGuiMessage(mc.player.openContainer.windowId, nbttagcompound));
				container.processMessage(mc.player, nbttagcompound);
			}
		}

		@Override
		public void copy(PositionableItem collectionelement) {
			ResourceLocation symbol = Page.getSymbol(collectionelement.itemstack);
			if (symbol == null)
				return;
			if (collectionelement.count <= 0)
				return;
			NBTTagCompound nbttagcompound = new NBTTagCompound();
			nbttagcompound.setString(ContainerWritingDesk.Messages.WriteSymbol, symbol.toString());
			MystcraftPacketHandler.CHANNEL.sendToServer(new MPacketGuiMessage(mc.player.openContainer.windowId, nbttagcompound));
			container.processMessage(mc.player, nbttagcompound);
		}
	}

	public class PageListHandler implements IGuiPageListProvider, IGuiScrollableClickHandler {

		@Override
		public List<ItemStack> getPageList() {
			ItemStack target = container.getTarget();
			if (target.isEmpty())
				return null;
			if (!container.getBook().isEmpty())
				return null;
			if (target.getItem() == ModItems.page)
				return null;
			if (target.getItem() instanceof IItemWritable) {
				return container.getBookPageList();
			}
			return null;
		}

		@Override
		public void onItemPlace(GuiElementScrollablePages guiElementScrollablePages, int index, int mousebutton) {
			// Inform server container to remove the page from the 'hand' and put it in the page container at index
			NBTTagCompound nbttagcompound = new NBTTagCompound();
			nbttagcompound.setInteger(ContainerWritingDesk.Messages.InsertHeldAt, index);
			nbttagcompound.setBoolean("Single", (mousebutton == 1));
			MystcraftPacketHandler.CHANNEL.sendToServer(new MPacketGuiMessage(mc.player.openContainer.windowId, nbttagcompound));
			container.processMessage(mc.player, nbttagcompound);
		}

		@Override
		public void onItemRemove(GuiElementScrollablePages guiElementScrollablePages, int clickedpage) {
			NBTTagCompound nbttagcompound = new NBTTagCompound();
			nbttagcompound.setInteger(ContainerWritingDesk.Messages.TakeFromSlider, clickedpage);
			MystcraftPacketHandler.CHANNEL.sendToServer(new MPacketGuiMessage(mc.player.openContainer.windowId, nbttagcompound));
			container.processMessage(mc.player, nbttagcompound);
		}
	}

	public class SurfaceTabsHandler implements IGuiSurfaceTabsHandler {

		@Override
		public void onSurfaceTabClick(int button, byte slot) {
			if (!mc.player.inventory.getItemStack().isEmpty()) {
				NBTTagCompound nbttagcompound = new NBTTagCompound();
				nbttagcompound.setByte(ContainerWritingDesk.Messages.AddToTab, slot);
				nbttagcompound.setBoolean("Single", (button == 1));
				MystcraftPacketHandler.CHANNEL.sendToServer(new MPacketGuiMessage(inventorySlots.windowId, nbttagcompound));
				container.processMessage(mc.player, nbttagcompound);
			} else if (getActiveTab() != slot) {
				NBTTagCompound nbttagcompound = new NBTTagCompound();
				nbttagcompound.setByte(ContainerWritingDesk.Messages.SetActiveNotebook, slot);
				MystcraftPacketHandler.CHANNEL.sendToServer(new MPacketGuiMessage(inventorySlots.windowId, nbttagcompound));
				container.processMessage(mc.player, nbttagcompound);
			}
		}

		@Override
		@Nonnull
		public ItemStack getItemInSlot(byte slot) {
			return container.getTabSlot(slot);
		}

		@Override
		public byte getTopSlot() {
			return container.getFirstTabSlot();
		}

		@Override
		public byte getActiveTab() {
			return container.getActiveTabSlot();
		}

		@Override
		public void setTopTabSlot(int topslot) {
			NBTTagCompound nbttagcompound = new NBTTagCompound();
			nbttagcompound.setByte(ContainerWritingDesk.Messages.SetFirstNotebook, (byte) topslot);
			MystcraftPacketHandler.CHANNEL.sendToServer(new MPacketGuiMessage(inventorySlots.windowId, nbttagcompound));
			container.processMessage(mc.player, nbttagcompound);
		}
	}

	public class LinkHandler implements IGuiOnLinkHandler {

		@Override
		public void onLink(GuiElement elem) {
			NBTTagCompound nbttagcompound = new NBTTagCompound();
			nbttagcompound.setByte(ContainerWritingDesk.Messages.Link, (byte) 0);
			MystcraftPacketHandler.CHANNEL.sendToServer(new MPacketGuiMessage(container.windowId, nbttagcompound));
		}

	}

	public class TextBoxHandlerTargetName implements IGuiTextProvider, IGuiOnTextChange {

		@Override
		public String getText(GuiElementTextField caller) {
			ItemStack target = container.getTarget();
			caller.setReadOnly(target.isEmpty() || target.getItem() == ModItems.page);
			caller.setEnabled(!caller.isReadOnly());
			return container.getTargetName();
		}

		@Override
		public void onTextChange(GuiElementTextField caller, String text) {
			NBTTagCompound nbttagcompound = new NBTTagCompound();
			nbttagcompound.setString(ContainerWritingDesk.Messages.SetTitle, text);
			MystcraftPacketHandler.CHANNEL.sendToServer(new MPacketGuiMessage(container.windowId, nbttagcompound));
			container.processMessage(mc.player, nbttagcompound);
		}
	}

	public class PageHandlerTarget implements IGuiPageProvider {

		@Override
		@Nonnull
		public ItemStack getPageItemStack(GuiElementPage elem) {
			return container.getTarget();
		}

	}

	private ContainerWritingDesk container;

	private int mainTop;
	private int guiCenter;

	static final int leftsize = 228;
	private static final int windowsizeX = 176;
	private static final int windowsizeY = 166;
	private static final int buttonssizeY = 18;

	public GuiWritingDesk(InventoryPlayer inventoryplayer, TileEntityDesk tileentity) {
		super(new ContainerWritingDesk(inventoryplayer, tileentity));
		this.container = (ContainerWritingDesk) this.inventorySlots;
	}

	@Override
	public void validate() {
		guiLeft = (width / 2) - (leftsize / 2) - (windowsizeX / 2);
		guiCenter = leftsize + 5;
		xSize = leftsize + windowsizeX + 5;
		ySize = windowsizeY + buttonssizeY + 1;
		guiTop = (height - ySize) / 2;
		mainTop = buttonssizeY + 2;

		GuiElementTextField txt_box = null;

		GuiElementSurfaceControls surfacemanager = new GuiElementSurfaceControls(this.mc, 53, 0, leftsize - 53, ySize);
		txt_box = new GuiElementTextField(surfacemanager, surfacemanager, "SearchBox", 58 + (buttonssizeY + 2) * 2, 0, leftsize - 53 - (buttonssizeY + 2) * 2, buttonssizeY);
		addElement(txt_box);
		TextBoxHandlerTargetName txt_targname = new TextBoxHandlerTargetName();
		txt_box = new GuiElementTextField(txt_targname, txt_targname, "ItemName", guiCenter + 28, mainTop + 61, windowsizeX - 48 - 9 - 20, 14);
		txt_box.setMaxLength(21);
		addElement(txt_box);

		IFluidTankProvider fluidprovider = container.getInkTankProvider();
		addElement(new GuiElementFluidTank(this.container, mc, guiCenter + windowsizeX - 44, mainTop + 7, 16, 70, fluidprovider));

		addElement(new GuiElementBook(this.container, new LinkHandler(), guiCenter + 30, mainTop + 6, 90, 50));

		PageListHandler pagelistHandler = new PageListHandler();
		addElement(new GuiElementScrollablePages(pagelistHandler, pagelistHandler, mc, guiCenter + 27, mainTop + 6, windowsizeX - 47 - 9 - 19, 50));

		addElement(new GuiElementPage(new PageHandlerTarget(), guiCenter + 32, mainTop + 6, 37.5F, 50F));

		GuiElementSurfaceTabs surfacetabs = new GuiElementSurfaceTabs(new SurfaceTabsHandler(), 0, mainTop, 58, ySize);
		//txt_search.addListener(surfacetabs);
		addElement(surfacetabs);

		GuiElementPageSurface surface = new GuiElementPageSurface(surfacemanager, this.mc, 58, mainTop, leftsize - 53, windowsizeY);
		surfacemanager.addListener(surface);
		addElement(surface);

		GuiElementButton btn_sortA = new GuiElementButtonToggle(surfacemanager, surfacemanager, "AZ", 58, 0, buttonssizeY, buttonssizeY);
		btn_sortA.setText("AZ");
		btn_sortA.setTooltip(Arrays.asList("Sort Alphabetically"));
		addElement(btn_sortA);
		GuiElementButton btn_allsym = new GuiElementButtonToggle(surfacemanager, surfacemanager, "ALL", 58 + buttonssizeY, 0, buttonssizeY, buttonssizeY);
		btn_allsym.setText("ALL");
		btn_allsym.setTooltip(Arrays.asList("Show all Symbols"));
		addElement(btn_allsym);

		surfacemanager.addSurfaceElement(btn_sortA);
		surfacemanager.addSurfaceElement(btn_allsym);
		//GuiElementButton btn_sortO = new GuiElementButton(btnHandlerSort, "123", guiLeft + 58, guiTop, buttonssizeY, buttonssizeY);
		//btn_sortO.setText("123");
		//btn_sortO.setTooltip(Arrays.asList("Sort By Slot Number"));
		//addElement(btn_sortO);
	}

	@Override
	protected void _drawBackgroundLayer(int mouseX, int mouseY, float f) {
		GlStateManager.color(1F, 1F, 1F, 1F);
		mc.renderEngine.bindTexture(GUIs.desk);
		drawTexturedModalRect(guiLeft + guiCenter, guiTop + mainTop, 0, 0, windowsizeX, windowsizeY);
	}
}
