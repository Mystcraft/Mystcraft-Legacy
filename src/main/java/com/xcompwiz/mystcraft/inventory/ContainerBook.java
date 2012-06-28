package com.xcompwiz.mystcraft.inventory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraftforge.common.DimensionManager;

import com.xcompwiz.mystcraft.Mystcraft;
import com.xcompwiz.mystcraft.api.linking.ILinkInfo;
import com.xcompwiz.mystcraft.item.ItemAgebook;
import com.xcompwiz.mystcraft.item.ItemLinking;
import com.xcompwiz.mystcraft.linking.LinkListenerManager;
import com.xcompwiz.mystcraft.linking.LinkOptions;
import com.xcompwiz.mystcraft.network.IGuiMessageHandler;
import com.xcompwiz.mystcraft.network.MPacketAgeData;
import com.xcompwiz.mystcraft.network.MPacketGuiMessage;
import com.xcompwiz.mystcraft.tileentity.TileEntityBookDisplay;
import com.xcompwiz.mystcraft.world.agedata.AgeData;

public class ContainerBook extends ContainerMyst implements IGuiMessageHandler, IBookContainer {
	private IInventory		inventory;
	private InventoryPlayer	inventoryplayer;
	private Integer			slot;

	private ItemStack		currentpage			= null;
	private int				pagecount			= 0;
	private int				currentpageIndex	= 0;

	private ILinkInfo		cached_linkinfo;
	private Boolean			cached_permitted;

	public ContainerBook(InventoryPlayer inventoryplayer, IInventory inventory) {
		this.inventory = inventory;
		this.inventoryplayer = inventoryplayer;
		updateSlots();
	}

	public ContainerBook(InventoryPlayer inventoryplayer, int slot) {
		this.slot = slot;
		this.inventoryplayer = inventoryplayer;
		updateSlots();
	}

	@Override
	public ItemStack getBook() {
		if (inventory != null) {
			ItemStack itemstack = inventory.getStackInSlot(0);
			if (itemstack != null && itemstack.getItem() instanceof ItemLinking) { return itemstack; }
		} else if (slot != null) {
			ItemStack itemstack = inventoryplayer.getStackInSlot(slot);
			if (itemstack != null && itemstack.getItem() instanceof ItemLinking) { return itemstack; }
		}
		return null;
	}

