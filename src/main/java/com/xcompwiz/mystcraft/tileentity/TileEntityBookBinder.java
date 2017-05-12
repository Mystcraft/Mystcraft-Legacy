package com.xcompwiz.mystcraft.tileentity;

import java.util.LinkedList;
import java.util.List;

import com.xcompwiz.mystcraft.data.ModItems;
import com.xcompwiz.mystcraft.inventory.IItemBuilder;
import com.xcompwiz.mystcraft.inventory.InventoryFolder;
import com.xcompwiz.mystcraft.item.ItemAgebook;
import com.xcompwiz.mystcraft.item.ItemPage;
import com.xcompwiz.mystcraft.nbt.NBTUtils;
import com.xcompwiz.mystcraft.page.Page;
import com.xcompwiz.mystcraft.symbol.SymbolRemappings;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileEntityBookBinder extends TileEntityBase implements IItemBuilder, InventoryFilter {

	private IOInventory inventory;

	private List<ItemStack>	pages = new LinkedList<>();
	private String			pendingtitle = null;

	public TileEntityBookBinder() {
		this.inventory = buildInventory();
	}

	protected IOInventory buildInventory() {
		return new IOInventory(this, new int[] { 1 }, new int[0], EnumFacing.VALUES)
				.setMiscSlots(0)
				.applyFilter(this, 1);
	}

	@Override
	public boolean canAcceptItem(int slot, @Nonnull ItemStack stack) {
		if(stack.isEmpty()) return false;
		if (slot == 1) {
			if(!isValidCover(stack)) return false;
		}
		return true;
	}

	//XXX: (Crafting) Book binding/covers
	private boolean isValidCover(ItemStack itemstack) {
		if (itemstack.getItem().equals(Items.LEATHER)) return true;
		if (itemstack.getItem() == ModItems.folder && InventoryFolder.getLargestSlotId(itemstack) == -1) return true; //XXX: This is slightly broken client-side (NBT might be stale)
		return false;
	}

	@Override
	public void readCustomNBT(NBTTagCompound compound) {
		super.readCustomNBT(compound);
		this.inventory = IOInventory.deserialize(this, compound.getCompoundTag("items"));
		pages.clear();
		NBTUtils.readItemStackCollection(compound.getTagList("pages", Constants.NBT.TAG_COMPOUND), pages);
		SymbolRemappings.remap(pages);
		if(compound.hasKey("title")) {
			pendingtitle = compound.getString("title");
		} else {
			pendingtitle = null;
		}
	}

	@Override
	public void writeCustomNBT(NBTTagCompound compound) {
		super.writeCustomNBT(compound);
		compound.setTag("items", this.inventory.writeNBT());
		NBTTagList list = new NBTTagList();
		NBTUtils.writeItemStackCollection(list, pages);
		compound.setTag("pages", list);
		if(pendingtitle != null) {
			compound.setString("title", pendingtitle);
		}
	}

	@Nonnull
	public String getPendingTitle() {
		return (pendingtitle == null ? "" : pendingtitle);
	}

	public void setBookTitle(@Nullable String name) {
		this.pendingtitle = name;
	}

	@Override
	public void buildItem(@Nonnull ItemStack itemstack, @Nonnull EntityPlayer player) {
		if (!canBuildItem()) return;
		if (itemstack.getItem() instanceof ItemAgebook) {
			ItemAgebook.create(itemstack, player, pages, pendingtitle);
			pages.clear();
			pendingtitle = null;
			inventory.getStackInSlot(1).shrink(1);
			if (inventory.getStackInSlot(1).getCount() <= 0) {
				inventory.setStackInSlot(1, ItemStack.EMPTY);
			}
			markForUpdate();
		} else {
			itemstack.setCount(0);
		}
	}

	private boolean canBuildItem() {
		if(inventory.getStackInSlot(1).isEmpty()) return false;
		if(!isValidCover(inventory.getStackInSlot(1))) return false;
		if (pages.size() == 0) return false;
		if (!Page.isLinkPanel(pages.get(0))) return false;
		if (pendingtitle == null || pendingtitle.equals("")) return false;
		for (int i = 1; i < pages.size(); ++i) {
			if (Page.isLinkPanel(pages.get(i))) return false;
		}
		return true;
	}

	@Nonnull
	public ItemStack getCraftedItem() {
		if (!canBuildItem()) return ItemStack.EMPTY;
		return new ItemStack(ModItems.agebook);
	}

	public void setPages(@Nonnull List<ItemStack> pages) {
		if (this.world.isRemote) {
			this.pages = pages;
		}
	}

	public List<ItemStack> getPageList() {
		return pages;
	}

	@Nonnull
	public ItemStack insertPage(@Nonnull ItemStack stack, int i) {
		if (stack.isEmpty()) return ItemStack.EMPTY;
		if (stack.getItem().equals(Items.PAPER)) {
			while (stack.getCount() > 0) {
				ItemStack clone = stack.copy();
				clone.setCount(1);
				clone = ItemPage.createItemstack(clone);
				if (clone.isEmpty() || !insertPage(clone, i).isEmpty()) {
					return stack;
				}
				stack.shrink(1);
			}
			if (stack.getCount() == 0) {
				stack = ItemStack.EMPTY;
			}
			return stack;
		}
		if (stack.getItem() != ModItems.page) {
			return stack;
		}
		while (stack.getCount() > 0) {
			ItemStack clone = stack.copy();
			clone.setCount(1);
			pages.add(i, clone);
			stack.shrink(1);
		}
		markForUpdate();
		return ItemStack.EMPTY;
	}

	@Nonnull
	public ItemStack insertFromFolder(@Nonnull ItemStack folder, int index) {
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
				if (page.isEmpty()) continue;
				page = insertPage(page, index);
				if (page.isEmpty()) ++index;
				InventoryFolder.setItem(folder, slot, page);
			}
		}
		markForUpdate();
		return ItemStack.EMPTY;
	}

	@Nonnull
	public ItemStack removePage(int i) {
		if (i >= pages.size()) return ItemStack.EMPTY;
		ItemStack itemstack = pages.remove(i);
		markForUpdate();
		return itemstack;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && this.inventory.hasCapability(facing);
	}

	@Nullable
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return (T) this.inventory.getCapability(facing);
		}
		return null;
	}
}
