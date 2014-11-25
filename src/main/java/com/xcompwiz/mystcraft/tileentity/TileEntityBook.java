package com.xcompwiz.mystcraft.tileentity;

import java.util.Arrays;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.Constants;

import com.xcompwiz.mystcraft.item.ItemLinking;
import com.xcompwiz.mystcraft.linking.LinkOptions;
import com.xcompwiz.mystcraft.nbt.NBTUtils;

public class TileEntityBook extends TileEntity implements ISidedInventory {

	private ItemStack		itemstacks[];

	private static int[]	isidedslots	= { 0 };

	public TileEntityBook() {
		itemstacks = new ItemStack[1];
		tileEntityInvalid = false;
	}

	public void setBook(ItemStack itemstack) {
		if (itemstack != null) ejectItem(itemstacks[0]);
		itemstacks[0] = itemstack;
		markDirty();
		handleItemChange(0);
	}

	public ItemStack getBook() {
		return itemstacks[0];
	}

	public String getBookTitle() {
		String title = "";
		if (getBook() != null && getBook().getItem() instanceof ItemLinking) {
			title = LinkOptions.getDisplayName(getBook().stackTagCompound);
		}
		if (title == null) { return ""; }
		return title;
	}

	public ItemStack getDisplayItem() {
		return itemstacks[0];
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		NBTUtils.readInventoryArray(nbttagcompound.getTagList("Items", Constants.NBT.TAG_COMPOUND), itemstacks);
		for (int i = 0; i < itemstacks.length; ++i) {
			handleItemChange(i);
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		nbttagcompound.setTag("Items", NBTUtils.writeInventoryArray(new NBTTagList(), itemstacks));
	}

	@Override
	public int getSizeInventory() {
		return itemstacks.length;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return itemstacks[i];
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		if (itemstacks[i] != null) {
			if (itemstacks[i].stackSize <= j) {
				ItemStack itemstack = itemstacks[i];
				itemstacks[i] = null;
				handleItemChange(i);
				return itemstack;
			}
			ItemStack itemstack1 = itemstacks[i].splitStack(j);
			if (itemstacks[i].stackSize == 0) {
				itemstacks[i] = null;
			}
			handleItemChange(i);
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
		handleItemChange(i);
	}

	@Override
	public String getInventoryName() {
		return "Book Holder";
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
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		if (itemstack == null) return false;
		return itemstack.getItem() instanceof ItemLinking;
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
	public ItemStack getStackInSlotOnClosing(int par1) {
		if (itemstacks[par1] != null) {
			ItemStack itemstack = itemstacks[par1];
			itemstacks[par1] = null;
			return itemstack;
		}
		return null;
	}

	public void handleItemChange(int slot) {
		if (!isItemValidForSlot(slot, itemstacks[slot])) {
			ejectItem(itemstacks[slot]);
			itemstacks[slot] = null;
		}
		if (worldObj == null) return;
		// I think this is what causes the TE to resend its information to the clients
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	private void ejectItem(ItemStack itemstack) {
		if (itemstack == null || worldObj == null || worldObj.isRemote) { return; }
		float f = worldObj.rand.nextFloat() * 0.8F + 0.1F;
		float f1 = worldObj.rand.nextFloat() * 0.8F + 0.1F;
		float f2 = worldObj.rand.nextFloat() * 0.8F + 0.1F;
		EntityItem entityitem = new EntityItem(worldObj, xCoord + f, yCoord + f1, zCoord + f2, itemstack);
		float f3 = 0.05F;
		entityitem.motionX = (float) worldObj.rand.nextGaussian() * f3;
		entityitem.motionY = (float) worldObj.rand.nextGaussian() * f3 + 0.2F;
		entityitem.motionZ = (float) worldObj.rand.nextGaussian() * f3;
		worldObj.spawnEntityInWorld(entityitem);
	}

	/**
	 * Get the size of the side inventory.
	 */
	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		return isidedslots;
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack itemstack, int side) {
		if (Arrays.binarySearch(getAccessibleSlotsFromSide(side), slot) < 0) return false;
		return this.isItemValidForSlot(slot, itemstack);
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack par2ItemStack, int side) {
		if (Arrays.binarySearch(getAccessibleSlotsFromSide(side), slot) < 0) return false;
		return true;
	}
}
