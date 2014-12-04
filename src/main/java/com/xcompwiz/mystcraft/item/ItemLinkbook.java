package com.xcompwiz.mystcraft.item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.xcompwiz.mystcraft.api.item.IItemRenameable;
import com.xcompwiz.mystcraft.core.InternalAPI;
import com.xcompwiz.mystcraft.data.ModAchievements;
import com.xcompwiz.mystcraft.linking.LinkOptions;
import com.xcompwiz.mystcraft.page.Page;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemLinkbook extends ItemLinking implements IItemRenameable {

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister register) {
		this.itemIcon = register.registerIcon("mystcraft:linkingbook");
	}

	@Override
	protected void initialize(World world, ItemStack itemstack, Entity entity) {
		if (itemstack.stackTagCompound == null) {
			itemstack.setTagCompound(InternalAPI.linking.createLinkInfoFromPosition(world, entity).getTagCompound());
		}
	}

	@Override
	public EnumRarity getRarity(ItemStack itemstack) {
		return EnumRarity.rare;
	}

	@Override
	public Collection<String> getAuthors(ItemStack itemstack) {
		if (itemstack.stackTagCompound != null && itemstack.stackTagCompound.hasKey("Author")) {
			ArrayList<String> authors = new ArrayList<String>();
			authors.add(itemstack.stackTagCompound.getString("Author"));
			return authors;
		}
		return Collections.EMPTY_SET;
	}

	/**
	 * Called when item is crafted/smelted. Used only by maps so far.
	 */
	@Override
	public void onCreated(ItemStack par1ItemStack, World par2World, EntityPlayer player) {
		player.addStat(ModAchievements.linkbook, 1);
	}

	@Override
	public String getDisplayName(EntityPlayer player, ItemStack itemstack) {
		return LinkOptions.getDisplayName(itemstack.stackTagCompound);
	}

	@Override
	public void setDisplayName(EntityPlayer player, ItemStack itemstack, String name) {
		LinkOptions.setDisplayName(itemstack.stackTagCompound, name);
	}

	@Override
	public List<ItemStack> getPageList(EntityPlayer player, ItemStack itemstack) {
		List<ItemStack> pages = new ArrayList<ItemStack>();
		pages.add(Page.createLinkPage());
		return pages;
	}
}
