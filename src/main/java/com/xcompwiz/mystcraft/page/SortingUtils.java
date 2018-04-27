package com.xcompwiz.mystcraft.page;

import java.util.Comparator;

import com.xcompwiz.mystcraft.api.impl.InternalAPI;
import com.xcompwiz.mystcraft.api.symbol.IAgeSymbol;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

/**
 * Provides helper methods and classes for sorting items, pages, and symbols
 * @author xcompwiz
 */
public final class SortingUtils {
	/**
	 * Sorts page items in NBT form by symbol display name. Note that, due to localization, this is only reliable on the client. Clients and the server may not
	 * agree on localization.
	 * @author xcompwiz
	 */
	public static class ComparatorTagItemSymbolAlphabetical implements Comparator<NBTTagCompound> {
		public static ComparatorTagItemSymbolAlphabetical	instance	= new ComparatorTagItemSymbolAlphabetical();

		@Override
		public int compare(NBTTagCompound itemdata1, NBTTagCompound itemdata2) {
			ItemStack itemstack1 = new ItemStack(itemdata1);
			ItemStack itemstack2 = new ItemStack(itemdata2);
			return ComparatorItemSymbolAlphabetical.instance.compare(itemstack1, itemstack2);
		}

	}

	/**
	 * Sorts page items in ItemStack form by symbol display name. Note that, due to localization, this is only reliable on the client. Clients and the server
	 * may not agree on localization.
	 * @author xcompwiz
	 */
	public static class ComparatorItemSymbolAlphabetical implements Comparator<ItemStack> {
		public static ComparatorItemSymbolAlphabetical	instance	= new ComparatorItemSymbolAlphabetical();

		@Override
		public int compare(@Nonnull ItemStack itemstack1, @Nonnull ItemStack itemstack2) {
			ResourceLocation id1 = InternalAPI.page.getPageSymbol(itemstack1);
			ResourceLocation id2 = InternalAPI.page.getPageSymbol(itemstack2);
			if (id1 == id2) return 0;
			if (id1 == null) return -1;
			if (id2 == null) return 1;
			IAgeSymbol symbol1 = InternalAPI.symbol.getSymbol(id1);
			IAgeSymbol symbol2 = InternalAPI.symbol.getSymbol(id2);
			return ComparatorSymbolAlphabetical.instance.compare(symbol1, symbol2);
		}
	}

	/**
	 * Sorts symbols by display name. Note that, due to localization, this is only reliable on the client. Clients and the server may not agree on localization.
	 * @author xcompwiz
	 */
	public static class ComparatorSymbolAlphabetical implements Comparator<IAgeSymbol> {
		public static ComparatorSymbolAlphabetical	instance	= new ComparatorSymbolAlphabetical();

		@Override
		public int compare(IAgeSymbol symbol1, IAgeSymbol symbol2) {
			if (symbol1 == symbol2) return 0;
			if (symbol1 == null) return 1;
			if (symbol2 == null) return -1;
			return symbol1.getLocalizedName().compareTo(symbol2.getLocalizedName());
		}
	}
}
