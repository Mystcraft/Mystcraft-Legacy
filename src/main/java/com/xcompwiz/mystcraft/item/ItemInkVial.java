package com.xcompwiz.mystcraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

public class ItemInkVial extends Item {

	public ItemInkVial() {
		setMaxStackSize(16);
		setUnlocalizedName("myst.vial");
		setCreativeTab(CreativeTabs.MATERIALS);
		setContainerItem(Items.GLASS_BOTTLE);
	}

	//public FluidStack getFluidStack(ItemStack itemstack) {
	//	return FluidContainerRegistry.getFluidForFilledItem(itemstack);
	//}
//
	//@Override
	//public IIcon getMaskIcon(ItemStack itemstack) {
	//	return mask;
	//}
//
	//@Override
	//public ResourceLocation getMaskResource(ItemStack itemstack) {
	//	return TextureMap.locationItemsTexture;
	//}
//
	//@Override
	//public IIcon getSubbedIcon(ItemStack itemstack) {
	//	return this.getFluidStack(itemstack).getFluid().getIcon();
	//}
//
	//@Override
	//public ResourceLocation getSubbedResource(ItemStack itemstack) {
	//	return getFluidStack(itemstack).getFluid().getSpriteNumber() == 0 ? TextureMap.locationBlocksTexture : TextureMap.locationItemsTexture;
	//}
//
	//@Override
	//public int getColor(ItemStack itemstack) {
	//	return this.getFluidStack(itemstack).getFluid().getColor() << 8 | (255 & 0xFF);
	//}
}
