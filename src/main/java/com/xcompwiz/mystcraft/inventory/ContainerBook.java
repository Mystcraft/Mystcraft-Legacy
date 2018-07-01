package com.xcompwiz.mystcraft.inventory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.xcompwiz.mystcraft.api.item.IItemPageProvider;
import com.xcompwiz.mystcraft.api.linking.ILinkInfo;
import com.xcompwiz.mystcraft.entity.EntityLinkbook;
import com.xcompwiz.mystcraft.item.ItemAgebook;
import com.xcompwiz.mystcraft.item.ItemLinking;
import com.xcompwiz.mystcraft.item.LinkItemUtils;
import com.xcompwiz.mystcraft.linking.DimensionUtils;
import com.xcompwiz.mystcraft.linking.LinkListenerManager;
import com.xcompwiz.mystcraft.network.IGuiMessageHandler;
import com.xcompwiz.mystcraft.network.MystcraftPacketHandler;
import com.xcompwiz.mystcraft.network.packet.MPacketGuiMessage;
import com.xcompwiz.mystcraft.tileentity.IOInventory;
import com.xcompwiz.mystcraft.tileentity.InventoryFilter;
import com.xcompwiz.mystcraft.tileentity.TileEntityBookRotateable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ContainerBook extends ContainerBase implements IGuiMessageHandler, IBookContainer {

	public static class Messages {

		public static final String LinkPermitted = "LinkPermitted";
		public static final String SetCurrentPage = "SetCurrentPage";
		public static final String Link = "Link";

	}

	private EntityLinkbook linkbook;
	private TileEntityBookRotateable bookTile;
	private Integer slot;

	private InventoryPlayer inventoryplayer;

	@Nonnull
	private ItemStack currentpage = ItemStack.EMPTY;
	private int pagecount = 0;
	private int currentpageIndex = 0;

	private ILinkInfo cached_linkinfo;
	private Boolean cached_permitted;

	//Container originates from entity
	public ContainerBook(InventoryPlayer inventoryplayer, EntityLinkbook linkbook) {
		this.linkbook = linkbook;
		this.inventoryplayer = inventoryplayer;
		updateSlots();
	}

	//Container originates from Tileentity
	public ContainerBook(InventoryPlayer inventoryplayer, TileEntityBookRotateable tile) {
		this.bookTile = tile;
		this.inventoryplayer = inventoryplayer;
		updateSlots();
	}

	//Container originates from player inventory
	public ContainerBook(InventoryPlayer inventoryplayer, int slot) {
		this.slot = slot;
		this.inventoryplayer = inventoryplayer;
		updateSlots();
	}

	@Override
	@Nonnull
	public ItemStack getBook() {
		if (linkbook != null) {
			ItemStack itemstack = linkbook.getBook();
			if (!itemstack.isEmpty() && itemstack.getItem() instanceof ItemLinking) {
				return itemstack;
			}
		} else if (bookTile != null) {
			ItemStack book = bookTile.getBook();
			if (!book.isEmpty() && book.getItem() instanceof ItemLinking) {
				return book;
			}
		} else if (slot != null) {
			ItemStack itemstack = inventoryplayer.getStackInSlot(slot);
			if (!itemstack.isEmpty() && itemstack.getItem() instanceof ItemLinking) {
				return itemstack;
			}
		}
		return ItemStack.EMPTY;
	}

	@Override
	public void setCurrentPageIndex(int index) {
		currentpage = ItemStack.EMPTY;
		currentpageIndex = 0;
		if (index < 0)
			index = 0;
		List<ItemStack> pagelist = getPageList();
		if (pagelist != null && index >= pagelist.size()) {
			index = pagelist.size();
		}
		if (pagelist != null && index < pagelist.size()) {
			pagecount = pagelist.size();
			currentpage = pagelist.get(index);
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

	@Nullable
	private List<ItemStack> getPageList() {
		ItemStack book = getBook();
		if (book.isEmpty())
			return null;
		if (book.getItem() instanceof IItemPageProvider) {
			return ((IItemPageProvider) book.getItem()).getPageList(this.inventoryplayer.player, book);
		}
		return null;
	}

	@Override
	public int getPageCount() {
		return pagecount;
	}

	@Override
	public boolean isTargetWorldVisited() {
		return DimensionUtils.isDimensionVisited(LinkItemUtils.getTargetDimension(getBook()));
	}

	@Override
	public boolean hasBookSlot() {
		return this.inventorySlots.size() > 0;
	}

	public int getInventorySize() {
		if (linkbook != null && !linkbook.isDead) {
			return 1;
		} else if (bookTile != null) {
			return 1;
		}
		return 0;
	}

	@Override
	@Nonnull
	public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
		if (slotId >= this.inventorySlots.size())
			return ItemStack.EMPTY;
		return super.slotClick(slotId, dragType, clickTypeIn, player);
	}

	public void updateSlots() {
		if (linkbook != null || bookTile != null) {
			ItemStack book = getBook();
			if (this.currentpageIndex > 0) {
				this.inventorySlots.clear();
				this.inventoryItemStacks.clear();
			} else if (!book.isEmpty() && this.inventorySlots.size() != 1) {
				this.inventorySlots.clear();
				this.inventoryItemStacks.clear();
				IItemHandlerModifiable other = getCurrentOtherInventory();
				addSlotToContainer(new SlotFiltered(other, other instanceof InventoryFilter ? (InventoryFilter) other : null, 0, 41, 21));
			} else if ((book.isEmpty() && getCurrentOtherInventorySize() == 1 && this.inventorySlots.size() != 37)) {
				this.inventorySlots.clear();
				this.inventoryItemStacks.clear();
				addInventorySlots();
				IItemHandlerModifiable other = getCurrentOtherInventory();
				addSlotToContainer(new SlotFiltered(other, other instanceof InventoryFilter ? (InventoryFilter) other : bookTile, 0, 80, 35));
			} else if ((book.isEmpty() && getCurrentOtherInventorySize() == 0 && this.inventorySlots.size() != 36)) {
				this.inventorySlots.clear();
				this.inventoryItemStacks.clear();
				addInventorySlots();
			}
		}
	}

	private void addInventorySlots() {
		int i;
		for (i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				this.addSlotToContainer(new Slot(inventoryplayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}

		for (i = 0; i < 9; ++i) {
			this.addSlotToContainer(new Slot(inventoryplayer, i, 8 + i * 18, 142));
		}
	}

	@Override
	public void detectAndSendChanges() {
		List<IMessage> packets = new ArrayList<>();
		for (int slotId = 0; slotId < this.inventorySlots.size(); ++slotId) {
			ItemStack actual = this.inventorySlots.get(slotId).getStack();
			ItemStack stored = this.inventoryItemStacks.get(slotId);

			if (!ItemStack.areItemStacksEqual(stored, actual)) {
				if (slotId == 0) {
					cached_linkinfo = null;
					cached_permitted = null;
					NBTTagCompound nbttagcompound = new NBTTagCompound();
					nbttagcompound.setInteger(Messages.SetCurrentPage, currentpageIndex);
					packets.add(new MPacketGuiMessage(this.windowId, nbttagcompound));
				}
				stored = actual.isEmpty() ? ItemStack.EMPTY : actual.copy();
				this.inventoryItemStacks.set(slotId, stored);

				for (IContainerListener listener : this.listeners) {
					listener.sendSlotContents(this, slotId, stored);
				}
			}
		}
		if (cached_permitted == null) {
			cached_permitted = checkLinkPermitted();
			if (cached_permitted != null) {
				NBTTagCompound nbttagcompound = new NBTTagCompound();
				nbttagcompound.setBoolean(Messages.LinkPermitted, cached_permitted);
				packets.add(new MPacketGuiMessage(this.windowId, nbttagcompound));
			}
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

	@Override
	public void updateProgressBar(int i, int j) {}

	@Override
	public boolean canInteractWith(@Nonnull EntityPlayer entityplayer) {
		updateSlots();
		if (linkbook != null && !linkbook.isDead) {
			return !getBook().isEmpty();
		}
		return true;
	}

	@Override
	public Slot getSlotFromInventory(@Nonnull IInventory par1IInventory, int par2) {
		for (Slot slot : this.inventorySlots) {
			if (slot.isHere(par1IInventory, par2)) {
				return slot;
			}
		}

		return new Slot(inventoryplayer, inventoryplayer.currentItem, 0, 0);
	}

	@Override
	public Slot getSlot(int par1) {
		if (par1 >= this.inventorySlots.size())
			par1 = 0;
		if (par1 < this.inventorySlots.size())
			return this.inventorySlots.get(par1);
		return new Slot(inventoryplayer, inventoryplayer.currentItem, 0, 0);
	}

	@Override
	public void processMessage(@Nonnull EntityPlayer player, @Nonnull NBTTagCompound data) {
		if (data.hasKey(Messages.LinkPermitted)) {
			cached_permitted = data.getBoolean(Messages.LinkPermitted);
		}
		if (data.hasKey(Messages.SetCurrentPage)) {
			this.setCurrentPageIndex(data.getInteger(Messages.SetCurrentPage));
		}
		if (data.hasKey(Messages.Link)) {
			if (bookTile != null) {
				bookTile.link(player);
			} else if (linkbook != null) {
				linkbook.linkEntity(player);
			} else if (slot != null) {
				ItemStack itemstack = inventoryplayer.getStackInSlot(slot);
				if (itemstack.getItem() instanceof ItemLinking) {
					((ItemLinking) itemstack.getItem()).activate(itemstack, player.world, player);
				}
			}
		}
	}

	@Override
	public ILinkInfo getLinkInfo() {
		ItemStack book = getBook();
		if (book.isEmpty() || !(book.getItem() instanceof ItemLinking)) {
			cached_linkinfo = null;
			return null;
		}
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
		if (cached_permitted == null)
			return false;
		return cached_permitted;
	}

	private boolean checkLinkPermitted() {
		ILinkInfo linkinfo = getLinkInfo();
		if (linkinfo == null) {
			return false;
		}
		if (ItemAgebook.isNewAgebook(getBook())) {
			return true; //TODO: Generalize this
		}
		return LinkListenerManager.isLinkPermitted(inventoryplayer.player.world, inventoryplayer.player, linkinfo);
	}

	@Nullable
	private IItemHandlerModifiable getCurrentOtherInventory() {
		if (linkbook != null && !linkbook.isDead) {
			return linkbook.createBookWrapper();
		}
		if (bookTile != null) {
			return (IItemHandlerModifiable) bookTile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.DOWN);
		}
		return null;
	}

	private int getCurrentOtherInventorySize() {
		if (linkbook != null && !linkbook.isDead) {
			return 1;
		}
		if (bookTile != null) {
			IOInventory inv = (IOInventory) bookTile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.DOWN);
			if (inv != null && inv.getSlots() > 0) {
				return 1;
			}
		}
		return 0;
	}

	@Override
	@Nonnull
	public ItemStack transferStackInSlot(EntityPlayer player, int i) {
		ItemStack clone = ItemStack.EMPTY;
		Slot slot = inventorySlots.get(i);
		if (slot != null && slot.getHasStack()) {
			ItemStack original = slot.getStack();
			clone = original.copy();

			List<SlotCollection> collections = new ArrayList<>();
			SlotCollection internal = null;
			SlotCollection maininv;
			SlotCollection hotbar;
			ItemStack book = getBook();
			if (!book.isEmpty()) {
				if (linkbook != null && !linkbook.isDead) {
					internal = new SlotCollection(this, 0, 1);
					internal.pushTargetFront(new TargetInventory(inventoryplayer));
					collections.add(internal);
				} else if (bookTile != null) {
					IOInventory inv = (IOInventory) bookTile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.DOWN);
					if (inv != null && inv.getSlots() > 0) {
						internal = new SlotCollection(this, inv.getSlots() - 1, inv.getSlots());
						internal.pushTargetFront(new TargetInventory(inventoryplayer));
						collections.add(internal);
					}
				}
			}
			if (book.isEmpty()) {
				maininv = new SlotCollection(this, 0, 27);
				hotbar = new SlotCollection(this, 27, 27 + 9);
				maininv.pushTargetFront(hotbar);
				hotbar.pushTargetFront(maininv);
				if (internal != null) {
					internal.pushTargetFront(maininv);
					internal.pushTargetFront(hotbar);
					maininv.pushTargetFront(internal);
					hotbar.pushTargetFront(internal);
				}
				collections.add(maininv);
				collections.add(hotbar);
			}

			for (SlotCollection collection : collections) {
				if (!collection.contains(i))
					continue;
				collection.onShiftClick(original);
				break;
			}

			if (original.getCount() == 0) {
				slot.putStack(ItemStack.EMPTY);
			} else {
				slot.onSlotChanged();
			}
			if (original.getCount() != clone.getCount()) {
				slot.onTake(player, original);
			} else {
				return ItemStack.EMPTY;
			}
		}
		return clone;
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
		if (book.isEmpty() || !(book.getItem() instanceof ItemLinking))
			return Collections.emptySet();
		return ((ItemLinking) book.getItem()).getAuthors(book);
	}
}
