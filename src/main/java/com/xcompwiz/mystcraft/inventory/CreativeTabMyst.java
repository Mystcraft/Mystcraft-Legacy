package com.xcompwiz.mystcraft.inventory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.xcompwiz.mystcraft.item.ItemAgebook;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CreativeTabMyst extends CreativeTabs {

	private List<ItemStack>	forcelist		= new ArrayList<ItemStack>();
	private boolean			hasSearchBar	= false;

	public CreativeTabMyst(String label) {
		super(label);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Item getTabIconItem() {
		return ItemAgebook.instance;
	}

	/**
	 * only shows items which have tabToDisplayOn == this
	 */
	@Override
	public void displayAllReleventItems(List list) {
		Iterator<Item> iterator = Item.itemRegistry.iterator();

		while (iterator.hasNext()) {
			Item item = iterator.next();
			if (item == null) continue;

			for (CreativeTabs tab : item.getCreativeTabs()) {
				if (tab == this) {
					item.getSubItems(item, this, list);
				}
			}
		}
		for (ItemStack itemstack : forcelist) {
			list.add(itemstack);
		}
	}

	public void registerItemStack(ItemStack itemstack) {
		forcelist.add(itemstack);
	}

	public void setHasSearchBar(boolean flag) {
		hasSearchBar = flag;
	}

	@Override
	public boolean hasSearchBar() {
		return hasSearchBar;
	}
}
