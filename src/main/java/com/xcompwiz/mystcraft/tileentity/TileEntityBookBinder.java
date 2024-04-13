package com.xcompwiz.mystcraft.tileentity;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

import com.xcompwiz.mystcraft.data.ModItems;
import com.xcompwiz.mystcraft.inventory.IItemBuilder;
import com.xcompwiz.mystcraft.inventory.InventoryFolder;
import com.xcompwiz.mystcraft.item.ItemAgebook;
import com.xcompwiz.mystcraft.item.ItemPage;
import com.xcompwiz.mystcraft.nbt.NBTUtils;
import com.xcompwiz.mystcraft.page.Page;
import com.xcompwiz.mystcraft.symbol.SymbolRemappings;

public class TileEntityBookBinder extends TileEntityRotatable implements IItemBuilder, ISidedInventory {

	private ItemStack		itemstacks[];
	private List<ItemStack>	pages;
	private String			pendingtitle;

	private static int[]	isidedslots	= { 1 };

	public TileEntityBookBinder() {
		itemstacks = new ItemStack[2];
		pages = new ArrayList<ItemStack>();
	}

	@Override
	public void setYaw(int rotation) {
		rotation = rotation - (rotation % 90);
		super.setYaw(rotation);
	}

	@Override
	public boolean canUpdate() {
		return false;
	}

	@Override
	public boolean isItemValidForSlot(int slotIndex, ItemStack itemstack) {
		if (itemstack == null) return false;
		if (slotIndex == 1 && isValidCover(itemstack)) return true;
		return false;
	}

	//XXX: (Crafting) Book binding/covers
	private boolean isValidCover(ItemStack itemstack) {
		if (itemstack.getItem() == Items.leather) return true;
		if (itemstack.getItem() == ModItems.folder && InventoryFolder.getLargestSlotId(itemstack) == -1) return true; //XXX: This is slightly broken client-side (NBT might be stale)
		return false;
	}

	@Override
	public int getSizeInventory() {
		return itemstacks.length + pages.size();
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		if (i >= itemstacks.length) {
			i -= itemstacks.length;
			if (i >= pages.size()) return null;
			return pages.get(i);
		}
		return itemstacks[i];
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		if (itemstacks[i] != null) {
			if (itemstacks[i].stackSize <= j) {
				ItemStack itemstack = itemstacks[i];
				itemstacks[i] = null;
				handleItemChange(itemstacks[i], i);
				return itemstack;
			}
			ItemStack itemstack1 = itemstacks[i].splitStack(j);
			if (itemstacks[i].stackSize == 0) {
				itemstacks[i] = null;
			}
			handleItemChange(itemstacks[i], i);
			return itemstack1;
		}
		return null;
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		if (itemstack != null && !isItemValidForSlot(i, itemstack)) {
			itemstack.stackSize = 0;
			return;
		}
		itemstacks[i] = itemstack;
		if (itemstack != null && itemstack.stackSize > getInventoryStackLimit()) {
			itemstack.stackSize = getInventoryStackLimit();
		}
		handleItemChange(itemstacks[i], i);
	}

