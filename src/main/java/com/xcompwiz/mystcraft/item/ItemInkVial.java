package com.xcompwiz.mystcraft.item;

import com.xcompwiz.mystcraft.client.render.IMaskRender;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemInkVial extends Item implements IMaskRender {

	protected IIcon	mask;

	public ItemInkVial() {
		setMaxStackSize(16);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister register) {
		this.itemIcon = register.registerIcon("mystcraft:inkvial");
		this.mask = register.registerIcon("mystcraft:inkvialmask");
	}

	public FluidStack getFluidStack(ItemStack itemstack) {
		return FluidContainerRegistry.getFluidForFilledItem(itemstack);
	}

	@Override
	public IIcon getMaskIcon(ItemStack itemstack) {
		return mask;
	}

	@Override
	public ResourceLocation getMaskResource(ItemStack itemstack) {
		return TextureMap.locationItemsTexture;
	}

	@Override
	public IIcon getSubbedIcon(ItemStack itemstack) {
		return this.getFluidStack(itemstack).getFluid().getIcon();
	}

	@Override
	public ResourceLocation getSubbedResource(ItemStack itemstack) {
		return getFluidStack(itemstack).getFluid().getSpriteNumber() == 0 ? TextureMap.locationBlocksTexture : TextureMap.locationItemsTexture;
	}

	@Override
	public int getColor(ItemStack itemstack) {
		return this.getFluidStack(itemstack).getFluid().getColor() << 8 | (255 & 0xFF);
	}
}
