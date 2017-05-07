package com.xcompwiz.mystcraft.item;

import com.xcompwiz.mystcraft.linking.LinkOptions;

import net.minecraft.item.ItemStack;

public class LinkItemUtils {
	public static Integer getTargetDimension(ItemStack book) {
		if (book == null) return null;
		if (book.stackTagCompound == null) return null;
		if (book.getItem() instanceof ItemLinking) { return LinkOptions.getDimensionUID(book.stackTagCompound); }
		return null;
	}
}
