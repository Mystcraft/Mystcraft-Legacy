package com.xcompwiz.mystcraft.inventory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fluids.FluidStack;

import com.xcompwiz.mystcraft.Mystcraft;
import com.xcompwiz.mystcraft.api.linking.ILinkInfo;
import com.xcompwiz.mystcraft.item.ItemAgebook;
import com.xcompwiz.mystcraft.item.ItemLinking;
import com.xcompwiz.mystcraft.item.ItemNotebook;
import com.xcompwiz.mystcraft.linking.LinkListenerManager;
import com.xcompwiz.mystcraft.linking.LinkOptions;
import com.xcompwiz.mystcraft.network.IGuiMessageHandler;
import com.xcompwiz.mystcraft.network.MPacketAgeData;
import com.xcompwiz.mystcraft.network.MPacketGuiMessage;
import com.xcompwiz.mystcraft.oldapi.PositionableItem;
import com.xcompwiz.mystcraft.page.IItemPageProvider.SortType;
import com.xcompwiz.mystcraft.tileentity.TileEntityDesk;
import com.xcompwiz.mystcraft.world.agedata.AgeData;

public class ContainerWritingDesk extends ContainerMyst implements IGuiMessageHandler, IBookContainer {
	private static final int						xShift				= 228 + 5;
	private static final int						yShift				= 20;
	private static final int						tabslots			= 4;
	private static final HashMap<String, SortType>	sortmap				= new HashMap<String, SortType>();

	private byte									activeslot;
	private byte									firstslot;

	private TileEntityDesk							tileentity;
	private FluidStack								fluid;
	private ItemStack								currentpage;
	private int										currentpageIndex;
	private int										pagecount;
	private ILinkInfo								cached_linkinfo;
	private Boolean									cached_permitted;
	private String									cached_title		= "";

	private EntityPlayer							player;
	private FluidTankProvider						fluidDataContainer	= new FluidTankProvider();

	public ContainerWritingDesk(InventoryPlayer inventoryplayer, TileEntityDesk te) {
		this.tileentity = te;
		this.activeslot = 0;
		this.firstslot = 0;
		this.currentpageIndex = 0;
		this.pagecount = 0;
		this.player = inventoryplayer.player;

		fluidDataContainer.setMax(tileentity.getTankInfo(null)[0].capacity);

		for (int i = 0; i < tabslots; ++i) {
			SlotFiltered slot = new SlotFiltered(tileentity, i + tileentity.getMainInventorySize(), 37, 14 + i * 37 + yShift);// ,
																																// ItemNotebook.instance.getIconFromDamage(0),
																																// ItemNotebook.instance.getTextureFile()));
			slot.setSlotStackLimit(1);
			addSlotToContainer(slot);
		}

		addSlotToContainer(new SlotFiltered(tileentity, 0, 8 + xShift, 60 + yShift));
		addSlotToContainer(new SlotFiltered(tileentity, 1, 8 + xShift, 8 + yShift));// ,
																					// Items.paper.getIconFromDamage(0),
																					// Items.paper.getTextureFile()));
		addSlotToContainer(new SlotFiltered(tileentity, 2, 152 + xShift, 8 + yShift));// ,
																						// Items.dyePowder.getIconFromDamage(0),
																						// Items.dyePowder.getTextureFile()));
		addSlotToContainer(new SlotFiltered(tileentity, 3, 152 + xShift, 60 + yShift));// ,
																						// Items.glass_bottle.getIconFromDamage(0),
																						// Items.glass_bottle.getTextureFile()));

		for (int i = 0; i < 3; i++) {
			for (int k = 0; k < 9; k++) {
				addSlotToContainer(new Slot(inventoryplayer, k + i * 9 + 9, 8 + k * 18 + xShift, 84 + i * 18 + yShift));
			}
		}

		for (int j = 0; j < 9; j++) {
			addSlotToContainer(new Slot(inventoryplayer, j, 8 + j * 18 + xShift, 142 + yShift));
		}

		SlotCollection internal = null;
		SlotCollection maininv = null;
		SlotCollection hotbar = null;
		internal = new SlotCollection(this, 0, tabslots + 4);
		maininv = new SlotCollection(this, tabslots + 4, tabslots + 4 + 27);
		hotbar = new SlotCollection(this, tabslots + 4 + 27, tabslots + 4 + 27 + 9);

		internal.pushTargetFront(maininv);
		internal.pushTargetFront(hotbar);
		maininv.pushTargetFront(hotbar);
		maininv.pushTargetFront(internal);
		hotbar.pushTargetFront(maininv);
		hotbar.pushTargetFront(internal);

		collections.add(internal);
		collections.add(maininv);
		collections.add(hotbar);

		this.getActiveNotebook();
	}

