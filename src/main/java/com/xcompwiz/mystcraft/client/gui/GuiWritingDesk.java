package com.xcompwiz.mystcraft.client.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import org.lwjgl.opengl.GL11;

import com.xcompwiz.mystcraft.client.gui.element.GuiElement;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementBook;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementBook.IGuiOnLinkHandler;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementButton;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementButton.IGuiOnClickHandler;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementButtonToggle;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementButtonToggle.IGuiStateProvider;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementFluidTank;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementNotebookTabs;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementNotebookTabs.IGuiNotebookTabsHandler;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementPage;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementPage.IGuiPageProvider;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementPageSurface;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementPageSurface.IGuiPositionedPagesProvider;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementPageSurface.PositionableItem;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementScrollablePages;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementScrollablePages.IGuiPageListProvider;
import com.xcompwiz.mystcraft.client.gui.element.GuiElementScrollablePages.IGuiScrollableClickHandler;
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
import com.xcompwiz.mystcraft.page.SortingUtils;
import com.xcompwiz.mystcraft.symbol.IAgeSymbol;
import com.xcompwiz.mystcraft.symbol.SymbolManager;
import com.xcompwiz.mystcraft.tileentity.TileEntityDesk;

public class GuiWritingDesk extends GuiContainerElements {
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

	public class PositionedPagesProvider implements IGuiPositionedPagesProvider, IGuiOnClickHandler, IGuiStateProvider {
		private ItemStack				cached_notebook;
		private List<PositionableItem>	arranged_pages;
		private Comparator<ItemStack>	sorttype	= SortingUtils.ComparatorItemSymbolAlphabetical.instance;
		private boolean					showall		= false;

		@Override
		public List<PositionableItem> getPositionedPages() {
			ItemStack notebook = container.getNotebook(container.getActiveNotebookSlot());
			if (!ItemStack.areItemStacksEqual(notebook, cached_notebook)) {
				if (notebook == null) {
					cached_notebook = null;
					arranged_pages = null;
					return null;
				}
				cached_notebook = notebook.copy();
				updateCollection();
			}
			return arranged_pages;
		}

		private void updateCollection() {
			if (cached_notebook.getItem() instanceof IItemPageCollection) {
				int i = 0;

				IItemPageCollection item = (IItemPageCollection) cached_notebook.getItem();
				List<ItemStack> pages = item.getPages(mc.thePlayer, cached_notebook);
				Map<String, PositionableItem> collection = new HashMap<String, PositionableItem>();
				for (ItemStack page : pages) {
					if (page == null) {
						++i;
						continue;
					}
					String key = Page.getSymbol(page);
					if (Page.isLinkPanel(page)) {
						key = "Linkpanel";
					}
					if (key == null) key = "?";
					PositionableItem newpos = collection.get(key);
					if (newpos == null) {
						newpos = new PositionableItem();
						newpos.itemstack = page;
						newpos.slotId = i;
						newpos.count = 1;
						collection.put(key, newpos);
					} else {
						newpos.count++;
					}
					++i;
				}
				if (showall) {
					Collection<IAgeSymbol> symbols = SymbolManager.getAgeSymbols();
					for (IAgeSymbol symbol : symbols) {
						String symbolname = symbol.identifier();
						PositionableItem newpos = collection.get(symbolname);
						if (newpos == null) {
							newpos = new PositionableItem();
							newpos.itemstack = Page.createSymbolPage(symbolname);
							newpos.count = 0;
							collection.put(symbolname, newpos);
						}
					}
				}
				arranged_pages = new LinkedList<PositionableItem>();
				arranged_pages.addAll(collection.values());
				sort(sorttype);
			} else if (cached_notebook.getItem() instanceof IItemWritable) {
				int i = 0;

				IItemWritable item = (IItemWritable) cached_notebook.getItem();
				List<ItemStack> pages = item.getPageList(mc.thePlayer, cached_notebook);
				arranged_pages = new LinkedList<PositionableItem>();
				for (ItemStack page : pages) {
					if (page == null) {
						++i;
						continue;
					}
					PositionableItem newpos = new PositionableItem();
					newpos.itemstack = page;
					newpos.slotId = i++;
					newpos.count = 1;
					arranged_pages.add(newpos);
				}
			} else {
				arranged_pages = null;
			}
			if (arranged_pages != null) arrange();
		}

