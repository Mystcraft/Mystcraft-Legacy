package com.xcompwiz.mystcraft.data;

import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

import com.xcompwiz.mystcraft.item.ItemLinkbookUnlinked;
import com.xcompwiz.mystcraft.page.Page;

public class RecipeLinkingbook implements IRecipe {

	private ItemStack	product;

	@Override
	public boolean matches(InventoryCrafting inventorycrafting, World world) {
		this.product = null;
		ItemStack linkpanel = null;
		ItemStack covermat = null;
		for (int i = 0; i < inventorycrafting.getSizeInventory(); ++i) {
			ItemStack itemstack = inventorycrafting.getStackInSlot(i);
			if (itemstack != null) {
				if (itemstack.stackTagCompound != null && Page.isLinkPanel(itemstack)) {
					if (linkpanel != null) return false;
					linkpanel = itemstack;
					continue;
				}
				if (isValidCover(itemstack)) {
					if (covermat != null) return false;
					covermat = itemstack;
					continue;
				}
				return false;
			}
		}
		if (linkpanel == null) return false;
		if (covermat == null) return false;
		this.product = ItemLinkbookUnlinked.createItem(linkpanel, covermat);
		return true;
	}

	private boolean isValidCover(ItemStack itemstack) {
		return itemstack.getItem() == Items.leather;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inventorycrafting) {
		return this.product.copy();
	}

	@Override
	public int getRecipeSize() {
		return 2;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return this.product;
	}

}