	private void updateNotebookSlots() {
		for (int i = 0; i < tabslots; ++i) {
			((SlotFiltered) this.inventorySlots.get(i)).setSlotIndex(i + tileentity.getMainInventorySize() + firstslot);
		}
	}

	@Override
	public void detectAndSendChanges() {
		List<Packet> packets = new ArrayList<Packet>();
		ItemStack actual = ((Slot) this.inventorySlots.get(tabslots)).getStack();
		ItemStack stored = (ItemStack) this.inventoryItemStacks.get(tabslots);
		if (!ItemStack.areItemStacksEqual(actual, stored)) {
			cached_linkinfo = null;
			cached_permitted = null;
			NBTTagCompound nbttagcompound = new NBTTagCompound();
			nbttagcompound.setInteger("SetCurrentPage", currentpageIndex);
			packets.add(MPacketGuiMessage.createPacket(this.windowId, nbttagcompound));
		}
		for (int slotId = 0; slotId < this.inventorySlots.size(); ++slotId) {
			actual = ((Slot) this.inventorySlots.get(slotId)).getStack();
			stored = (ItemStack) this.inventoryItemStacks.get(slotId);

			if (!ItemStack.areItemStacksEqual(stored, actual)) {
				if (actual != null && actual.stackTagCompound != null && actual.getItem() == ItemAgebook.instance) {
					if (ItemAgebook.getAgeData(player.worldObj, actual) != null) {
						packets.add(MPacketAgeData.getDataPacket(LinkOptions.getDimensionUID(actual.stackTagCompound)));
					}
				}
				stored = actual == null ? null : actual.copy();
				this.inventoryItemStacks.set(slotId, stored);

				for (int var4 = 0; var4 < this.crafters.size(); ++var4) {
					((ICrafting) this.crafters.get(var4)).sendSlotContents(this, slotId, stored);
				}
			}
		}
		FluidStack temp = tileentity.getInk();
		if (hasFluidChanged(fluid, temp)) {
			if (temp != null) temp = temp.copy();
			fluid = temp;
			fluidDataContainer.setFluid(fluid);
			NBTTagCompound nbttagcompound = new NBTTagCompound();
			NBTTagCompound fluidnbt = new NBTTagCompound();
			if (fluid != null) fluid.writeToNBT(fluidnbt);
			nbttagcompound.setTag("SetFluid", fluidnbt);
			packets.add(MPacketGuiMessage.createPacket(this.windowId, nbttagcompound));
		}
		if (cached_permitted == null) {
			cached_permitted = checkLinkPermitted();
			if (cached_permitted != null) {
				NBTTagCompound nbttagcompound = new NBTTagCompound();
				nbttagcompound.setBoolean("LinkPermitted", cached_permitted);
				packets.add(MPacketGuiMessage.createPacket(this.windowId, nbttagcompound));
			}
		}
		String temp_title = tileentity.getTargetString(player);
		if (this.cached_title != temp_title) {
			cached_title = temp_title;

			NBTTagCompound nbttagcompound = new NBTTagCompound();
			nbttagcompound.setString("SetTitle", cached_title);
			packets.add(MPacketGuiMessage.createPacket(this.windowId, nbttagcompound));
		}
		if (packets.size() > 0) {
			for (int var4 = 0; var4 < this.crafters.size(); ++var4) {
				ICrafting crafter = ((ICrafting) this.crafters.get(var4));
				if (crafter instanceof EntityPlayerMP) {
					EntityPlayerMP player = (EntityPlayerMP) crafter;
					for (Packet pkt : packets) {
						player.playerNetServerHandler.sendPacket(pkt);
					}
				}
			}
		}
	}

	private boolean hasFluidChanged(FluidStack fluid, FluidStack temp) {
		if (fluid == null && temp == null) return false;
		if (fluid == null && temp != null) return true;
		if (fluid != null && temp == null) return true;
		if (!fluid.isFluidStackIdentical(temp)) return true;
		return false;
	}

