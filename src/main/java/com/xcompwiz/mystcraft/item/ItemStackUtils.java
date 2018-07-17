package com.xcompwiz.mystcraft.item;

import javax.annotation.Nonnull;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;

public class ItemStackUtils {

	@Nonnull
	public static ItemStack loadItemStackFromNBT(NBTTagCompound item) {
		ItemStack itemstack = new ItemStack(item);
		if (!itemstack.isEmpty() && itemstack.getItem() instanceof IItemOnLoadable) {
			itemstack = ((IItemOnLoadable) itemstack.getItem()).onLoad(itemstack);
		}
		return itemstack;
	}

	public static void spawnItems(IItemHandler handle, World world, BlockPos pos) {
		for (int l = 0; l < handle.getSlots(); ++l) {
			ItemStack itemstack = handle.getStackInSlot(l);
			if (itemstack.isEmpty()) {
				continue;
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
	}
}
