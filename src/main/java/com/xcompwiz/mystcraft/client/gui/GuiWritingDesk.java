package com.xcompwiz.mystcraft.client.gui;

import java.util.Arrays;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import org.lwjgl.opengl.GL11;

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
import com.xcompwiz.mystcraft.item.IItemWritable;
import com.xcompwiz.mystcraft.network.MPacketGuiMessage;
import com.xcompwiz.mystcraft.network.MystcraftPacketHandler;
import com.xcompwiz.mystcraft.page.IItemPageCollection;
import com.xcompwiz.mystcraft.page.Page;
import com.xcompwiz.mystcraft.tileentity.TileEntityDesk;

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
			nbttagcompound.setByte("AddToSurface", container.getActiveTabSlot());
			nbttagcompound.setBoolean("Single", single);
			nbttagcompound.setInteger("Index", index);
			MystcraftPacketHandler.bus.sendToServer(MPacketGuiMessage.createPacket(mc.thePlayer.openContainer.windowId, nbttagcompound));
			container.processMessage(mc.thePlayer, nbttagcompound);
		}

		@Override
		public void pickup(PositionableItem collectionelement) {
			if (collectionelement.count <= 0) return;
			boolean iscollection = getItemStack().getItem() instanceof IItemPageCollection;
			if (iscollection) {
				NBTTagCompound itemdata = new NBTTagCompound();
				ItemStack page = collectionelement.itemstack;
				if (GuiWritingDesk.isShiftKeyDown()) {
					page = page.copy();
					page.stackSize = 64;
				}
				page.writeToNBT(itemdata);
				NBTTagCompound nbttagcompound = new NBTTagCompound();
				nbttagcompound.setTag("RemoveFromCollection", itemdata);
				MystcraftPacketHandler.bus.sendToServer(MPacketGuiMessage.createPacket(mc.thePlayer.openContainer.windowId, nbttagcompound));
				container.processMessage(mc.thePlayer, nbttagcompound);
			} else {
				int index = collectionelement.slotId;
				NBTTagCompound nbttagcompound = new NBTTagCompound();
				nbttagcompound.setInteger("RemoveFromOrderedCollection", index);
				MystcraftPacketHandler.bus.sendToServer(MPacketGuiMessage.createPacket(mc.thePlayer.openContainer.windowId, nbttagcompound));
				container.processMessage(mc.thePlayer, nbttagcompound);
			}
		}

		@Override
		public void copy(PositionableItem collectionelement) {
			String symbol = Page.getSymbol(collectionelement.itemstack);
			if (symbol == null) return;
			if (collectionelement.count <= 0) return;
			NBTTagCompound nbttagcompound = new NBTTagCompound();
			nbttagcompound.setString("WriteSymbol", symbol);
			MystcraftPacketHandler.bus.sendToServer(MPacketGuiMessage.createPacket(mc.thePlayer.openContainer.windowId, nbttagcompound));
			container.processMessage(mc.thePlayer, nbttagcompound);
		}
	}

	public class PageListHandler implements IGuiPageListProvider, IGuiScrollableClickHandler {
		@Override
		public List<ItemStack> getPageList() {
			ItemStack target = container.getTarget();
			if (target == null || target.getItem() == null) return null;
			if (container.getBook() != null) return null;
			if (target.getItem() == ModItems.page) return null;
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

	public class SurfaceTabsHandler implements IGuiSurfaceTabsHandler {
		@Override
		public void onSurfaceTabClick(int button, byte slot) {
			if (mc.thePlayer.inventory.getItemStack() != null) {
				NBTTagCompound nbttagcompound = new NBTTagCompound();
				nbttagcompound.setByte("AddToCollection", slot);
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

	public class TextBoxHandlerTargetName implements IGuiTextProvider, IGuiOnTextChange {
		@Override
		public String getText(GuiElementTextField caller) {
			ItemStack target = container.getTarget();
			caller.setReadOnly(target == null || target.getItem() == null || target.getItem() == ModItems.page);
			caller.setEnabled(!caller.isReadOnly());
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

	static final int				leftsize		= 228;
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
		xSize = leftsize + windowsizeX + 5;
		ySize = windowsizeY + buttonssizeY + 1;
		guiTop = (height - ySize) / 2;
		mainTop = guiTop + buttonssizeY + 2;

		GuiElementTextField txt_box = null;

		GuiElementSurfaceControls surfacemanager = new GuiElementSurfaceControls(this.mc, guiLeft + 53, guiTop, leftsize - 53, ySize);
		txt_box = new GuiElementTextField(surfacemanager, surfacemanager, "SearchBox", guiLeft + 58 + (buttonssizeY + 2) * 2, guiTop, leftsize - 53 - (buttonssizeY + 2) * 2, buttonssizeY);
		elements.add(txt_box);
		TextBoxHandlerTargetName txt_targname = new TextBoxHandlerTargetName();
		txt_box = new GuiElementTextField(txt_targname, txt_targname, "ItemName", guiCenter + 28, mainTop + 61, windowsizeX - 48 - 9 - 20, 14);
		txt_box.setMaxLength(21);
		elements.add(txt_box);

		IFluidTankProvider fluidprovider = container.getInkTankProvider();
		elements.add(new GuiElementFluidTank(this.container, mc, guiCenter + windowsizeX - 44, mainTop + 7, 16, 70, fluidprovider));

		elements.add(new GuiElementBook(this.container, new LinkHandler(), guiCenter + 30, mainTop + 6, 90, 50));

		PageListHandler pagelistHandler = new PageListHandler();
		elements.add(new GuiElementScrollablePages(pagelistHandler, pagelistHandler, mc, guiCenter + 30, mainTop + 6, 90, 50));

		elements.add(new GuiElementPage(new PageHandlerTarget(), guiCenter + 32, mainTop + 6, 37.5F, 50F));

		GuiElementSurfaceTabs surfacetabs = new GuiElementSurfaceTabs(new SurfaceTabsHandler(), guiLeft, mainTop, 58, ySize);
		//txt_search.addListener(surfacetabs);
		elements.add(surfacetabs);

		GuiElementPageSurface surface = new GuiElementPageSurface(surfacemanager, this.mc, guiLeft + 58, mainTop, leftsize - 53, windowsizeY);
		surfacemanager.addListener(surface);
		elements.add(surface);

		GuiElementButton btn_sortA = new GuiElementButtonToggle(surfacemanager, surfacemanager, "AZ", guiLeft + 58, guiTop, buttonssizeY, buttonssizeY);
		btn_sortA.setText("AZ");
		btn_sortA.setTooltip(Arrays.asList("Sort Alphabetically"));
		elements.add(btn_sortA);
		GuiElementButton btn_allsym = new GuiElementButtonToggle(surfacemanager, surfacemanager, "ALL", guiLeft + 58 + buttonssizeY, guiTop, buttonssizeY, buttonssizeY);
		btn_allsym.setText("ALL");
		btn_allsym.setTooltip(Arrays.asList("Show all Symbols"));
		elements.add(btn_allsym);

		surfacemanager.addSurfaceElement(btn_sortA);
		surfacemanager.addSurfaceElement(btn_allsym);
		//GuiElementButton btn_sortO = new GuiElementButton(btnHandlerSort, "123", guiLeft + 58, guiTop, buttonssizeY, buttonssizeY);
		//btn_sortO.setText("123");
		//btn_sortO.setTooltip(Arrays.asList("Sort By Slot Number"));
		//elements.add(btn_sortO);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.renderEngine.bindTexture(GUIs.desk);
		drawTexturedModalRect(guiCenter, mainTop, 0, 0, windowsizeX, windowsizeY);
		this.drawElementBackgrounds(f, mouseX, mouseY);
	}
}