	@Override
	public void processMessage(EntityPlayer player, NBTTagCompound data) {
		if (data.hasKey("LinkPermitted")) {
			cached_permitted = data.getBoolean("LinkPermitted");
		}
		if (data.hasKey("SetTitle")) {
			cached_title = data.getString("SetTitle");
			this.tileentity.setBookTitle(player, cached_title);
		}
		if (data.hasKey("Link")) {
			if (tileentity != null) {
				tileentity.link(player);
			}
		}
		if (data.hasKey("TakeFromSurface")) {
			if (player.inventory.getItemStack() != null) return;
			int index = data.getInteger("TakeFromSurface");
			player.inventory.setItemStack(tileentity.removePageFromSurface(player, this.getActiveNotebook(), index));
		}
		if (data.hasKey("PlaceHeldAt")) {
			if (player.inventory.getItemStack() == null) return;
			NBTTagCompound coords = data.getCompoundTag("PlaceHeldAt");
			boolean single = data.getBoolean("Single");
			if (single) {
				ItemStack stack = player.inventory.getItemStack();
				ItemStack clone = stack.copy();
				clone.stackSize = 1;
				if (tileentity.placePageInSurface(player, this.getActiveNotebook(), clone, coords.getFloat("X"), coords.getFloat("Y")) == null) {
					stack.stackSize -= 1;
					if (stack.stackSize <= 0) stack = null;
					player.inventory.setItemStack(stack);
				}
			} else {
				player.inventory.setItemStack(tileentity.placePageInSurface(player, this.getActiveNotebook(), player.inventory.getItemStack(), coords.getFloat("X"), coords.getFloat("Y")));
			}
		}
		if (data.hasKey("AddToNotebook")) {
			if (player.inventory.getItemStack() == null) return;
			byte slot = data.getByte("AddToNotebook");
			if (tileentity.getNotebook(slot) == null) return;
			boolean single = data.getBoolean("Single");
			if (single) {
				ItemStack stack = player.inventory.getItemStack();
				ItemStack clone = stack.copy();
				clone.stackSize = 1;
				if (tileentity.addPage(player, tileentity.getNotebook(slot), clone) == null) {
					stack.stackSize -= 1;
					if (stack.stackSize <= 0) stack = null;
					player.inventory.setItemStack(stack);
				}
			} else {
				player.inventory.setItemStack(tileentity.addPage(player, tileentity.getNotebook(slot), player.inventory.getItemStack()));
			}
		}
		if (data.hasKey("WriteSymbol")) {
			tileentity.writeSymbol(player, data.getString("WriteSymbol"));
		}
		if (data.hasKey("SortNotebook")) {
			SortType type = sortmap.get(data.getString("SortType")); // TODO: (PageSorting) SortType mapping
			if (type == null) type = SortType.ALPHABETICAL;
			tileentity.sortNotebook(this.getActiveNotebook(), (short) 155, type); //FIXME: (PageSorting) Sort client-side (and copy item to server?)
		}
		if (data.hasKey("SetActiveNotebook")) {
			this.activeslot = data.getByte("SetActiveNotebook");
			this.getActiveNotebook();
		}
		if (data.hasKey("SetFirstNotebook")) {
			this.firstslot = data.getByte("SetFirstNotebook");
			if (firstslot < 0) firstslot = 0;
			if (firstslot >= tileentity.getMaxNotebookCount()) firstslot = 0;
			updateNotebookSlots();
		}
		if (data.hasKey("SetFluid")) {
			if (this.tileentity.getWorldObj().isRemote) {
				this.tileentity.setInk(FluidStack.loadFluidStackFromNBT(data.getCompoundTag("SetFluid")));
				this.fluid = this.tileentity.getInk();
				fluidDataContainer.setFluid(fluid);
			}
		}
		if (data.hasKey("SetCurrentPage")) {
			if (this.tileentity.getWorldObj().isRemote) {
				this.setCurrentPageIndex(data.getInteger("SetCurrentPage"));
			}
		}
		if (data.hasKey("TakeFromSlider")) {
			ItemStack target = tileentity.getTarget();
			if (target == null) return;
			if (player.inventory.getItemStack() != null) return;
			int index = data.getInteger("TakeFromSlider");
			player.inventory.setItemStack(InventoryNotebook.removeItem(target, index));
		}
		if (data.hasKey("InsertHeldAt")) {
			ItemStack target = tileentity.getTarget();
			if (target == null) return;
			ItemStack stack = player.inventory.getItemStack();
			if (stack == null) return;
			if (stack.stackSize > 1) return;
			int index = data.getInteger("InsertHeldAt");
			player.inventory.setItemStack(InventoryNotebook.setItem(target, index, stack));
		}
	}

	private ItemStack getActiveNotebook() {
		return tileentity.getNotebook(activeslot);
	}

	public ItemStack getNotebook(byte slot) {
		return tileentity.getNotebook(slot);
	}