	@Override
	public void setCurrentPageIndex(int index) {
		currentpage = null;
		currentpageIndex = 0;
		if (index < 0) index = 0;
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
	public ItemStack getCurrentPage() {
		if (currentpage == null) setCurrentPageIndex(currentpageIndex);
		return currentpage;
	}

	@Override
	public int getCurrentPageIndex() {
		return currentpageIndex;
	}

	private List<ItemStack> getPageList() {
		ItemStack book = getBook();
		if (book == null) return null;
		if (book.getItem() instanceof ItemLinking) { //FIXME: (PageStorage) This needs to change with the gui and page storage rewrite
			return ((ItemLinking) book.getItem()).getPageList(this.inventoryplayer.player, book);
		}
		return null;
	}

	@Override
	public int getPageCount() {
		return pagecount;
	}

	@Override
	public boolean isTargetWorldVisited() {
		ItemStack book = getBook();
		if (book == null) return false;
		if (book.stackTagCompound == null) return false;
		if (book.getItem() instanceof ItemLinking) { //XXX: (Helper) This should be broken into some kind of helper
			int dim = LinkOptions.getDimensionUID(book.stackTagCompound);
			if (DimensionManager.getProviderType(dim) == Mystcraft.providerId) {
				AgeData agedata = AgeData.getAge(dim, inventoryplayer.player.worldObj.isRemote);
				if (agedata == null) return false;
				return agedata.isVisited();
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean hasBookSlot() {
		return this.inventorySlots.size() > 0;
	}

	public int getInventorySize() {
		if (inventory == null) return 0;
		return inventory.getSizeInventory();
	}

	@Override
	public ItemStack slotClick(int par1, int par2, int par3, EntityPlayer par4EntityPlayer) {
		if (par1 >= this.inventorySlots.size()) return null;
		return super.slotClick(par1, par2, par3, par4EntityPlayer);
	}

	public void updateSlots() {
		if (inventory != null) {
			ItemStack book = getBook();
			if (this.currentpageIndex > 0) {
				this.inventorySlots.clear();
				this.inventoryItemStacks.clear();
			} else if (book != null && this.inventorySlots.size() != 1) {
				this.inventorySlots.clear();
				this.inventoryItemStacks.clear();
				addSlotToContainer(new SlotFiltered(inventory, 0, 41, 21));
			} else if ((book == null && inventory.getSizeInventory() == 1 && this.inventorySlots.size() != 37)) {
				this.inventorySlots.clear();
				this.inventoryItemStacks.clear();
				addInventorySlots();
				addSlotToContainer(new SlotFiltered(inventory, 0, 80, 35));
			} else if ((book == null && inventory.getSizeInventory() == 0 && this.inventorySlots.size() != 36)) {
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
		List<Packet> packets = new ArrayList<Packet>();
		for (int slotId = 0; slotId < this.inventorySlots.size(); ++slotId) {
			ItemStack actual = ((Slot) this.inventorySlots.get(slotId)).getStack();
			ItemStack stored = (ItemStack) this.inventoryItemStacks.get(slotId);

			if (!ItemStack.areItemStacksEqual(stored, actual)) {
				if (slotId == 0) {
					cached_linkinfo = null;
					cached_permitted = null;
					NBTTagCompound nbttagcompound = new NBTTagCompound();
					nbttagcompound.setInteger("SetCurrentPage", currentpageIndex);
					packets.add(MPacketGuiMessage.createPacket(this.windowId, nbttagcompound));
				}
				if (actual != null && actual.stackTagCompound != null && actual.getItem() == ItemAgebook.instance) {
					if (ItemAgebook.getAgeData(inventoryplayer.player.worldObj, actual) != null) {
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
		if (cached_permitted == null) {
			cached_permitted = checkLinkPermitted();
			if (cached_permitted != null) {
				NBTTagCompound nbttagcompound = new NBTTagCompound();
				nbttagcompound.setBoolean("LinkPermitted", cached_permitted);
				packets.add(MPacketGuiMessage.createPacket(this.windowId, nbttagcompound));
			}
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

	@Override
	public void updateProgressBar(int i, int j) {}

	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		updateSlots();
		if (inventory == null) return getBook() != null;
		return inventory.isUseableByPlayer(entityplayer);
	}

	@Override
	public Slot getSlotFromInventory(IInventory par1IInventory, int par2) {
		for (int var3 = 0; var3 < this.inventorySlots.size(); ++var3) {
			Slot var4 = (Slot) this.inventorySlots.get(var3);

			if (var4.isSlotInInventory(par1IInventory, par2)) { return var4; }
		}

		return new Slot(inventoryplayer, inventoryplayer.currentItem, 0, 0);
	}

	@Override
	public Slot getSlot(int par1) {
		if (par1 >= this.inventorySlots.size()) par1 = 0;
		if (par1 < this.inventorySlots.size()) return (Slot) this.inventorySlots.get(par1);
		return new Slot(inventoryplayer, inventoryplayer.currentItem, 0, 0);
	}

	@Override
	public void processMessage(EntityPlayer player, NBTTagCompound data) {
		if (data.hasKey("LinkPermitted")) {
			cached_permitted = data.getBoolean("LinkPermitted");
		}
		if (data.hasKey("SetCurrentPage")) {
			this.setCurrentPageIndex(data.getInteger("SetCurrentPage"));
		}
		if (data.hasKey("Link")) {
			if (inventory != null) {
				if (inventory instanceof TileEntityBookDisplay) {
					((TileEntityBookDisplay) inventory).link(player);
				}
				if (inventory instanceof InventoryBook) {
					((InventoryBook) inventory).linkEntity(player);
				}
			} else if (slot != null) {
				ItemStack itemstack = inventoryplayer.getStackInSlot(slot);
				if (itemstack.getItem() instanceof ItemLinking) {
					((ItemLinking) itemstack.getItem()).activate(itemstack, player.worldObj, player);
				}
			}
		}
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
		return LinkListenerManager.isLinkPermitted(inventoryplayer.player.worldObj, inventoryplayer.player, linkinfo);
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int i) {
		ItemStack clone = null;
		Slot slot = (Slot) inventorySlots.get(i);
		if (slot != null && slot.getHasStack()) {
			ItemStack original = slot.getStack();
			clone = original.copy();

			List<SlotCollection> collections = new ArrayList<SlotCollection>();
			SlotCollection internal = null;
			SlotCollection maininv = null;
			SlotCollection hotbar = null;
			ItemStack book = getBook();
			if (book != null || (inventory != null && inventory.getSizeInventory() > 0)) {
				internal = new SlotCollection(this, inventorySlots.size() - 1, inventorySlots.size());
				internal.pushTargetFront(new TargetInventory(inventoryplayer));
				collections.add(internal);
			}
			if (book == null) {
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
				if (!collection.contains(i)) continue;
				collection.onShiftClick(original);
				break;
			}

			if (original.stackSize == 0) {
				slot.putStack(null);
			} else {
				slot.onSlotChanged();
			}
			if (original.stackSize != clone.stackSize) {
				slot.onPickupFromSlot(player, original);
			} else {
				return null;
			}
		}
		return clone;
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
		if (book == null || !(book.getItem() instanceof ItemLinking)) return Collections.EMPTY_SET;
		return ((ItemLinking) book.getItem()).getAuthors(book);
	}
}
