package com.xcompwiz.mystcraft.inventory;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import com.xcompwiz.mystcraft.item.ItemLinking;

public class InventoryBook implements IInventory {

	private EntityLiving	owner;
	private ItemStack		itemstacks[];

	public InventoryBook(EntityLiving owner) {
		this.owner = owner;
		this.itemstacks = new ItemStack[1];
	}

	private float getBookMaxHealth() {
		ItemStack book = getBook();
		return ItemLinking.getMaxHealth(book);
	}

	public void setBook(ItemStack itemstack) {
		itemstacks[0] = itemstack;
		markDirty();
		if (itemstacks[0] == null) {
			owner.setDead();
		} else {
			owner.isDead = false;
		}
		owner.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(getBookMaxHealth());
	}

	public ItemStack getBook() {
		return itemstacks[0];
	}

	@Override
	public String getInventoryName() {
		return "Book Entity";
	}

	@Override
	public int getSizeInventory() {
		return (owner.isDead ? 0 : 1);
	}

	@Override
	public int getInventoryStackLimit() {
		return (owner.isDead ? 0 : 1);
	}

	@Override
	public ItemStack getStackInSlot(int var1) {
		return itemstacks[0];
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		if (itemstacks[i] != null) {
			if (itemstacks[i].stackSize <= j) {
				ItemStack itemstack = itemstacks[i];
				itemstacks[i] = null;
				owner.setDead();
				return itemstack;
			}
			ItemStack itemstack1 = itemstacks[i].splitStack(j);
			if (itemstacks[i].stackSize == 0) {
				itemstacks[i] = null;
				owner.setDead();
			}
			return itemstack1;
		}
		return null;
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

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		if (itemstack != null && !isItemValidForSlot(i, itemstack)) {
			itemstack.stackSize = 0;
			return;
		}
		if (i == 0) {
			setBook(itemstack);
			return;
		}
		itemstacks[i] = itemstack;
		if (itemstack != null && itemstack.stackSize > getInventoryStackLimit()) {
			itemstack.stackSize = getInventoryStackLimit();
		}
	}

	@Override
	public void markDirty() {}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		if (entityplayer.inventory.getItemStack() != null) return true;
		if (!owner.isEntityAlive()) return false;
		return entityplayer.getDistanceSq(owner.posX + 0.5D, owner.posY + 0.5D, owner.posZ + 0.5D) <= 64D;
	}

	@Override
	public void openInventory() {}

	@Override
	public void closeInventory() {}

	public void linkEntity(Entity entity) {
		ItemStack book = getBook();
		if (book == null) return;
		if (!(book.getItem() instanceof ItemLinking)) return;
		((ItemLinking) book.getItem()).activate(book, owner.worldObj, entity);
	}

	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}

	@Override
	public boolean isItemValidForSlot(int slotIndex, ItemStack itemstack) {
		if (itemstack == null) return false;
		if (itemstack.getItem() instanceof ItemLinking && slotIndex == 0) return true;
		return false;
	}
}
