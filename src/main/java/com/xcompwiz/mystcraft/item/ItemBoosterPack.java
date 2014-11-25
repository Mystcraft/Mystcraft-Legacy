package com.xcompwiz.mystcraft.item;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;


import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemBoosterPack extends Item {

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister register) {
		this.itemIcon = register.registerIcon("mystcraft:booster");
	}

	@Override
	public boolean onItemUse(ItemStack itemstack, EntityPlayer entityplayer, World worldObj, int x, int y, int z, int face, float p_77648_8_, float p_77648_9_, float p_77648_10_) {
		if (worldObj.isRemote) return false;
		return true;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer) {
		if (world.isRemote) return itemstack;
		ItemStack newitemstack = ItemPortfolio.generateBooster(entityplayer.getRNG(), 7, 4, 4, 1);
		if (newitemstack == null) return itemstack;
		itemstack.stackSize--;
		if (itemstack.stackSize <= 0) {
			entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, null);
			itemstack = newitemstack;
		} else {
			if (!entityplayer.inventory.addItemStackToInventory(newitemstack)) {
				++itemstack.stackSize;
				return itemstack;
			}
		}
		return itemstack;
	}
}
