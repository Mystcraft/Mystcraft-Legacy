package com.xcompwiz.mystcraft.inventory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.xcompwiz.mystcraft.api.impl.InternalAPI;
import com.xcompwiz.mystcraft.data.ModItems;
import com.xcompwiz.mystcraft.symbol.SymbolRemappings;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class InventoryFolder {

    //TODO: Refactor me! I'm a mess! - Hellfire> don't mind me, just updating, not refactoring :^)

	public static boolean isItemValid(@Nonnull ItemStack itemstack) {
		if (itemstack.isEmpty()) return true;
		if (itemstack.getCount() != 1) return false;
		if (itemstack.getItem() == ModItems.page) return true;
		if (itemstack.getItem() == Items.PAPER) return true;
		return false;
	}

	private static void initFolder(@Nonnull ItemStack folder) {
		if (folder.getTagCompound() == null) {
			folder.setTagCompound(new NBTTagCompound());
		}
	}

	public static void setName(@Nonnull ItemStack folder, String bookname) {
		if (folder.isEmpty()) return;
		if (!folder.getItem().equals(ModItems.folder)) return;
		if (folder.getTagCompound() == null) {
			initFolder(folder);
		}
		if (bookname == null || bookname.equals("")) {
			folder.getTagCompound().removeTag("Name");
		} else {
			folder.getTagCompound().setString("Name", bookname);
		}
	}

	@Nullable
	public static String getName(@Nonnull ItemStack folder) {
		if (folder.isEmpty()) return null;
		if (!folder.getItem().equals(ModItems.folder)) return null;

		if (folder.getTagCompound() == null) {
			initFolder(folder);
		}
		if (!folder.getTagCompound().hasKey("Name")) {
			return null;
		}
		return folder.getTagCompound().getString("Name");
	}

	@Nullable
	private static NBTTagCompound getInventoryCompound(@Nonnull ItemStack folder) {
		if (folder.isEmpty()) return null;
		if (!folder.getItem().equals(ModItems.folder)) return null;
		if (folder.getTagCompound() == null) {
			initFolder(folder);
		}
		if (!folder.getTagCompound().hasKey("Pages")) {
			folder.getTagCompound().setTag("Pages", new NBTTagCompound());
		}
		return folder.getTagCompound().getCompoundTag("Pages");
	}

	public static int getItemCount(@Nonnull ItemStack folder) {
		if (getInventoryCompound(folder) == null) return 0;
		return getInventoryCompound(folder).getKeySet().size();
	}

	public static int getLargestSlotId(@Nonnull ItemStack folder) {
		int largest = -1;
		NBTTagCompound compound = getInventoryCompound(folder);
		if (compound == null) return 0;
		for (String tagname : compound.getKeySet()) {
			int slot = Integer.parseInt(tagname);
			if (largest < slot) {
				largest = slot;
			}
		}
		return largest;
	}

	@Nonnull
	public static ItemStack getItem(@Nonnull ItemStack folder, int slot) {
		NBTTagCompound data = getInventoryCompound(folder);
		if (data != null && data.hasKey(String.valueOf(slot))) {
			return new ItemStack(data.getCompoundTag(String.valueOf(slot)));
		}
		return ItemStack.EMPTY;
	}

	@Nonnull
	public static ItemStack setItem(@Nonnull ItemStack folder, int slot, @Nonnull ItemStack page) {
		if (!isItemValid(page)) return page;
		NBTTagCompound data = getInventoryCompound(folder);
		if (data == null) {
			return page;
		}
		ItemStack previous = removeItem(folder, slot);
		if (!page.isEmpty() && page.getCount() > 0) {
			data.setTag(String.valueOf(slot), page.writeToNBT(new NBTTagCompound()));
		} else {
			data.removeTag(String.valueOf(slot));
		}
		return previous;
	}

	@Nonnull
	public static ItemStack removeItem(@Nonnull ItemStack folder, int slot) {
		NBTTagCompound data = getInventoryCompound(folder);
		ItemStack itemstack = ItemStack.EMPTY;
		String strSlot = String.valueOf(slot);
		if (data != null) {
			if (data.hasKey(strSlot)) {
				itemstack = new ItemStack(data.getCompoundTag(strSlot));
			}
			data.removeTag(strSlot);
		}
		return itemstack;
	}

    @Nonnull
	public static ItemStack addItem(@Nonnull ItemStack folder, @Nonnull ItemStack page) {
		if (!isItemValid(page)) {
			return page;
		}
		NBTTagCompound data = getInventoryCompound(folder);
		if (data == null) {
			return page;
		}
		int slot = 0;
		while (!page.isEmpty()) {
			if (!data.hasKey(String.valueOf(slot))) {
				ItemStack clone = page.copy();
				clone.setCount(1);
				data.setTag(String.valueOf(slot), clone.writeToNBT(new NBTTagCompound()));
				page.shrink(1);
				if (page.getCount() <= 0) {
					page = ItemStack.EMPTY;
				}
			}
			++slot;
		}
		return ItemStack.EMPTY;
	}

	public static List<ItemStack> getItems(@Nonnull ItemStack folder) {
		List<ItemStack> pages = new ArrayList<>();
		NBTTagCompound compound = getInventoryCompound(folder);
		if (compound == null) {
			return pages;
		}

		for (String tagname : compound.getKeySet()) {
			NBTTagCompound pagedata = compound.getCompoundTag(tagname);
			int slot = Integer.parseInt(tagname);
			while (pages.size() <= slot) {
				pages.add(ItemStack.EMPTY);
			}
			pages.set(slot, new ItemStack(pagedata));
		}
		return pages;
	}

	/**
	 * Performs some basic housekeeping. Handles symbol remappings
	 */
	public static void updatePages(@Nonnull ItemStack folder) {
		NBTTagCompound compound = getInventoryCompound(folder);
		if (compound == null) {
			return;
		}

		// Take a copy of the tag names to avoid concurrent modification issues (since we modify the set in the loop)
        Collection<String> tagnames = new ArrayList<String>();
        tagnames.addAll(compound.getKeySet());
        for (String tagname : tagnames) {
			NBTTagCompound pagedata = compound.getCompoundTag(tagname);
			ItemStack page = new ItemStack(pagedata);
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
	 * Writes the symbol to the first blank page in the book. Returns false on failure.
	 * @param folder Folder container
	 * @param symbol Symbol to write
	 * @return
	 */
	public static boolean writeSymbol(@Nonnull ItemStack folder, String symbol) {
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
