package com.xcompwiz.mystcraft.tileentity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.xcompwiz.mystcraft.api.item.IItemRenameable;
import com.xcompwiz.mystcraft.entity.EntityDummy;
import com.xcompwiz.mystcraft.item.ItemLinking;
import com.xcompwiz.mystcraft.linking.DimensionUtils;
import com.xcompwiz.mystcraft.linking.LinkOptions;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class TileEntityLinkModifier extends TileEntityBookRotateable {

	@Override
	public void setYaw(int rotation) {
		rotation = rotation - (rotation % 90);
		super.setYaw(rotation);
	}

	public void setBookTitle(EntityPlayer player, String text) {
		ItemStack itemstack = getBook();
		if (!itemstack.isEmpty() && (itemstack.getItem() instanceof IItemRenameable)) {
			((IItemRenameable) itemstack.getItem()).setDisplayName(player, itemstack, text);
		}
	}
	
	public boolean isLinkDimensionDead() {
		ItemStack itemstack = getBook();
		if (itemstack.isEmpty())
			return false;
			
		if (!(itemstack.getItem() instanceof ItemLinking))
			return false;

		((ItemLinking) itemstack.getItem()).validate(world, itemstack, new EntityDummy(world, pos.getX(), pos.getY(), pos.getZ(), 0, 0));

		Integer dimId = LinkOptions.getDimensionUID(itemstack.getTagCompound());

		if (dimId != null)
			return DimensionUtils.isDimensionDead(dimId) || !DimensionUtils.checkDimensionUUID(dimId, LinkOptions.getUUID(itemstack.getTagCompound()));
		
		return false;
	}

	public void recycleDimension() {
		Integer dimId = getLinkDimensionID();
		if (dimId != null)
			DimensionUtils.markDimensionDead(dimId);
	}

	@Nullable
	public Integer getLinkDimensionID() {
		ItemStack itemstack = getBook();
		if (!itemstack.isEmpty()) {
			if (itemstack.getItem() instanceof ItemLinking) {
				((ItemLinking) itemstack.getItem()).validate(world, itemstack, new EntityDummy(world, pos.getX(), pos.getY(), pos.getZ(), 0, 0));
				return LinkOptions.getDimensionUID(itemstack.getTagCompound());
			}
		}
		return null;
	}

	@Nonnull
	public String getLinkDimensionUID() {
		Integer dimId = getLinkDimensionID();
		if (dimId != null) {
			return "" + String.valueOf(dimId);
		}
		return "";
	}

	public boolean getLinkOption(String name) {
		ItemStack itemstack = getBook();
		if (!itemstack.isEmpty()) {
			if (itemstack.getItem() instanceof ItemLinking) {
				((ItemLinking) itemstack.getItem()).validate(world, itemstack, new EntityDummy(world, pos.getX(), pos.getY(), pos.getZ(), 0, 0));
				return LinkOptions.getFlag(itemstack.getTagCompound(), name);
			}
		}
		return false;
	}

	public void setLinkOption(String name, boolean value) {
		ItemStack itemstack = getBook();
		if (!itemstack.isEmpty()) {
			if (itemstack.getItem() instanceof ItemLinking) {
				((ItemLinking) itemstack.getItem()).validate(world, itemstack, new EntityDummy(world, pos.getX(), pos.getY(), pos.getZ(), 0, 0));
				LinkOptions.setFlag(itemstack.getTagCompound(), name, value);
			}
		}
	}

	@Nullable
	public String getLinkProperty(String name) {
		ItemStack itemstack = getBook();
		if (!itemstack.isEmpty()) {
			if (itemstack.getItem() instanceof ItemLinking) {
				((ItemLinking) itemstack.getItem()).validate(world, itemstack, new EntityDummy(world, pos.getX(), pos.getY(), pos.getZ(), 0, 0));
				return LinkOptions.getProperty(itemstack.getTagCompound(), name);
			}
		}
		return null;
	}

	public void setLinkProperty(String name, String value) {
		ItemStack itemstack = getBook();
		if (!itemstack.isEmpty()) {
			if (itemstack.getItem() instanceof ItemLinking) {
				((ItemLinking) itemstack.getItem()).validate(world, itemstack, new EntityDummy(world, pos.getX(), pos.getY(), pos.getZ(), 0, 0));
				LinkOptions.setProperty(itemstack.getTagCompound(), name, value);
			}
		}
	}
}
