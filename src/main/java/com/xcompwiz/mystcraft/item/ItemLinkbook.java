package com.xcompwiz.mystcraft.item;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.xcompwiz.mystcraft.api.impl.InternalAPI;
import com.xcompwiz.mystcraft.api.item.IItemPageProvider;
import com.xcompwiz.mystcraft.api.item.IItemRenameable;
import com.xcompwiz.mystcraft.data.ModAchievements;
import com.xcompwiz.mystcraft.linking.LinkOptions;
import com.xcompwiz.mystcraft.page.Page;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class ItemLinkbook extends ItemLinking implements IItemRenameable, IItemPageProvider {

	public ItemLinkbook() {
		setUnlocalizedName("myst.linkbook");
		setCreativeTab(CreativeTabs.TRANSPORTATION);
	}

	@Override
	@Nonnull
	public EnumRarity getRarity(@Nonnull ItemStack itemstack) {
		return EnumRarity.RARE;
	}

	@Override
	protected void initialize(World world, @Nonnull ItemStack itemstack, Entity entity) {
		if (itemstack.getTagCompound() == null) {
			itemstack.setTagCompound(InternalAPI.linking.createLinkInfoFromPosition(world, entity).getTagCompound());
		}
	}

	@Override
	public void onCreated(@Nonnull ItemStack par1ItemStack, World world, EntityPlayer player) {
		player.addStat(ModAchievements.linkbook, 1);
	}

	@Override
	public String getDisplayName(EntityPlayer player, @Nonnull ItemStack itemstack) {
		return LinkOptions.getDisplayName(itemstack.getTagCompound());
	}

	@Override
	public void setDisplayName(EntityPlayer player, @Nonnull ItemStack itemstack, String name) {
		LinkOptions.setDisplayName(itemstack.getTagCompound(), name);
	}

	@Override
	public List<ItemStack> getPageList(EntityPlayer player, @Nonnull ItemStack itemstack) {
		//TODO: Might want to create the actual link page in the future
		return Collections.singletonList(Page.createLinkPage());
	}

	@Override
	public Collection<String> getAuthors(@Nonnull ItemStack itemstack) {
		if (itemstack.getTagCompound() != null && itemstack.getTagCompound().hasKey("Author")) { return Collections.singleton(itemstack.getTagCompound().getString("Author")); }
		return Collections.emptySet();
	}

}
