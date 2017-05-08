package com.xcompwiz.mystcraft.item;

import java.util.List;

import com.xcompwiz.mystcraft.data.ModItems;
import com.xcompwiz.mystcraft.page.Page;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemLinkbookUnlinked extends Item {

	public ItemLinkbookUnlinked() {
		setMaxStackSize(16);
		setUnlocalizedName("myst.unlinkedbook");
		setCreativeTab(CreativeTabs.TRANSPORTATION);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister register) {
		this.itemIcon = register.registerIcon("mystcraft:unlinked");
	}

	/**
	 * If this function returns true (or the item is damageable), the ItemStack's NBT tag will be sent to the client.
	 */
	@Override
	public boolean getShareTag() {
		return true;
	}

	@Override
	public void addInformation(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean advancedTooltip) {
		if (itemstack.stackTagCompound != null) {
			Page.getTooltip(itemstack, list);
		}
	}

	public static ItemStack createItem(ItemStack linkpanel, ItemStack covermat) {
		ItemStack linkbook = new ItemStack(ModItems.unlinked);
		linkbook.stackTagCompound = (NBTTagCompound) linkpanel.stackTagCompound.copy();
		return linkbook;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer) {
		if (world.isRemote || itemstack.getCount() > 1) return itemstack;
		ItemStack linkbook = new ItemStack(ModItems.linkbook);
		((ItemLinkbook) ModItems.linkbook).initialize(world, linkbook, entityplayer);
		Page.applyLinkPanel(itemstack, linkbook);
		entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, linkbook);
		itemstack.stackSize = 0;
		return linkbook;
	}
}
