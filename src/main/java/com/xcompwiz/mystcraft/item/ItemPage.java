package com.xcompwiz.mystcraft.item;

import java.util.Arrays;
import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.xcompwiz.mystcraft.data.ModAchievements;
import com.xcompwiz.mystcraft.data.ModItems;
import com.xcompwiz.mystcraft.inventory.InventoryNotebook;
import com.xcompwiz.mystcraft.oldapi.PositionableItem;
import com.xcompwiz.mystcraft.page.IItemPageProvider;
import com.xcompwiz.mystcraft.page.Page;
import com.xcompwiz.mystcraft.symbol.IAgeSymbol;
import com.xcompwiz.mystcraft.symbol.SymbolManager;
import com.xcompwiz.mystcraft.symbol.SymbolRemappings;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemPage extends Item implements IItemPageProvider, IItemWritable {

	public ItemPage() {
		super();
		setMaxStackSize(16);
		setHasSubtypes(true);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister register) {}

	/**
	 * If this function returns true the ItemStack's NBT tag will be sent to the client.
	 */
	@Override
	public boolean getShareTag() {
		return true;
	}

	@Override
	public String getItemStackDisplayName(ItemStack itemstack) {
		if (itemstack.stackTagCompound != null) {
			if (Page.isLinkPanel(itemstack)) return StatCollector.translateToLocal(this.getUnlocalizedName(itemstack)+".panel.name");
			if (Page.isBlank(itemstack)) return StatCollector.translateToLocal(this.getUnlocalizedName(itemstack)+".blank.name");
			String symbolId = Page.getSymbol(itemstack);
			IAgeSymbol symbol = SymbolManager.getAgeSymbol(symbolId);
			if (symbol == null) {
				return StatCollector.translateToLocal(this.getUnlocalizedName(itemstack)+".symbol.name") + " (Unknown: " + symbolId + ")";
			}
			return StatCollector.translateToLocal(this.getUnlocalizedName(itemstack)+".symbol.name") + " (" + symbol.displayName() + ")";
		}
		return StatCollector.translateToLocal(this.getUnlocalizedName(itemstack)+".blank.name");
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
			((EntityPlayer) entity).addStat(ModAchievements.symbol, 1);
		}
		remapItemstack(itemstack);
	}

	public static void remapItemstack(ItemStack itemstack) {
		List<ItemStack> mapping = SymbolRemappings.remap(itemstack);
		if (mapping.size() == 0) {
			itemstack.stackSize = 0;
		}
		if (mapping.size() != 1) {
			ItemStack notebook = new ItemStack(ModItems.notebook);
			notebook.stackTagCompound = new NBTTagCompound();
			for (ItemStack item : mapping) {
				InventoryNotebook.addItem(notebook, item);
			}
			itemstack = notebook;
			itemstack.stackSize = notebook.stackSize;
			itemstack.stackTagCompound = notebook.stackTagCompound;
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
	public boolean writeSymbol(EntityPlayer player, ItemStack itemstack, String symbol, ItemStack paper_feeder) {
		if (!Page.isBlank(itemstack)) return false;
		Page.setSymbol(itemstack, symbol);
		return true;
	}

	@Override
	public ItemStack removePage(EntityPlayer player, ItemStack itemstack, int index) {
		return null;
	}

	@Override
	public List<ItemStack> getPageList(EntityPlayer player, ItemStack itemstack) {
		return Arrays.asList(itemstack);
	}

	@Override
	public List<PositionableItem> getPagesForSurface(EntityPlayer player, ItemStack itemstack) {
		PositionableItem pos = new PositionableItem(itemstack, 0);
		return Arrays.asList(pos);
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
