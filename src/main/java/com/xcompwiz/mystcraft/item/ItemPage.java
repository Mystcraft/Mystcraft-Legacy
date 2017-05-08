package com.xcompwiz.mystcraft.item;

import java.util.Arrays;
import java.util.List;

import com.xcompwiz.mystcraft.api.item.IItemOrderablePageProvider;
import com.xcompwiz.mystcraft.api.item.IItemPageProvider;
import com.xcompwiz.mystcraft.api.item.IItemWritable;
import com.xcompwiz.mystcraft.api.symbol.IAgeSymbol;
import com.xcompwiz.mystcraft.data.ModAchievements;
import com.xcompwiz.mystcraft.data.ModItems;
import com.xcompwiz.mystcraft.page.Page;
import com.xcompwiz.mystcraft.symbol.SymbolManager;
import com.xcompwiz.mystcraft.symbol.SymbolRemappings;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemPage extends Item implements IItemWritable, IItemPageProvider, IItemOnLoadable {

	public ItemPage() {
		setMaxStackSize(64);
		setHasSubtypes(true);
		setUnlocalizedName("myst.page");
		setCreativeTab(null);
	}

	@Override
	public String getItemStackDisplayName(ItemStack itemstack) {
		if (itemstack.stackTagCompound != null) {
			if (Page.isLinkPanel(itemstack)) return I18n.format(this.getUnlocalizedName(itemstack) + ".panel.name");
			if (Page.isBlank(itemstack)) return I18n.format(this.getUnlocalizedName(itemstack) + ".blank.name");
			String symbolId = Page.getSymbol(itemstack);
			IAgeSymbol symbol = SymbolManager.getAgeSymbol(symbolId);
			if (symbol == null) { return I18n.format(this.getUnlocalizedName(itemstack) + ".symbol.name") + " (Unknown: " + symbolId + ")"; }
			return I18n.format(this.getUnlocalizedName(itemstack) + ".symbol.name") + " (" + symbol.displayName() + ")";
		}
		return I18n.format(this.getUnlocalizedName(itemstack) + ".blank.name");
	}

	@Override
	public void addInformation(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean advancedTooltip) {
		if (itemstack.stackTagCompound != null) {
			Page.getTooltip(itemstack, list);
		}
	}

	@Override
	public void onUpdate(ItemStack itemstack, World world, Entity entity, int i, boolean flag) {
		if (world.isRemote) { return; }
		if (Page.isBlank(itemstack)) {
			itemstack.stackTagCompound = null;
		}
		if (itemstack.stackTagCompound == null) {
			itemstack = new ItemStack(Items.paper);
			return;
		}
		if (entity instanceof EntityPlayer) {
			EntityPlayer player = ((EntityPlayer) entity);
			player.addStat(ModAchievements.symbol, 1);
			remapItemstack(player, itemstack);
		}
	}

	public static void remapItemstack(EntityPlayer player, ItemStack itemstack) {
		List<ItemStack> mapping = SymbolRemappings.remap(itemstack);
		if (mapping.size() == 0) {
			itemstack.stackSize = 0;
		}
		if (mapping.size() != 1 && player != null) {
			ItemStack folder = new ItemStack(ModItems.folder);
			IItemOrderablePageProvider item = (IItemOrderablePageProvider) folder.getItem();
			folder.stackTagCompound = new NBTTagCompound();
			for (ItemStack mappeditemstack : mapping) {
				item.addPage(player, folder, mappeditemstack);
			}
			itemstack = folder;
			itemstack.stackSize = folder.getCount();
			itemstack.stackTagCompound = folder.stackTagCompound;
		}
	}

	public static ItemStack createItemstack(ItemStack prototype) {
		if (prototype.getItem() == Items.paper) {
			--prototype.stackSize;
			return Page.createPage();
		}
		return null;
	}

	@Override
	public String getDisplayName(EntityPlayer player, ItemStack itemstack) {
		return this.getItemStackDisplayName(itemstack);
	}

	@Override
	public void setDisplayName(EntityPlayer player, ItemStack itemstack, String name) {}

	@Override
	public boolean writeSymbol(EntityPlayer player, ItemStack itemstack, String symbol) {
		if (!Page.isBlank(itemstack)) return false;
		Page.setSymbol(itemstack, symbol);
		return true;
	}

	@Override
	public List<ItemStack> getPageList(EntityPlayer player, ItemStack itemstack) {
		return Arrays.asList(itemstack);
	}

	@Override
	public ItemStack onLoad(ItemStack itemstack) {
		remapItemstack(null, itemstack);
		return itemstack;
	}
}
