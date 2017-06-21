package com.xcompwiz.mystcraft.tileentity;

import com.xcompwiz.mystcraft.item.ItemLinking;
import com.xcompwiz.mystcraft.linking.LinkOptions;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileEntityBook extends TileEntityBase implements InventoryUpdateListener, InventoryFilter {

	private IOInventory inventory;

	public TileEntityBook() {
		this.inventory = buildInventory();
	}

	protected IOInventory buildInventory() {
		return new IOInventory(this, new int[] { 0 }, new int[] { 0 }, EnumFacing.VALUES)
				.setListener(this)
				.applyFilter(this, 0)
				.setStackLimit(1, 0);
	}

	public void setBook(@Nonnull ItemStack itemstack) {
		if (!itemstack.isEmpty()) {
			ejectItem(this.inventory.getStackInSlot(0));
		}
		this.inventory.setStackInSlot(0, itemstack);
		markDirty();
		handleItemChange(0);
	}

	@Override
	public boolean canAcceptItem(int slot, @Nonnull ItemStack stack) {
		return !stack.isEmpty() && stack.getItem() instanceof ItemLinking;
	}

	@Override
	public void onChange() {
		markForUpdate();
	}

	@Nonnull
	public ItemStack getBook() {
		return this.inventory.getStackInSlot(0);
	}

	public String getBookTitle() {
		String title = null;
		if (!getBook().isEmpty() && getBook().getItem() instanceof ItemLinking) {
			title = LinkOptions.getDisplayName(getBook().getTagCompound());
		}
		return title;
	}

	@Nonnull
	public ItemStack getDisplayItem() {
		return this.inventory.getStackInSlot(0);
	}

	@Override
	public void readCustomNBT(NBTTagCompound compound) {
		super.readCustomNBT(compound);
		this.inventory.readNBT(compound.getCompoundTag("inventory"));
		for (int i = 0; i < inventory.getSlots(); i++) {
			handleItemChange(i);
		}
	}

	@Override
	public void writeCustomNBT(NBTTagCompound compound) {
		super.writeCustomNBT(compound);
		compound.setTag("inventory", this.inventory.writeNBT());
	}

	public void handleItemChange(int slot) {
		if (!canAcceptItem(slot, this.inventory.getStackInSlot(slot))) {
			ejectItem(this.inventory.getStackInSlot(slot));
			this.inventory.setStackInSlot(slot, ItemStack.EMPTY);
		}
		if(world != null && !world.isRemote) {
			markForUpdate();
		}
	}

	private void ejectItem(@Nonnull ItemStack itemstack) {
		if (itemstack.isEmpty() || world == null || world.isRemote) {
			return;
		}
		float f = world.rand.nextFloat() * 0.8F + 0.1F;
		float f1 = world.rand.nextFloat() * 0.8F + 0.1F;
		float f2 = world.rand.nextFloat() * 0.8F + 0.1F;
		EntityItem entityitem = new EntityItem(world, pos.getX() + f, pos.getY() + f1, pos.getZ() + f2, itemstack);
		float f3 = 0.05F;
		entityitem.motionX = (float) world.rand.nextGaussian() * f3;
		entityitem.motionY = (float) world.rand.nextGaussian() * f3 + 0.2F;
		entityitem.motionZ = (float) world.rand.nextGaussian() * f3;
		world.spawnEntity(entityitem);
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