	@Override
	public String getInventoryName() {
		return "Bookbinder";
	}

	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		if (worldObj.getTileEntity(xCoord, yCoord, zCoord) != this) { return false; }
		return entityplayer.getDistanceSq(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D) <= 64D;
	}

	@Override
	public void openInventory() {}

	@Override
	public void closeInventory() {}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		NBTUtils.readInventoryArray(nbttagcompound.getTagList("Items", Constants.NBT.TAG_COMPOUND), itemstacks);
		pages.clear();
		NBTUtils.readItemStackCollection(nbttagcompound.getTagList("Pages", Constants.NBT.TAG_COMPOUND), pages);
		SymbolRemappings.remap(pages);
		if (nbttagcompound.hasKey("PendingTitle")) {
			pendingtitle = nbttagcompound.getString("PendingTitle");
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		NBTTagList nbttaglist = new NBTTagList();
		for (int i = 0; i < itemstacks.length; i++) { //XXX: Use generic item saving helper
			if (itemstacks[i] != null) {
				NBTTagCompound nbttagcompound1 = new NBTTagCompound();
				nbttagcompound1.setByte("Slot", (byte) i);
				itemstacks[i].writeToNBT(nbttagcompound1);
				nbttaglist.appendTag(nbttagcompound1);
			}
		}
		nbttagcompound.setTag("Items", nbttaglist);
		nbttaglist = new NBTTagList();
		for (ItemStack page : pages) { //XXX: Use generic item saving helper?
			NBTTagCompound itemdata = new NBTTagCompound();
			page.writeToNBT(itemdata);
			nbttaglist.appendTag(itemdata);
		}
		nbttagcompound.setTag("Pages", nbttaglist);

		if (pendingtitle != null) nbttagcompound.setString("PendingTitle", pendingtitle);
	}

	protected void handleItemChange(ItemStack itemstack, int slot) {}

	public String getPendingTitle() {
		return (pendingtitle == null ? "" : pendingtitle);
	}

	public void setBookTitle(String name) {
		this.pendingtitle = name;
	}

	@Override
	public void buildItem(ItemStack itemstack, EntityPlayer player) {
		if (!canBuildItem()) return;
		if (itemstack.getItem() instanceof ItemAgebook) {
			ItemAgebook.create(itemstack, player, pages, pendingtitle);
			pages.clear();
			pendingtitle = null;
			--(itemstacks[1].stackSize);
			if (itemstacks[1].stackSize <= 0) itemstacks[1] = null;
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			this.markDirty();
		} else {
			itemstack.stackSize = 0;
		}
	}

	private boolean canBuildItem() {
		if (itemstacks[1] == null) return false;
		if (!isValidCover(itemstacks[1])) return false;
		if (pages.size() == 0) return false;
		if (!Page.isLinkPanel(pages.get(0))) return false;
		if (pendingtitle == null || pendingtitle.equals("")) return false;
		for (int i = 1; i < pages.size(); ++i) {
			if (Page.isLinkPanel(pages.get(i))) return false;
		}
		return true;
	}

	public ItemStack getCraftedItem() {
		if (!canBuildItem()) return null;
		return new ItemStack(ModItems.agebook);
	}

	public void setPages(List<ItemStack> page_list) {
		if (this.worldObj.isRemote) {
			this.pages = page_list;
		}
	}

	public List<ItemStack> getPageList() {
		return pages;
	}

	public ItemStack insertPage(ItemStack stack, int i) {
		if (stack == null) return null;
		if (stack.getItem() == Items.paper) {
			while (stack.stackSize > 0) {
				ItemStack clone = stack.copy();
				clone.stackSize = 1;
				clone = ItemPage.createItemstack(clone);
				if (clone == null || insertPage(clone, i) != null) return stack;
				--stack.stackSize;
			}
			if (stack.stackSize == 0) stack = null;
			return stack;
		}
		if (stack.getItem() != ModItems.page) return stack;
		while (stack.stackSize > 0) {
			ItemStack clone = stack.copy();
			clone.stackSize = 1;
			pages.add(i, clone);
			stack.stackSize -= 1;
		}
		stack = null;
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		this.markDirty();
		return stack;
	}

	public ItemStack insertFromFolder(ItemStack folder, int index) {
		if (folder.getItem() != ModItems.folder) return folder; //XXX: Make use an interface
		int size = InventoryFolder.getLargestSlotId(folder);
		if (size == -1) {
			for (ItemStack page : pages) {
				InventoryFolder.addItem(folder, page);
			}
			pages.clear();
		} else {
			for (int slot = 0; slot < size + 1; ++slot) {
				ItemStack page = InventoryFolder.getItem(folder, slot);
				if (page == null) continue;
				page = insertPage(page, index);
				if (page == null) ++index;
				InventoryFolder.setItem(folder, slot, page);
			}
		}
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		this.markDirty();
		return null;
	}

	public ItemStack removePage(int i) {
		if (i >= pages.size()) return null;
		ItemStack itemstack = pages.remove(i);
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		this.markDirty();
		return itemstack;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int par1) {
		if (itemstacks[par1] != null) {
			ItemStack itemstack = itemstacks[par1];
			itemstacks[par1] = null;
			return itemstack;
		}
		return null;
	}

	/**
	 * Get the size of the side inventory.
	 */
	@Override
	public int[] getAccessibleSlotsFromSide(int par1) {
		return isidedslots;
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack itemstack, int par3) {
		return this.isItemValidForSlot(slot, itemstack);
	}

	@Override
	public boolean canExtractItem(int par1, ItemStack par2ItemStack, int par3) {
		return false;
	}
}
