package com.xcompwiz.mystcraft.item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import com.xcompwiz.mystcraft.data.AchievementsMyst;
import com.xcompwiz.mystcraft.inventory.InventoryNotebook;
import com.xcompwiz.mystcraft.linking.DimensionUtils;
import com.xcompwiz.mystcraft.linking.LinkOptions;
import com.xcompwiz.mystcraft.oldapi.PositionableItem;
import com.xcompwiz.mystcraft.oldapi.internal.ILinkPropertyAPI;
import com.xcompwiz.mystcraft.page.IItemPageProvider;
import com.xcompwiz.mystcraft.page.Page;
import com.xcompwiz.mystcraft.world.agedata.AgeData;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemAgebook extends ItemLinking implements IItemPageProvider, IItemWritable {

	public static ItemAgebook	instance;

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister register) {
		this.itemIcon = register.registerIcon("mystcraft:agebook");
	}

	@Override
	protected void initialize(World world, ItemStack itemstack, Entity entity) {
		if (world.isRemote) return;
		if (itemstack.stackTagCompound != null) {
			AgeData data = AgeData.getAge(LinkOptions.getDimensionUID(itemstack.stackTagCompound), world.isRemote);
			if (data == null) {
				itemstack.stackTagCompound = null;
			}
		}
		if (itemstack.stackTagCompound == null) {
			AgeData bookdata = bindToNewDim(itemstack);
			bookdata.setPages(Arrays.asList(Page.createLinkPage()));
		}
	}

	public static AgeData bindToNewDim(ItemStack itemstack) {
		int dimUID = DimensionUtils.getNewDimensionUID();
		AgeData bookdata = DimensionUtils.createAge(DimensionUtils.convertDimensionUIDToID(dimUID));
		initializeCompound(itemstack, dimUID, bookdata);
		return bookdata;
	}

	public static AgeData getAgeData(World world, ItemStack itemstack) {
		if (itemstack.stackTagCompound == null) return null;
		instance.initialize(world, itemstack, null);
		int uid = LinkOptions.getDimensionUID(itemstack.stackTagCompound);
		return AgeData.getAge(uid, world.isRemote);
	}

	public static void initializeCompound(ItemStack itemstack, int dimId, AgeData bookdata) {
		itemstack.setTagCompound(new NBTTagCompound());
		LinkOptions.setDimensionUID(itemstack.stackTagCompound, dimId);
		LinkOptions.setDisplayName(itemstack.stackTagCompound, bookdata.getAgeName());
		LinkOptions.setFlag(itemstack.stackTagCompound, ILinkPropertyAPI.FLAG_GENERATE_PLATFORM, true);
	}

	@Override
	public EnumRarity getRarity(ItemStack itemstack) {
		return itemstack.isItemEnchanted() ? EnumRarity.rare : EnumRarity.epic;
	}

	@Override
	public Collection<String> getAuthors(ItemStack itemstack) {
		if (itemstack.stackTagCompound != null) {
			int uid = LinkOptions.getDimensionUID(itemstack.stackTagCompound);
			AgeData data = AgeData.getAge(uid, true);
			if (data != null) {
				Collection<String> authors = data.getAuthors();
				if (authors != null) return authors;
			}
		}
		return Collections.emptySet();
	}

	/**
	 * Called when item is crafted/smelted. Used in maps.
	 */
	@Override
	public void onCreated(ItemStack par1ItemStack, World par2World, EntityPlayer player) {
		player.addStat(AchievementsMyst.agebook, 1);
	}

	@Override
	public String getDisplayName(EntityPlayer player, ItemStack itemstack) {
		return LinkOptions.getDisplayName(itemstack.stackTagCompound);
	}

	@Override
	public void setDisplayName(EntityPlayer player, ItemStack itemstack, String name) {
		LinkOptions.setDisplayName(itemstack.stackTagCompound, name);
		AgeData data = AgeData.getAge(LinkOptions.getDimensionUID(itemstack.stackTagCompound), player.worldObj.isRemote);
		if (data != null) data.setAgeName(name);
	}

	@Override
	public boolean writeSymbol(EntityPlayer player, ItemStack target, String symbol, ItemStack paper_feeder) {
		AgeData agedata = ItemAgebook.getAgeData(player.worldObj, target);
		if (agedata != null && !agedata.isVisited()) {
			if (agedata.writeSymbol(symbol)) {
				agedata.addAuthor(player.getDisplayName());
				return true;
			}
		}
		return false;
	}

	public void addPages(EntityPlayer player, ItemStack itemstack, List<ItemStack> pages) {
		AgeData agedata = ItemAgebook.getAgeData(player.worldObj, itemstack);
		if (agedata != null && !agedata.isVisited()) {
			agedata.addPages(pages);
		}
	}

	public void addAuthor(EntityPlayer player, ItemStack itemstack) {
		AgeData agedata = ItemAgebook.getAgeData(player.worldObj, itemstack);
		if (agedata != null && !agedata.isVisited()) {
			agedata.addAuthor(player.getDisplayName());
		}
	}

	@Override
	public ItemStack removePage(EntityPlayer player, ItemStack itemstack, int index) {
		return null;
	}

	@Override
	public List<ItemStack> getPageList(EntityPlayer player, ItemStack itemstack) {
		AgeData data = AgeData.getAge(LinkOptions.getDimensionUID(itemstack.stackTagCompound), player.worldObj.isRemote);
		if (data != null) return data.getPages();
		return null;
	}

	@Override
	public List<PositionableItem> getPagesForSurface(EntityPlayer player, ItemStack itemstack) {
		AgeData agedata = ItemAgebook.getAgeData(player.worldObj, itemstack);
		if (agedata != null) return getPositionedPages(agedata.getPages());
		return null;
	}

	public List<PositionableItem> getPositionedPages(List<ItemStack> pages) {
		List<PositionableItem> result = new ArrayList<PositionableItem>();
		int slot = 0;
		for (ItemStack page : pages) {
			PositionableItem positionable = new PositionableItem(page, slot);
			positionable.x = (slot % 5) * (InventoryNotebook.pagewidth + 1);
			positionable.y = (slot / 5) * (InventoryNotebook.pageheight + 1);
			result.add(positionable);
			++slot;
		}
		return result;
	}

	@Override
	public ItemStack addPage(EntityPlayer player, ItemStack itemstack, ItemStack page) {
		return page;
	}

	@Override
	public ItemStack addPage(ItemStack itemstack, ItemStack page, float x, float y) {
		return page;
	}

	@Override
	public void sort(ItemStack itemstack, SortType type, short width) {}
}
