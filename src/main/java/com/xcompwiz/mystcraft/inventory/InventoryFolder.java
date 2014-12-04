package com.xcompwiz.mystcraft.inventory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.xcompwiz.mystcraft.core.InternalAPI;
import com.xcompwiz.mystcraft.data.ModItems;
import com.xcompwiz.mystcraft.symbol.SymbolRemappings;

public class InventoryFolder {
//FIXME: Refactor me! I'm a mess!
	public static boolean isItemValid(ItemStack itemstack) {
		if (itemstack == null) return true;
		if (itemstack.stackSize != 1) return false;
		if (itemstack.getItem() == ModItems.page) return true;
		if (itemstack.getItem() == Items.paper) return true;
		return false;
	}

	private static void initFolder(ItemStack folder) {
		if (folder.stackTagCompound == null) folder.stackTagCompound = new NBTTagCompound();
	}

	public static void setName(ItemStack folder, String bookname) {
		if (folder == null) return;
		if (folder.getItem() != ModItems.folder) return;
		if (folder.stackTagCompound == null) initFolder(folder);
		if (bookname == null || bookname.equals("")) {
			folder.stackTagCompound.removeTag("Name");
		} else {
			folder.stackTagCompound.setString("Name", bookname);
		}
	}

	public static String getName(ItemStack folder) {
		if (folder == null) return null;
		if (folder.getItem() != ModItems.folder) return null;
		if (folder.stackTagCompound == null) initFolder(folder);
		if (!folder.stackTagCompound.hasKey("Name")) return null;
		return folder.stackTagCompound.getString("Name");
	}

	private static NBTTagCompound getInventoryCompound(ItemStack folder) {
		if (folder == null) return null;
		if (folder.getItem() != ModItems.folder) return null;
		if (folder.stackTagCompound == null) initFolder(folder);
		if (!folder.stackTagCompound.hasKey("Pages")) {
			folder.stackTagCompound.setTag("Pages", new NBTTagCompound());
		}
		return folder.stackTagCompound.getCompoundTag("Pages");
	}

	public static int getItemCount(ItemStack folder) {
		if (getInventoryCompound(folder) == null) return 0;
		return getInventoryCompound(folder).func_150296_c().size();
	}

	public static int getLargestSlotId(ItemStack folder) {
		int largest = 0;
		NBTTagCompound compound = getInventoryCompound(folder);
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

	public static ItemStack getItem(ItemStack folder, int slot) {
		NBTTagCompound data = getInventoryCompound(folder);
		if (data != null && data.hasKey("" + slot)) { return ItemStack.loadItemStackFromNBT(data.getCompoundTag("" + slot)); }
		return null;
	}

	public static ItemStack setItem(ItemStack folder, int slot, ItemStack page) {
		if (!isItemValid(page)) return page;
		NBTTagCompound data = getInventoryCompound(folder);
		if (data == null) return page;
		ItemStack previous = null;
		previous = removeItem(folder, slot);
		if (page != null && page.stackSize > 0) {
			data.setTag("" + slot, page.writeToNBT(new NBTTagCompound()));
		} else {
			data.removeTag("" + slot);
		}
		return previous;
	}

	public static ItemStack removeItem(ItemStack folder, int slot) {
		NBTTagCompound data = getInventoryCompound(folder);
		ItemStack itemstack = null;
		if (data != null) {
			if (data.hasKey("" + slot)) {
				itemstack = ItemStack.loadItemStackFromNBT(data.getCompoundTag("" + slot));
			}
			data.removeTag("" + slot);
		}
		return itemstack;
	}

	public static ItemStack addItem(ItemStack folder, ItemStack page) {
		if (!isItemValid(page)) return page;
		NBTTagCompound data = getInventoryCompound(folder);
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

	public static List<ItemStack> getItems(ItemStack folder) {
		List<ItemStack> pages = new ArrayList<ItemStack>();
		NBTTagCompound compound = getInventoryCompound(folder);
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
	 */
	public static void updatePages(ItemStack folder) {
		NBTTagCompound compound = getInventoryCompound(folder);
		if (compound == null) return;
		Collection<String> tagnames = new ArrayList<String>();
		tagnames.addAll(compound.func_150296_c());
		for (String tagname : tagnames) {
			NBTTagCompound pagedata = compound.getCompoundTag(tagname);
			ItemStack page = ItemStack.loadItemStackFromNBT(pagedata);
			List<ItemStack> results = SymbolRemappings.remap(page);
			int slot = Integer.parseInt(tagname);
			if (results.size() == 0) {
				removeItem(folder, slot);
				continue;
			}
			if (results.size() == 1) {
				setItem(folder, slot, results.get(0));
			}
			if (results.size() != 1) {
				removeItem(folder, slot);
				for (ItemStack item : results) {
					addItem(folder, item);
				}
			}
		}
	}

	/**
	 * Writes the symbol to the first blank page in the book Can draw from the provided paperstack if there are not available blank pages Returns false on
	 * failure
	 * @param folder Folder container
	 * @param symbol Symbol to write
	 * @return
	 */
	public static boolean writeSymbol(ItemStack folder, String symbol) {
		List<ItemStack> pages = getItems(folder);
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
