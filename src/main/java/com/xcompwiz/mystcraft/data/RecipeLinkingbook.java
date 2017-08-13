package com.xcompwiz.mystcraft.data;

import com.xcompwiz.mystcraft.api.MystObjects;
import com.xcompwiz.mystcraft.item.ItemLinkbookUnlinked;
import com.xcompwiz.mystcraft.page.Page;

import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

import javax.annotation.Nullable;

public class RecipeLinkingbook implements IRecipe {

	private ItemStack product = ItemStack.EMPTY;

	@Override
	public boolean matches(InventoryCrafting inventorycrafting, World world) {
		this.product = ItemStack.EMPTY;
		ItemStack linkpanel = ItemStack.EMPTY;
		ItemStack covermat = ItemStack.EMPTY;
		for (int i = 0; i < inventorycrafting.getSizeInventory(); ++i) {
			ItemStack itemstack = inventorycrafting.getStackInSlot(i);
			if (!itemstack.isEmpty()) {
				if (itemstack.hasTagCompound() && Page.isLinkPanel(itemstack)) {
					if (!linkpanel.isEmpty()) {
						return false;
					}
					linkpanel = itemstack;
					continue;
				}
				if (isValidCover(itemstack)) {
					if (!covermat.isEmpty()) {
						return false;
					}
					covermat = itemstack;
					continue;
				}
				return false;
			}
		}
		if (linkpanel.isEmpty()) return false;
		if (covermat.isEmpty()) return false;
		this.product = ItemLinkbookUnlinked.createItem(linkpanel, covermat);
		return true;
	}

	private boolean isValidCover(ItemStack itemstack) {
		return itemstack.getItem() == Items.LEATHER;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inventorycrafting) {
		return this.product.copy();
	}

	@Override
	public boolean canFit(int width, int height) {
		return width >= 2 || height >= 2;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return this.product;
	}

	@Override
	public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
		return ForgeHooks.defaultRecipeGetRemainingItems(inv);
	}

	@Override
	public IRecipe setRegistryName(ResourceLocation name) {
		return this; //Unchangeable
	}

	@Nullable
	@Override
	public ResourceLocation getRegistryName() {
		return new ResourceLocation(MystObjects.MystcraftModId, "internal/linkingbook");
	}

	@Override
	public Class<IRecipe> getRegistryType() {
		return IRecipe.class;
	}

}
