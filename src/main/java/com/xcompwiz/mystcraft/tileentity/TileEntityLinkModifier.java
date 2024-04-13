package com.xcompwiz.mystcraft.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import com.xcompwiz.mystcraft.api.item.IItemRenameable;
import com.xcompwiz.mystcraft.entity.EntityDummy;
import com.xcompwiz.mystcraft.item.ItemLinking;
import com.xcompwiz.mystcraft.linking.LinkOptions;

public class TileEntityLinkModifier extends TileEntityBookRotateable {

	@Override
	public void setYaw(int rotation) {
		rotation = rotation - (rotation % 90);
		super.setYaw(rotation);
	}

	@Override
	public boolean canUpdate() {
		return false;
	}

	public void setBookTitle(EntityPlayer player, String text) {
		ItemStack itemstack = getBook();
		if (itemstack != null && (itemstack.getItem() instanceof IItemRenameable)) {
			((IItemRenameable) itemstack.getItem()).setDisplayName(player, itemstack, text);
		}
	}

	public String getLinkDimensionUID() {
		ItemStack itemstack = getBook();
		if (itemstack != null) {
			if (itemstack.getItem() instanceof ItemLinking) {
				((ItemLinking)itemstack.getItem()).validate(worldObj, itemstack, new EntityDummy(worldObj, this.xCoord, this.yCoord, this.zCoord, 0, 0));
				return "" + String.valueOf(LinkOptions.getDimensionUID(itemstack.stackTagCompound));
			}
		}
		return "";
	}

	public boolean getLinkOption(String name) {
		ItemStack itemstack = getBook();
		if (itemstack != null) {
			if (itemstack.getItem() instanceof ItemLinking) {
				((ItemLinking)itemstack.getItem()).validate(worldObj, itemstack, new EntityDummy(worldObj, this.xCoord, this.yCoord, this.zCoord, 0, 0));
				return LinkOptions.getFlag(itemstack.stackTagCompound, name);
			}
		}
		return false;
	}

	public void setLinkOption(String name, boolean value) {
		ItemStack itemstack = getBook();
		if (itemstack != null) {
			if (itemstack.getItem() instanceof ItemLinking) {
				((ItemLinking)itemstack.getItem()).validate(worldObj, itemstack, new EntityDummy(worldObj, this.xCoord, this.yCoord, this.zCoord, 0, 0));
				LinkOptions.setFlag(itemstack.stackTagCompound, name, value);
			}
		}
	}

	public String getLinkProperty(String name) {
		ItemStack itemstack = getBook();
		if (itemstack != null) {
			if (itemstack.getItem() instanceof ItemLinking) {
				((ItemLinking)itemstack.getItem()).validate(worldObj, itemstack, new EntityDummy(worldObj, this.xCoord, this.yCoord, this.zCoord, 0, 0));
				return LinkOptions.getProperty(itemstack.stackTagCompound, name);
			}
		}
		return null;
	}

	public void setLinkProperty(String name, String value) {
		ItemStack itemstack = getBook();
		if (itemstack != null) {
			if (itemstack.getItem() instanceof ItemLinking) {
				((ItemLinking)itemstack.getItem()).validate(worldObj, itemstack, new EntityDummy(worldObj, this.xCoord, this.yCoord, this.zCoord, 0, 0));
				LinkOptions.setProperty(itemstack.stackTagCompound, name, value);
			}
		}
	}
}