		public void arrange() {
			float xStep = GuiElementPageSurface.pagewidth + 1;
			float yStep = GuiElementPageSurface.pageheight + 1;
			float x = 0;
			float y = 0;
			for (PositionableItem page : arranged_pages) {
				page.x = x;
				page.y = y;
				x += xStep;
				if (x + xStep > leftsize - 53) { //XXX: sorting has width hardcoded
					x = 0;
					y += yStep;
				}
			}
		}

		public void sort(final Comparator<ItemStack> comparator) {
			Collections.sort(arranged_pages, new Comparator<PositionableItem>() {
				@Override
				public int compare(PositionableItem arg0, PositionableItem arg1) {
					return comparator.compare(arg0.itemstack, arg1.itemstack);
				}
			});
		}

		@Override
		public void onClick(GuiElementButton caller) {
			if (caller.getId().equals("AZ")) {
				sorttype = SortingUtils.ComparatorItemSymbolAlphabetical.instance;
				updateCollection();
			} else if (caller.getId().equals("ALL")) {
				showall = !showall;
				updateCollection();
			}
		}

		@Override
		public boolean getState(String id) {
			if (id.equals("AZ")) return sorttype == SortingUtils.ComparatorItemSymbolAlphabetical.instance;
			if (id.equals("ALL")) return showall;
			return false;
		}

		@Override
		public void place(boolean single) {
			NBTTagCompound nbttagcompound = new NBTTagCompound();
			nbttagcompound.setByte("AddToNotebook", container.getActiveNotebookSlot());
			nbttagcompound.setBoolean("Single", single);
			MystcraftPacketHandler.bus.sendToServer(MPacketGuiMessage.createPacket(mc.thePlayer.openContainer.windowId, nbttagcompound));
			container.processMessage(mc.thePlayer, nbttagcompound);
		}

		@Override
		public void pickup(PositionableItem collectionelement) {
			if (collectionelement.count <= 0) return;
			int index = collectionelement.slotId;
			NBTTagCompound nbttagcompound = new NBTTagCompound();
			nbttagcompound.setInteger("TakeFromSurface", index);
			MystcraftPacketHandler.bus.sendToServer(MPacketGuiMessage.createPacket(mc.thePlayer.openContainer.windowId, nbttagcompound));
			container.processMessage(mc.thePlayer, nbttagcompound);
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

	public class PageHandlerTarget implements IGuiPageProvider {
		@Override
		public ItemStack getPageItemStack(GuiElementPage elem) {
			return container.getTarget();
		}
	}

	private ContainerWritingDesk	container;

	private int						mainTop;
	private int						guiCenter;

	String							text			= null;

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

		GuiElementTextField txt_box = null;

		TextBoxHandlerSearch txt_search = new TextBoxHandlerSearch();
		txt_box = new GuiElementTextField(txt_search, txt_search, "SearchBox", guiLeft + 58 + (buttonssizeY + 2) * 2, guiTop, leftsize - 53 - (buttonssizeY + 2) * 2, buttonssizeY);
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

		GuiElementNotebookTabs notebooktabs = new GuiElementNotebookTabs(new NotebookTabsHandler(), guiLeft, mainTop, 58, ySize);
		//txt_search.addListener(notebooktabs);
		elements.add(notebooktabs);

		PositionedPagesProvider pagesmanager = new PositionedPagesProvider();
		GuiElementPageSurface surface = new GuiElementPageSurface(pagesmanager, this.mc, guiLeft + 58, mainTop, leftsize - 53, windowsizeY);
		txt_search.addListener(surface);
		elements.add(surface);

		GuiElementButton btn_sortA = new GuiElementButtonToggle(pagesmanager, pagesmanager, "AZ", guiLeft + 58, guiTop, buttonssizeY, buttonssizeY);
		btn_sortA.setText("AZ");
		btn_sortA.setTooltip(Arrays.asList("Sort Alphabetically"));
		elements.add(btn_sortA);
		GuiElementButton btn_allsym = new GuiElementButtonToggle(pagesmanager, pagesmanager, "ALL", guiLeft + 58 + buttonssizeY, guiTop, buttonssizeY, buttonssizeY);
		btn_allsym.setText("ALL");
		btn_allsym.setTooltip(Arrays.asList("Show all Symbols"));
		elements.add(btn_allsym);
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
