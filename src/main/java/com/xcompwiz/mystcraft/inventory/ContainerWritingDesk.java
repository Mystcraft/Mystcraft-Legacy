package com.xcompwiz.mystcraft.inventory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraftforge.fluids.FluidStack;

import com.xcompwiz.mystcraft.api.item.IItemPageProvider;
import com.xcompwiz.mystcraft.api.linking.ILinkInfo;
import com.xcompwiz.mystcraft.inventory.PageCollectionPageReceiver.IItemProvider;
import com.xcompwiz.mystcraft.item.ItemAgebook;
import com.xcompwiz.mystcraft.item.ItemLinking;
import com.xcompwiz.mystcraft.item.LinkItemUtils;
import com.xcompwiz.mystcraft.linking.DimensionUtils;
import com.xcompwiz.mystcraft.linking.LinkListenerManager;
import com.xcompwiz.mystcraft.network.IGuiMessageHandler;
import com.xcompwiz.mystcraft.network.packet.MPacketGuiMessage;
import com.xcompwiz.mystcraft.tileentity.TileEntityDesk;

public class ContainerWritingDesk extends ContainerBase implements IGuiMessageHandler, IBookContainer, IItemProvider {
	public static class Messages {
		public static final String	LinkPermitted				= "LinkPermitted";
		public static final String	SetCurrentPage				= "SetCurrentPage";
		public static final String	SetTitle					= "SetTitle";
		public static final String	Link						= "Link";
		public static final String	RemoveFromCollection		= "RemoveFromCollection";
		public static final String	RemoveFromOrderedCollection	= "RemoveFromOrderedCollection";
		public static final String	AddToCollection				= "AddToCollection";
		public static final String	AddToSurface				= "AddToSurface";
		public static final String	WriteSymbol					= "WriteSymbol";
		public static final String	SetActiveNotebook			= "SetActiveNotebook";
		public static final String	SetFirstNotebook			= "SetFirstNotebook";
		public static final String	SetFluid					= "SetFluid";
		public static final String	TakeFromSlider				= "TakeFromSlider";
		public static final String	InsertHeldAt				= "InsertHeldAt";
	}

	private static final int	xShift				= 228 + 5;
	private static final int	yShift				= 20;
	private static final int	tabslots			= 4;

	//XXX: Remove activeslot from container
	private byte				activeslot;
	private byte				firstslot;

	private TileEntityDesk		tileentity;
	private FluidStack			fluid;
	private ItemStack			currentpage;
	private int					currentpageIndex;
	private int					pagecount;
	private ILinkInfo			cached_linkinfo;
	private Boolean				cached_permitted;
	private String				cached_title		= "";

	private EntityPlayer		player;
	private FluidTankProvider	fluidDataContainer	= new FluidTankProvider();

