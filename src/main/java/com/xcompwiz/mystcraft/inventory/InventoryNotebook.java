package com.xcompwiz.mystcraft.inventory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.xcompwiz.mystcraft.core.InternalAPI;
import com.xcompwiz.mystcraft.data.ModItems;
import com.xcompwiz.mystcraft.symbol.SymbolRemappings;

public class InventoryNotebook implements IInventory {

	private IInventory	sourceinventory;
	private int			slot;

	public InventoryNotebook(IInventory source, int slot) {
		this.sourceinventory = source;
		this.slot = slot;
	}

	@Override
	public int getSizeInventory() {
		return getLargestSlotId(this.getNotebookItem()) + 1;
	}

	@Override
	public ItemStack getStackInSlot(int var1) {
		return getItem(this.getNotebookItem(), var1);
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		ItemStack stored = getItem(getNotebookItem(), slot);
		ItemStack returned = stored.copy();
		if (returned.stackSize < amount) {
			amount = returned.stackSize;
		}
		returned.stackSize = amount;
		stored.stackSize -= amount;
		setItem(this.getNotebookItem(), slot, stored);
		return returned;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		return getStackInSlot(slot);
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		if (itemstack != null) {
			if (itemstack.stackSize > getInventoryStackLimit()) {
				itemstack.stackSize = getInventoryStackLimit();
			}
		}
		setItem(this.getNotebookItem(), i, itemstack);
	}

	@Override
	public String getInventoryName() {
		String name = getNotebookName();
		if (name == null || name.equals("")) return "Notebook";
		return name;
	}

	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public void markDirty() {}

	@Override
	public boolean isUseableByPlayer(EntityPlayer var1) {
		return getNotebookItem() != null && getNotebookItem().stackSize > 0;
	}

	@Override
	public void openInventory() {}

	@Override
	public void closeInventory() {}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return isItemValid(itemstack);
	}

	public ItemStack getNotebookItem() {
		return sourceinventory.getStackInSlot(slot);
	}

	public String getNotebookName() {
		return getName(getNotebookItem());
	}

