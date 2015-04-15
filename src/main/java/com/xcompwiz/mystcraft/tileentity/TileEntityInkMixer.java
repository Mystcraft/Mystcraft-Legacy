package com.xcompwiz.mystcraft.tileentity;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.xcompwiz.mystcraft.Mystcraft;
import com.xcompwiz.mystcraft.data.InkEffects;
import com.xcompwiz.mystcraft.data.ModLinkEffects;
import com.xcompwiz.mystcraft.fluids.FluidUtils;
import com.xcompwiz.mystcraft.inventory.IItemBuilder;
import com.xcompwiz.mystcraft.item.ItemPage;
import com.xcompwiz.mystcraft.nbt.NBTUtils;
import com.xcompwiz.mystcraft.page.Page;

public class TileEntityInkMixer extends TileEntityRotatable implements IItemBuilder, ISidedInventory {

	private ItemStack				itemstacks[];

	private boolean					hasInk				= false;
	private HashMap<String, Float>	ink_probabilities	= new HashMap<String, Float>();

	private long					next_seed;

	private static final int		ink_in				= 0;
	private static final int		ink_out				= 2;
	private static final int		paper				= 1;

	private static int[]			isidedslots			= { paper, ink_in };

	public TileEntityInkMixer() {
		next_seed = new Random().nextLong();
		itemstacks = new ItemStack[3];
	}

	@Override
	public void setYaw(int rotation) {
		rotation = rotation - (rotation % 90);
		super.setYaw(rotation);
	}

	@Override
	public boolean isItemValidForSlot(int slotIndex, ItemStack itemstack) {
		if (itemstack == null) return false;
		if (slotIndex == ink_in && FluidContainerRegistry.isContainer(itemstack)) return true;
		if (slotIndex == paper && itemstack.getItem() == Items.paper) return true;
		return false;
	}

	@Override
	public int getSizeInventory() {
		return itemstacks.length;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		if (i >= itemstacks.length) { return null; }
		return itemstacks[i];
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		if (itemstacks[i] != null) {
			if (itemstacks[i].stackSize <= j) {
				ItemStack itemstack = itemstacks[i];
				itemstacks[i] = null;
				return itemstack;
			}
			ItemStack itemstack1 = itemstacks[i].splitStack(j);
			if (itemstacks[i].stackSize == 0) {
				itemstacks[i] = null;
			}
			return itemstack1;
		}
		return null;
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		itemstacks[i] = itemstack;
		if (itemstack != null && itemstack.stackSize > getInventoryStackLimit()) {
			itemstack.stackSize = getInventoryStackLimit();
		}
	}

	@Override
	public String getInventoryName() {
		return "Ink Mixer";
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

		hasInk = nbttagcompound.getBoolean("Ink");

		ink_probabilities = NBTUtils.readFloatMap(nbttagcompound.getCompoundTag("Probabilities"), new HashMap<String, Float>());
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		nbttagcompound.setTag("Items", NBTUtils.writeInventoryArray(new NBTTagList(), itemstacks));

		nbttagcompound.setBoolean("Ink", hasInk);

		nbttagcompound.setTag("Probabilities", NBTUtils.writeFloatMap(new NBTTagCompound(), ink_probabilities));
	}

	@Override
	public void buildItem(ItemStack itemstack, EntityPlayer player) {
		if (!canBuildItem()) return;
		if (itemstack.getItem() instanceof ItemPage) {
			Random rand = new Random(next_seed);
			for (Entry<String, Float> entry : ink_probabilities.entrySet()) {
				float f = entry.getValue() * 100;
				int i = rand.nextInt(100);
				if (i < f) {
					Page.addLinkProperty(itemstack, entry.getKey());
				}
			}
			next_seed = rand.nextLong();
			hasInk = false;
			ink_probabilities.clear();
			--(itemstacks[paper].stackSize);
			if (itemstacks[paper].stackSize <= 0) itemstacks[paper] = null;
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			this.markDirty();
		} else {
			itemstack.stackSize = 0;
		}
	}

	private boolean canBuildItem() {
		return itemstacks[paper] != null && itemstacks[paper].getItem() == Items.paper && hasInk;
	}

	public ItemStack getCraftedItem() {
		if (!canBuildItem()) return null;
		return Page.createLinkPage();
	}

	public boolean getHasInk() {
		return hasInk;
	}

