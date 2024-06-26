package com.xcompwiz.mystcraft.api.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

/**
 * This interface allows you to create your own items which the Mystcraft writing desk can write to. Have your items implement it to allow them to be writable
 * in the desk.
 * @author xcompwiz
 */
public interface IItemWritable {

	/**
	 * This is called when the player tries to write a symbol to the item.
	 * @param player The player doing the writing
	 * @param itemstack The itemstack instance of the item being written to
	 * @param symbol The id of the symbol being written
	 * @return True on success
	 */
	boolean writeSymbol(EntityPlayer player, @Nonnull ItemStack itemstack, ResourceLocation symbol);

}
