package com.xcompwiz.mystcraft.client.render;

import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

public interface IMaskRender {

	IIcon getMaskIcon(ItemStack itemstack);

	ResourceLocation getMaskResource(ItemStack itemstack);

	IIcon getSubbedIcon(ItemStack itemstack);

	ResourceLocation getSubbedResource(ItemStack itemstack);

	int getColor(ItemStack itemstack);
}