	public void setHasInk(boolean b) {
		hasInk = b;
	}

	public long getNextSeed() {
		return next_seed;
	}

	public void setNextSeed(long seed) {
		this.next_seed = seed;
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

	@Override
	public boolean canUpdate() {
		return true;
	}

	@Override
	public void updateEntity() {
		if (worldObj.isRemote) return;
		// If we can pull from the in container and the out slot is either empty or contains the empty form of the container
		if (itemstacks[ink_in] != null && hasInk == false) {
			ItemStack container = itemstacks[ink_in];
			ItemStack emptycontainer = container.getItem().getContainerItem(container);
			if (emptycontainer == null || mergeItemStacksLeft(itemstacks[ink_out], emptycontainer) != itemstacks[ink_out]) {
				ItemStack result = fillBasinWithContainer(container);
				if (result != null) {
					itemstacks[ink_out] = mergeItemStacksLeft(itemstacks[ink_out], result);
					if (container.stackSize == 0) itemstacks[ink_in] = null;
				}
			}
		}
	}

	// XXX: (Fluids) Handle fluids better
	private ItemStack fillBasinWithContainer(ItemStack containerStack) {
		ItemStack container = containerStack.copy();
		container.stackSize = 1;
		FluidStack fluid = FluidContainerRegistry.getFluidForFilledItem(container);
		if (fluid != null) {
			if (!Mystcraft.validInks.contains(fluid.getFluid().getName())) return null;
			int used = 1000;
			if (used == fluid.amount) {
				--containerStack.stackSize;
				hasInk = true;
				container = FluidUtils.emptyContainer(container);
				return container;
			}
		}
		return null;
	}

	private ItemStack mergeItemStacksLeft(ItemStack left, ItemStack right) {
		if (right == null) return left;
		if (left == null) return right;
		if (left.getItem() != right.getItem()) {
			return left;
		} else if (left.hasTagCompound() != right.hasTagCompound()) {
			return left;
		} else if (left.hasTagCompound() && !left.getTagCompound().equals(right.getTagCompound())) {
			return left;
		} else if (left.getItem().getHasSubtypes() && left.getItemDamage() != right.getItemDamage()) {
			return left;
		} else if (left.stackSize + right.stackSize > left.getMaxStackSize()) { return left; }
		left = left.copy();
		left.stackSize += right.stackSize;
		right.stackSize = 0;
		return left;
	}

	public Map<String, Float> getInkProperties() {
		return Collections.unmodifiableMap(ink_probabilities);
	}

	public ItemStack addItems(ItemStack itemstack, int amount) {
		Map<String, Float> itemprobs = null;

		itemprobs = InkEffects.getItemEffects(itemstack);

		if (itemprobs == null) return itemstack;

		float total = 0.0F;
		for (Entry<String, Float> entry : itemprobs.entrySet()) {
			if (entry.getKey().equals("")) continue;
			if (!isPropertyAllowed(entry.getKey())) continue;
			total += entry.getValue();
		}
		float inverse = 1 - total;

		if (amount > itemstack.stackSize) amount = itemstack.stackSize;
		for (int i = 0; i < amount; ++i) {
			--itemstack.stackSize;
			for (Entry<String, Float> entry : ink_probabilities.entrySet()) {
				entry.setValue(entry.getValue() * inverse);
			}
			for (Entry<String, Float> entry : itemprobs.entrySet()) {
				if (entry.getKey().equals("")) continue;
				if (!isPropertyAllowed(entry.getKey())) continue;
				float prob = entry.getValue();
				Float f = ink_probabilities.get(entry.getKey());
				if (f != null) prob += f;
				ink_probabilities.put(entry.getKey(), prob);
			}
		}
		if (itemstack.stackSize <= 0) {
			itemstack = null;
		}
		return itemstack;
	}

	private boolean isPropertyAllowed(String property) {
		return ModLinkEffects.isPropertyAllowed(property);
	}

	@SuppressWarnings("unused")
	private void addTraitWithProbability(String trait, float prob) {
		float inverse = 1 - prob;
		for (Entry<String, Float> entry : ink_probabilities.entrySet()) {
			entry.setValue(entry.getValue() * inverse);
		}
		if (trait.equals("")) return;
		Float f = ink_probabilities.get(trait);
		if (f != null) prob += f;
		ink_probabilities.put(trait, prob);
	}
}
