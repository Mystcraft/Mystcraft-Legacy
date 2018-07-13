package com.xcompwiz.mystcraft.inventory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import com.xcompwiz.mystcraft.api.item.IItemOrderablePageProvider;
import com.xcompwiz.mystcraft.api.item.IItemPageProvider;
import com.xcompwiz.mystcraft.api.linking.ILinkInfo;
import com.xcompwiz.mystcraft.inventory.PageCollectionPageReceiver.IItemProvider;
import com.xcompwiz.mystcraft.item.ItemAgebook;
import com.xcompwiz.mystcraft.item.ItemLinking;
import com.xcompwiz.mystcraft.item.LinkItemUtils;
import com.xcompwiz.mystcraft.linking.DimensionUtils;
import com.xcompwiz.mystcraft.linking.LinkListenerManager;
import com.xcompwiz.mystcraft.network.IGuiMessageHandler;
import com.xcompwiz.mystcraft.network.MystcraftPacketHandler;
import com.xcompwiz.mystcraft.network.packet.MPacketGuiMessage;
import com.xcompwiz.mystcraft.tileentity.IOInventory;
import com.xcompwiz.mystcraft.tileentity.TileEntityDesk;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class ContainerWritingDesk extends ContainerBase implements IGuiMessageHandler, IBookContainer, IItemProvider {

	public static class Messages {

		public static final String LinkPermitted = "LinkPermitted";
		public static final String SetCurrentPage = "SetCurrentPage";
		public static final String SetTitle = "SetTitle";
		public static final String Link = "Link";
		public static final String RemoveFromCollection = "RemoveFromCollection";
		public static final String RemoveFromOrderedCollection = "RemoveFromOrderedCollection";
		public static final String AddToTab = "AddToCollection";
		public static final String AddToSurface = "AddToSurface";
		public static final String WriteSymbol = "WriteSymbol";
		public static final String SetActiveNotebook = "SetActiveNotebook";
		public static final String SetFirstNotebook = "SetFirstNotebook";
		public static final String TakeFromSlider = "TakeFromSlider";
		public static final String InsertHeldAt = "InsertHeldAt";

	}

	private static final int xShift = 228 + 5;
	private static final int yShift = 20;
	private static final int tabslots = 4;

	//XXX: Remove activeslot from container
	private byte activeslot;
	private byte firstslot;

	private TileEntityDesk tileentity;
	private FluidStack fluid;

	@Nonnull
	private ItemStack currentpage = ItemStack.EMPTY;
	private int currentpageIndex;
	private int pagecount;
	private ILinkInfo cached_linkinfo;
	private boolean cached_permitted;
	@Nonnull
	private String cached_title = "";

	private EntityPlayer player;
	private FluidTankProvider fluidDataContainer = new FluidTankProvider();

	public ContainerWritingDesk(InventoryPlayer inventoryplayer, TileEntityDesk te) {
		this.tileentity = te;
		this.activeslot = 0;
		this.firstslot = 0;
		this.currentpageIndex = 0;
		this.pagecount = 0;
		this.player = inventoryplayer.player;

		fluidDataContainer.setTank(te.getInkwell());
		IOInventory inventory = te.getContainerItemHandler();

		for (int i = 0; i < tabslots; ++i) {
			SlotFiltered slot = new SlotFiltered(inventory, tileentity, i + tileentity.getMainInventorySize(), 37, 14 + i * 37 + yShift);
			slot.setSlotStackLimit(1);
			addSlotToContainer(slot);
		}

		SlotFiltered slot = new SlotFiltered(inventory, tileentity, 0, 8 + xShift, 60 + yShift);
		slot.setSlotStackLimit(1);
		addSlotToContainer(slot);
		addSlotToContainer(new SlotFiltered(inventory, tileentity, 1, 8 + xShift, 8 + yShift));
		addSlotToContainer(new SlotFiltered(inventory, tileentity, 2, 152 + xShift, 8 + yShift));
		addSlotToContainer(new SlotFiltered(inventory, tileentity, 3, 152 + xShift, 60 + yShift));

		for (int i = 0; i < 3; i++) {
			for (int k = 0; k < 9; k++) {
				addSlotToContainer(new Slot(inventoryplayer, k + i * 9 + 9, 8 + k * 18 + xShift, 84 + i * 18 + yShift));
			}
		}

		for (int j = 0; j < 9; j++) {
			addSlotToContainer(new Slot(inventoryplayer, j, 8 + j * 18 + xShift, 142 + yShift));
		}

		SlotCollection internal = new SlotCollection(this, 0, tabslots + 4);
		SlotCollection maininv = new SlotCollection(this, tabslots + 4, tabslots + 4 + 27);
		SlotCollection hotbar = new SlotCollection(this, tabslots + 4 + 27, tabslots + 4 + 27 + 9);

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
		super.detectAndSendChanges();
		List<IMessage> packets = new ArrayList<>();
		ItemStack actual = this.inventorySlots.get(tabslots).getStack();
		ItemStack stored = this.inventoryItemStacks.get(tabslots);
		if (!ItemStack.areItemStacksEqual(actual, stored)) {
			cached_linkinfo = null;
			cached_permitted = false;
			NBTTagCompound nbttagcompound = new NBTTagCompound();
			nbttagcompound.setInteger(Messages.SetCurrentPage, currentpageIndex);
			packets.add(new MPacketGuiMessage(this.windowId, nbttagcompound));
		}
		for (int slotId = 0; slotId < this.inventorySlots.size(); ++slotId) {
			actual = this.inventorySlots.get(slotId).getStack();
			stored = this.inventoryItemStacks.get(slotId);

			if (!ItemStack.areItemStacksEqual(stored, actual)) {
				stored = actual.isEmpty() ? ItemStack.EMPTY : actual.copy();
				this.inventoryItemStacks.set(slotId, stored);

				for (IContainerListener listener : this.listeners) {
					listener.sendSlotContents(this, slotId, stored);
				}
			}
		}
		boolean perm = checkLinkPermitted();
		if (cached_permitted != perm) {
			cached_permitted = perm;
			NBTTagCompound nbttagcompound = new NBTTagCompound();
			nbttagcompound.setBoolean(Messages.LinkPermitted, cached_permitted);
			packets.add(new MPacketGuiMessage(this.windowId, nbttagcompound));
		}
		String temp_title = tileentity.getTargetString(player);
		if (!this.cached_title.equals(temp_title)) {
			cached_title = temp_title;

			NBTTagCompound nbttagcompound = new NBTTagCompound();
			nbttagcompound.setString(Messages.SetTitle, cached_title);
			packets.add(new MPacketGuiMessage(this.windowId, nbttagcompound));
		}
		if (packets.size() > 0) {
			for (IContainerListener listener : this.listeners) {
				if (listener instanceof EntityPlayerMP) {
					for (IMessage message : packets) {
						MystcraftPacketHandler.CHANNEL.sendTo(message, (EntityPlayerMP) listener);
					}
				}
			}
		}
	}

	private boolean hasFluidChanged(FluidStack fluid, FluidStack temp) {
		if (fluid == null && temp == null)
			return false;
		if (fluid == null && temp != null)
			return true;
		if (fluid != null && temp == null)
			return true;
		if (!fluid.isFluidStackIdentical(temp))
			return true;
		return false;
	}

	@Override
	public void processMessage(@Nonnull EntityPlayer player, @Nonnull NBTTagCompound data) {
		if (data.hasKey(Messages.LinkPermitted)) {
			cached_permitted = data.getBoolean(Messages.LinkPermitted);
		}
		if (data.hasKey(Messages.SetTitle)) {
			this.cached_title = data.getString(Messages.SetTitle);
			this.tileentity.setBookTitle(player, cached_title);
		}
		if (data.hasKey(Messages.Link)) {
			if (tileentity != null) {
				tileentity.link(player);
			}
		}
		if (data.hasKey(Messages.RemoveFromCollection)) {
			if (!player.inventory.getItemStack().isEmpty())
				return;
			ItemStack page = new ItemStack(data.getCompoundTag(Messages.RemoveFromCollection));
			ItemStack itemstack = tileentity.removePageFromSurface(player, this.getTabSlot(this.activeslot), page);
			player.inventory.setItemStack(itemstack);
		}
		if (data.hasKey(Messages.RemoveFromOrderedCollection)) {
			if (!player.inventory.getItemStack().isEmpty())
				return;
			int index = data.getInteger(Messages.RemoveFromOrderedCollection);
			player.inventory.setItemStack(tileentity.removePageFromSurface(player, this.getTabSlot(this.activeslot), index));
		}
		if (data.hasKey(Messages.AddToTab)) {
			if (player.inventory.getItemStack().isEmpty())
				return;
			byte slot = data.getByte(Messages.AddToTab);
			if (tileentity.getTabItem(slot).isEmpty())
				return;
			boolean single = data.getBoolean("Single");
			if (single) {
				ItemStack stack = player.inventory.getItemStack();
				ItemStack one = stack.copy();
				one.setCount(1);
				ItemStack ret = tileentity.addPageToTab(player, tileentity.getTabItem(slot), one.copy());
				if (ItemStack.areItemStackTagsEqual(ret, one) && ItemStack.areItemStacksEqual(ret, one))
					return;
				stack.shrink(1);
				if (stack.getCount() <= 0) {
					stack = ItemStack.EMPTY;
				}
				player.inventory.setItemStack(stack);
				if (ret.isEmpty())
					return;
				if (stack.isEmpty()) {
					player.inventory.setItemStack(ret);
					return;
				}
				if (ItemStack.areItemStackTagsEqual(ret, stack)) {
					stack.grow(ret.getCount());
					//TODO: This is technically capable of exceeding max stack size
					return;
				}
				//TODO: Push any leftover to player inventory
			} else {
				player.inventory.setItemStack(tileentity.addPageToTab(player, tileentity.getTabItem(slot), player.inventory.getItemStack()));
			}
		}
		if (data.hasKey(Messages.AddToSurface)) {
			if (player.inventory.getItemStack().isEmpty())
				return;
			if (!data.hasKey("Index"))
				return;
			byte slot = data.getByte(Messages.AddToSurface);
			if (tileentity.getTabItem(slot).isEmpty())
				return;
			boolean single = data.getBoolean("Single");
			int index = data.getInteger("Index");
			if (single) {
				ItemStack stack = player.inventory.getItemStack();
				ItemStack clone = stack.copy();
				clone.setCount(1);
				ItemStack returned = tileentity.placePageOnSurface(player, tileentity.getTabItem(slot), clone, index);
				if (returned.isEmpty() || stack.getCount() == 1) {
					stack.shrink(1);
					if (stack.getCount() <= 0) {
						stack = returned;
					}
					player.inventory.setItemStack(stack);
				} else {
					tileentity.placePageOnSurface(player, tileentity.getTabItem(slot), returned, index);
				}
			} else {
				player.inventory.setItemStack(tileentity.placePageOnSurface(player, tileentity.getTabItem(slot), player.inventory.getItemStack(), index));
			}
		}
		if (data.hasKey(Messages.WriteSymbol)) {
			tileentity.writeSymbol(player, new ResourceLocation(data.getString(Messages.WriteSymbol)));
		}
		//XXX: This is weird, as it's only meaningful client-side
		if (data.hasKey(Messages.SetActiveNotebook)) {
			this.activeslot = data.getByte(Messages.SetActiveNotebook);
		}
		if (data.hasKey(Messages.SetFirstNotebook)) {
			this.firstslot = data.getByte(Messages.SetFirstNotebook);
			if (firstslot < 0)
				firstslot = 0;
			if (firstslot >= tileentity.getMaxSurfaceTabCount())
				firstslot = 0;
			updateSurfaceTabSlots();
		}
		if (data.hasKey(Messages.SetCurrentPage)) {
			if (this.tileentity.getWorld().isRemote) {
				this.setCurrentPageIndex(data.getInteger(Messages.SetCurrentPage));
				cached_linkinfo = null;
			}
		}
		if (data.hasKey(Messages.TakeFromSlider)) {
			ItemStack target = tileentity.getTarget();
			if (target.isEmpty())
				return;
			if (!player.inventory.getItemStack().isEmpty())
				return;
			if (!(target.getItem() instanceof IItemOrderablePageProvider))
				return;
			IItemOrderablePageProvider itemdat = (IItemOrderablePageProvider) target.getItem();
			int index = data.getInteger(Messages.TakeFromSlider);
			player.inventory.setItemStack(itemdat.removePage(player, target, index));
		}
		if (data.hasKey(Messages.InsertHeldAt)) {
			ItemStack target = tileentity.getTarget();
			if (target.isEmpty())
				return;
			ItemStack stack = player.inventory.getItemStack();
			if (stack.isEmpty())
				return;
			if (stack.getCount() > 1)
				return;
			if (!(target.getItem() instanceof IItemOrderablePageProvider))
				return;
			IItemOrderablePageProvider itemdat = (IItemOrderablePageProvider) target.getItem();
			int index = data.getInteger(Messages.InsertHeldAt);
			player.inventory.setItemStack(itemdat.setPage(player, target, stack, index));
		}
	}

	@Nonnull
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
		return !tileentity.isInvalid();
	}

	private List<ItemStack> getPageList() {
		ItemStack book = getBook();
		if (book.isEmpty())
			return null;
		if (book.getItem() instanceof IItemPageProvider) {
			return ((IItemPageProvider) book.getItem()).getPageList(this.player, book);
		}
		return null;
	}

	@Override
	public void setCurrentPageIndex(int index) {
		currentpage = ItemStack.EMPTY;
		currentpageIndex = 0;
		if (index < 0)
			index = 0;
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
	@Nonnull
	public ItemStack getCurrentPage() {
		if (currentpage.isEmpty()) {
			setCurrentPageIndex(currentpageIndex);
		}
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
		return dim != null && DimensionUtils.isDimensionVisited(dim);
	}

	@Override
	@Nonnull
	public ItemStack getBook() {
		if (tileentity != null) {
			ItemStack itemstack = tileentity.getTarget();
			if (itemstack.isEmpty())
				return ItemStack.EMPTY;
			if (itemstack.getItem() instanceof ItemLinking) {
				return itemstack;
			}
		}
		return ItemStack.EMPTY;
	}

	@Nonnull
	public ItemStack getTarget() {
		ItemStack itemstack = ItemStack.EMPTY;
		if (tileentity != null) {
			itemstack = tileentity.getTarget();
		}
		return itemstack;
	}

	@Override
	public ILinkInfo getLinkInfo() {
		ItemStack book = getBook();
		if (book.isEmpty() || !(book.getItem() instanceof ItemLinking))
			return null;
		if (cached_linkinfo == null) {
			cached_linkinfo = ((ItemLinking) book.getItem()).getLinkInfo(book);
		}
		return cached_linkinfo;
	}

	@Override
	public boolean isLinkPermitted() {
		ILinkInfo linkinfo = getLinkInfo();
		if (linkinfo == null) {
			cached_permitted = false;
		}
		return cached_permitted;
	}

	private boolean checkLinkPermitted() {
		ILinkInfo linkinfo = getLinkInfo();
		if (linkinfo == null) {
			return false;
		}
		if (ItemAgebook.isNewAgebook(getBook())) {
			return true;
		}
		return LinkListenerManager.isLinkPermitted(tileentity.getWorld(), player, linkinfo);
	}

	@Override
	public String getBookTitle() {
		ItemStack book = getBook();
		if (book.isEmpty() || !(book.getItem() instanceof ItemLinking))
			return "";
		return ((ItemLinking) book.getItem()).getTitle(book);
	}

	@Override
	public Collection<String> getBookAuthors() {
		ItemStack book = getBook();
		if (book.isEmpty())
			return Collections.emptySet();
		return ((ItemLinking) book.getItem()).getAuthors(book);
	}

	public IFluidTankProvider getInkTankProvider() {
		return fluidDataContainer;
	}

	public String getTargetName() {
		return cached_title;
	}

	public List<ItemStack> getBookPageList() {
		if (tileentity != null) {
			return tileentity.getBookPageList(this.player);
		}
		return null;
	}
}