//BEGIN NotebookUtils
	public static boolean isItemValid(ItemStack itemstack) {
		if (itemstack == null) return true;
		if (itemstack.getItem() == ModItems.page) return true;
		if (itemstack.getItem() == Items.paper) return true;
		return false;
	}

	private static void initNotebook(ItemStack notebook) {
		if (notebook.stackTagCompound == null) notebook.stackTagCompound = new NBTTagCompound();
	}

	public static void setName(ItemStack notebook, String bookname) {
		if (notebook == null) return;
		if (notebook.getItem() != ModItems.notebook) return;
		if (notebook.stackTagCompound == null) initNotebook(notebook);
		if (bookname == null || bookname.equals("")) {
			notebook.stackTagCompound.removeTag("Name");
		} else {
			notebook.stackTagCompound.setString("Name", bookname);
		}
	}

	public static String getName(ItemStack notebook) {
		if (notebook == null) return null;
		if (notebook.getItem() != ModItems.notebook) return null;
		if (notebook.stackTagCompound == null) initNotebook(notebook);
		if (!notebook.stackTagCompound.hasKey("Name")) return null;
		return notebook.stackTagCompound.getString("Name");
	}

	private static NBTTagCompound getInventoryCompound(ItemStack notebook) {
		if (notebook == null) return null;
		if (notebook.getItem() != ModItems.notebook) return null;
		if (notebook.stackTagCompound == null) initNotebook(notebook);
		if (!notebook.stackTagCompound.hasKey("Pages")) {
			notebook.stackTagCompound.setTag("Pages", new NBTTagCompound());
		}
		return notebook.stackTagCompound.getCompoundTag("Pages");
	}

	public static int getItemCount(ItemStack notebook) {
		if (getInventoryCompound(notebook) == null) return 0;
		return getInventoryCompound(notebook).func_150296_c().size();
	}

	public static int getLargestSlotId(ItemStack notebook) {
		int largest = 0;
		NBTTagCompound compound = getInventoryCompound(notebook);
		if (compound == null) return 0;
		Collection<String> tagnames = compound.func_150296_c();
		for (String tagname : tagnames) {
			int slot = Integer.parseInt(tagname);
			if (largest < slot) {
				largest = slot;
			}
		}
		return largest;
	}

	public static ItemStack getItem(ItemStack notebook, int slot) {
		NBTTagCompound data = getInventoryCompound(notebook);
		if (data != null && data.hasKey("" + slot)) { return ItemStack.loadItemStackFromNBT(data.getCompoundTag("" + slot)); }
		return null;
	}

	public static ItemStack setItem(ItemStack notebook, int slot, ItemStack page) {
		if (!isItemValid(page)) return page;
		NBTTagCompound data = getInventoryCompound(notebook);
		if (data == null) return page;
		ItemStack previous = null;
		previous = removeItem(notebook, slot);
		if (page != null && page.stackSize > 0) {
			data.setTag("" + slot, page.writeToNBT(new NBTTagCompound()));
		}
		return previous;
	}

	public static ItemStack removeItem(ItemStack notebook, int slot) {
		NBTTagCompound data = getInventoryCompound(notebook);
		ItemStack itemstack = null;
		if (data != null) {
			if (data.hasKey("" + slot)) {
				itemstack = ItemStack.loadItemStackFromNBT(data.getCompoundTag("" + slot));
			}
			data.removeTag("" + slot);
		}
		return itemstack;
	}

	public static ItemStack addItem(ItemStack notebook, ItemStack page) {
		if (!isItemValid(page)) return page;
		NBTTagCompound data = getInventoryCompound(notebook);
		if (data == null) { return page; }
		int slot = 0;
		while (page != null) {
			if (!data.hasKey("" + slot)) {
				ItemStack clone = page.copy();
				clone.stackSize = 1;
				data.setTag("" + slot, clone.writeToNBT(new NBTTagCompound()));
				--page.stackSize;
				if (page.stackSize == 0) page = null;
			}
			++slot;
		}
		return null;
	}

	public static List<ItemStack> getItems(ItemStack notebook) {
		List<ItemStack> pages = new ArrayList<ItemStack>();
		NBTTagCompound compound = getInventoryCompound(notebook);
		if (compound == null) return pages;
		Collection<String> tagnames = compound.func_150296_c();
		for (String tagname : tagnames) {
			NBTTagCompound pagedata = compound.getCompoundTag(tagname);
			int slot = Integer.parseInt(tagname);
			while (pages.size() <= slot)
				pages.add(null);
			pages.set(slot, ItemStack.loadItemStackFromNBT(pagedata));
		}
		return pages;
	}

	/**
	 * Performs some basic housekeeping. Handles symbol remappings
	 * 
	 * @param notebook
	 */
	public static void updatePages(ItemStack notebook) {
		NBTTagCompound compound = getInventoryCompound(notebook);
		if (compound == null) return;
		Collection<String> tagnames = new ArrayList<String>();
		tagnames.addAll(compound.func_150296_c());
		for (String tagname : tagnames) {
			NBTTagCompound pagedata = compound.getCompoundTag(tagname);
			ItemStack page = ItemStack.loadItemStackFromNBT(pagedata);
			List<ItemStack> results = SymbolRemappings.remap(page);
			int slot = Integer.parseInt(tagname);
			if (results.size() == 0) {
				removeItem(notebook, slot);
				continue;
			}
			if (results.size() == 1) {
				setItem(notebook, slot, results.get(0));
			}
			if (results.size() != 1) {
				removeItem(notebook, slot);
				for (ItemStack item : results) {
					addItem(notebook, item);
				}
			}
		}
	}

	/**
	 * Writes the symbol to the first blank page in the book Can draw from the provided paperstack if there are not
	 * available blank pages Returns false on failure
	 * 
	 * @param notebook Notebook container
	 * @param symbol Symbol to write
	 * @return
	 */
	public static boolean writeSymbol(ItemStack notebook, String symbol) {
		List<ItemStack> pages = getItems(notebook);
		for (ItemStack page : pages) {
			if (page == null) continue;
			if (page.getItem() != ModItems.page) continue;
			if (InternalAPI.page.isPageWritable(page)) {
				InternalAPI.page.setPageSymbol(page, symbol);
				return true;
			}
		}
		return false;
	}
}