	public byte getActiveNotebookSlot() {
		return activeslot;
	}

	public byte getFirstNotebookSlot() {
		return firstslot;
	}

	@Override
	public void updateProgressBar(int i, int j) {}

	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		return tileentity.isUseableByPlayer(entityplayer);
	}

	private List<ItemStack> getPageList() {
		ItemStack book = getBook();
		if (book == null) return null;
		if (book.getItem() instanceof ItemLinking) { //FIXME: (PageStorage) This needs to change with the gui and page storage rewrite
			return ((ItemLinking) book.getItem()).getPageList(this.player, book);
		}
		if (book.getItem() == ItemNotebook.instance) { return InventoryNotebook.getItems(book); }
		return null;
	}

	@Override
	public void setCurrentPageIndex(int index) {
		currentpage = null;
		currentpageIndex = 0;
		if (index < 0) index = 0;
		List<ItemStack> pagelist = getPageList();
		if (pagelist == null) {
			index = 0;
			pagecount = 0;
		} else {
			pagecount = pagelist.size();
			if (index >= pagecount) {
				index = pagecount;
			} else {
				currentpage = pagelist.get(index);
			}
		}
		currentpageIndex = index;
	}

	@Override
	public ItemStack getCurrentPage() {
		if (currentpage == null) setCurrentPageIndex(currentpageIndex);
		return currentpage;
	}

	@Override
	public int getCurrentPageIndex() {
		return currentpageIndex;
	}

	@Override
	public int getPageCount() {
		return pagecount;
	}

	@Override
	public boolean hasBookSlot() {
		return false;
	}

	@Override
	public boolean isTargetWorldVisited() {
		ItemStack book = getBook();
		if (book == null) return false;
		if (book.stackTagCompound == null) return false;
		if (book.getItem() instanceof ItemLinking) { //XXX: (Helper) This should be broken into some kind of helper
			int dim = LinkOptions.getDimensionUID(book.stackTagCompound);
			if (DimensionManager.getProviderType(dim) == Mystcraft.providerId) {
				AgeData agedata = AgeData.getAge(dim, tileentity.getWorldObj().isRemote);
				if (agedata == null) return false;
				return agedata.isVisited();
			}
			return true;
		}
		return false;
	}

	@Override
	public ItemStack getBook() {
		if (tileentity != null) {
			ItemStack itemstack = tileentity.getTarget();
			if (itemstack == null) return null;
			if (itemstack.getItem() instanceof ItemLinking) { return itemstack; }
		}
		return null;
	}

	public ItemStack getTarget() {
		ItemStack itemstack = null;
		if (tileentity != null) {
			itemstack = tileentity.getTarget();
		}
		return itemstack;
	}

	@Override
	public ILinkInfo getLinkInfo() {
		ItemStack book = getBook();
		if (book == null || !(book.getItem() instanceof ItemLinking)) return null;
		if (cached_linkinfo == null) {
			cached_linkinfo = ((ItemLinking) book.getItem()).getLinkInfo(book);
		}
		return cached_linkinfo;
	}

	@Override
	public boolean isLinkPermitted() {
		ILinkInfo linkinfo = getLinkInfo();
		if (linkinfo == null) {
			cached_permitted = null;
		}
		if (cached_permitted == null) { return false; }
		return cached_permitted;
	}

	private boolean checkLinkPermitted() {
		ILinkInfo linkinfo = getLinkInfo();
		if (linkinfo == null) { return false; }
		return LinkListenerManager.isLinkPermitted(tileentity.getWorldObj(), player, linkinfo);
	}

	@Override
	public String getBookTitle() {
		ItemStack book = getBook();
		if (book == null || !(book.getItem() instanceof ItemLinking)) return "";
		return ((ItemLinking) book.getItem()).getTitle(book);
	}

	@Override
	public Collection<String> getBookAuthors() {
		ItemStack book = getBook();
		if (book == null) return Collections.EMPTY_SET;
		return ((ItemLinking) book.getItem()).getAuthors(book);
	}

	public IFluidTankProvider getInkTankProvider() {
		return fluidDataContainer;
	}

	public String getTargetName() {
		return cached_title;
	}

	public List<PositionableItem> getSurfacePages(byte notebookSlot) {
		if (tileentity != null) { return tileentity.getSurfacePages(this.player, notebookSlot); }
		return null;
	}

	public List<ItemStack> getBookPageList() {
		if (tileentity != null) { return tileentity.getBookPageList(this.player); }
		return null;
	}
}
