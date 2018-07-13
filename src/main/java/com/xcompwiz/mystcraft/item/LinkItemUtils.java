package com.xcompwiz.mystcraft.item;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.xcompwiz.mystcraft.linking.LinkOptions;

import net.minecraft.item.ItemStack;

public class LinkItemUtils {

	@Nullable
	public static Integer getTargetDimension(@Nonnull ItemStack book) {
		if (book.isEmpty())
			return null;
		if (book.getTagCompound() == null)
			return null;
		if (book.getItem() instanceof ItemLinking) {
			return LinkOptions.getDimensionUID(book.getTagCompound());
		}
		return null;
	}

}