	public ContainerWritingDesk(InventoryPlayer inventoryplayer, TileEntityDesk te) {
		this.tileentity = te;
		this.activeslot = 0;
		this.firstslot = 0;
		this.currentpageIndex = 0;
		this.pagecount = 0;
		this.player = inventoryplayer.player;

		fluidDataContainer.setMax(tileentity.getTankInfo(null)[0].capacity);

		for (int i = 0; i < tabslots; ++i) {
			SlotFiltered slot = new SlotFiltered(tileentity, i + tileentity.getMainInventorySize(), 37, 14 + i * 37 + yShift);// , ItemFolder.instance.getIconFromDamage(0), ItemFolder.instance.getTextureFile()));
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

		ITargetInventory pagecollectionreceiver = new PageCollectionPageReceiver(this, player);

		internal.pushTargetFront(maininv);
		internal.pushTargetFront(hotbar);
		maininv.pushTargetFront(hotbar);
		maininv.pushTargetFront(internal);
		maininv.pushTargetFront(pagecollectionreceiver);
		hotbar.pushTargetFront(maininv);
		hotbar.pushTargetFront(internal);
		hotbar.pushTargetFront(pagecollectionreceiver);

		collections.add(internal);
		collections.add(maininv);
		collections.add(hotbar);
	}

	@Override
	public ItemStack getPageCollection() {
		return this.getTabSlot(this.activeslot);
	}

	private void updateSurfaceTabSlots() {
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
			nbttagcompound.setInteger(Messages.SetCurrentPage, currentpageIndex);
			packets.add(MPacketGuiMessage.createPacket(this.windowId, nbttagcompound));
		}
		for (int slotId = 0; slotId < this.inventorySlots.size(); ++slotId) {
			actual = ((Slot) this.inventorySlots.get(slotId)).getStack();
			stored = (ItemStack) this.inventoryItemStacks.get(slotId);

			if (!ItemStack.areItemStacksEqual(stored, actual)) {
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
			nbttagcompound.setTag(Messages.SetFluid, fluidnbt);
			packets.add(MPacketGuiMessage.createPacket(this.windowId, nbttagcompound));
		}
		if (cached_permitted == null) {
			cached_permitted = checkLinkPermitted();
			if (cached_permitted != null) {
				NBTTagCompound nbttagcompound = new NBTTagCompound();
				nbttagcompound.setBoolean(Messages.LinkPermitted, cached_permitted);
				packets.add(MPacketGuiMessage.createPacket(this.windowId, nbttagcompound));
			}
		}
		String temp_title = tileentity.getTargetString(player);
		if (this.cached_title != temp_title) {
			cached_title = temp_title;

			NBTTagCompound nbttagcompound = new NBTTagCompound();
			nbttagcompound.setString(Messages.SetTitle, cached_title);
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
		if (data.hasKey(Messages.LinkPermitted)) {
			cached_permitted = data.getBoolean(Messages.LinkPermitted);
		}
		if (data.hasKey(Messages.SetTitle)) {
			cached_title = data.getString(Messages.SetTitle);
			this.tileentity.setBookTitle(player, cached_title);
		}
		if (data.hasKey(Messages.Link)) {
			if (tileentity != null) {
				tileentity.link(player);
			}
		}
		if (data.hasKey(Messages.RemoveFromCollection)) {
			if (player.inventory.getItemStack() != null) return;
			ItemStack page = ItemStack.loadItemStackFromNBT(data.getCompoundTag(Messages.RemoveFromCollection));
			ItemStack itemstack = tileentity.removePageFromSurface(player, this.getTabSlot(this.activeslot), page);
			player.inventory.setItemStack(itemstack);
		}
		if (data.hasKey(Messages.RemoveFromOrderedCollection)) {
			if (player.inventory.getItemStack() != null) return;
			int index = data.getInteger(Messages.RemoveFromOrderedCollection);
			player.inventory.setItemStack(tileentity.removePageFromSurface(player, this.getTabSlot(this.activeslot), index));
		}
		if (data.hasKey(Messages.AddToCollection)) {
			if (player.inventory.getItemStack() == null) return;
			byte slot = data.getByte(Messages.AddToCollection);
			if (tileentity.getTabItem(slot) == null) return;
			boolean single = data.getBoolean("Single");
			if (single) {
				ItemStack stack = player.inventory.getItemStack();
				ItemStack clone = stack.copy();
				clone.stackSize = 1;
				stack.stackSize -= 1;
				if (stack.stackSize <= 0) stack = null;
				player.inventory.setItemStack(stack);
			}
			player.inventory.setItemStack(tileentity.addPageToCollection(player, tileentity.getTabItem(slot), player.inventory.getItemStack()));
		}
		if (data.hasKey(Messages.AddToSurface)) {
			if (player.inventory.getItemStack() == null) return;
			if (!data.hasKey("Index")) return;
			byte slot = data.getByte(Messages.AddToSurface);
			if (tileentity.getTabItem(slot) == null) return;
			boolean single = data.getBoolean("Single");
			int index = data.getInteger("Index");
			if (single) {
				ItemStack stack = player.inventory.getItemStack();
				ItemStack clone = stack.copy();
				clone.stackSize = 1;
				ItemStack returned = tileentity.placePageOnSurface(player, tileentity.getTabItem(slot), clone, index);
				if (returned == null || stack.stackSize == 1) {
					stack.stackSize -= 1;
					if (stack.stackSize <= 0) stack = returned;
					player.inventory.setItemStack(stack);
				} else {
					tileentity.placePageOnSurface(player, tileentity.getTabItem(slot), returned, index);
				}
			} else {
				player.inventory.setItemStack(tileentity.placePageOnSurface(player, tileentity.getTabItem(slot), player.inventory.getItemStack(), index));
			}
		}
		if (data.hasKey(Messages.WriteSymbol)) {
			tileentity.writeSymbol(player, data.getString(Messages.WriteSymbol));
		}
		//XXX: This is weird, as it's only meaningful client-side
		if (data.hasKey(Messages.SetActiveNotebook)) {
			this.activeslot = data.getByte(Messages.SetActiveNotebook);
		}
		if (data.hasKey(Messages.SetFirstNotebook)) {
			this.firstslot = data.getByte(Messages.SetFirstNotebook);
			if (firstslot < 0) firstslot = 0;
			if (firstslot >= tileentity.getMaxSurfaceTabCount()) firstslot = 0;
			updateSurfaceTabSlots();
		}
		if (data.hasKey(Messages.SetFluid)) {
			if (this.tileentity.getWorldObj().isRemote) {
				this.tileentity.setInk(FluidStack.loadFluidStackFromNBT(data.getCompoundTag(Messages.SetFluid)));
				this.fluid = this.tileentity.getInk();
				fluidDataContainer.setFluid(fluid);
			}
		}
		if (data.hasKey(Messages.SetCurrentPage)) {
			if (this.tileentity.getWorldObj().isRemote) {
				this.setCurrentPageIndex(data.getInteger(Messages.SetCurrentPage));
				cached_linkinfo = null;
			}
		}
		if (data.hasKey(Messages.TakeFromSlider)) {
			ItemStack target = tileentity.getTarget();
			if (target == null) return;
			if (player.inventory.getItemStack() != null) return;
			int index = data.getInteger(Messages.TakeFromSlider);
			player.inventory.setItemStack(InventoryFolder.removeItem(target, index));//FIXME: Reference to InventoryFolder outside folder
		}
		if (data.hasKey(Messages.InsertHeldAt)) {
			ItemStack target = tileentity.getTarget();
			if (target == null) return;
			ItemStack stack = player.inventory.getItemStack();
			if (stack == null) return;
			if (stack.stackSize > 1) return;
			int index = data.getInteger(Messages.InsertHeldAt);
			player.inventory.setItemStack(InventoryFolder.setItem(target, index, stack));//FIXME: Reference to InventoryFolder outside folder
		}
	}

	public ItemStack getTabSlot(byte slot) {
		return tileentity.getTabItem(slot);
	}

	public byte getActiveTabSlot() {
		return activeslot;
	}

	public byte getFirstTabSlot() {
		return firstslot;
	}

	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		return tileentity.isUseableByPlayer(entityplayer);
	}

	private List<ItemStack> getPageList() {
		ItemStack book = getBook();
		if (book == null) return null;
		if (book.getItem() instanceof IItemPageProvider) { return ((IItemPageProvider) book.getItem()).getPageList(this.player, book); }
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
		Integer dim = LinkItemUtils.getTargetDimension(getBook());
		if (dim == null) return false;
		return DimensionUtils.isDimensionVisited(dim);
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
		if (ItemAgebook.isNewAgebook(getBook())) return true;
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

	public List<ItemStack> getBookPageList() {
		if (tileentity != null) { return tileentity.getBookPageList(this.player); }
		return null;
	}
}
