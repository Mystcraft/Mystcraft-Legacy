package com.xcompwiz.mystcraft.item;

import com.xcompwiz.mystcraft.data.Assets;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemMyGlasses extends ItemArmor {

	public ItemMyGlasses() {
		super(ArmorMaterial.GOLD, 1, 0);
		this.setUnlocalizedName("glasses");
		setHasSubtypes(false);
		this.setMaxStackSize(64);
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
		return "mystcraft:textures/models/glasses.png";
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister register) {
		this.itemIcon = register.registerIcon(Assets.Items.glasses);
	}

	@Override
	public String getUnlocalizedNameInefficiently(ItemStack itemstack) {
		return getUnlocalizedName();
	}

	@Override
	public String getUnlocalizedName() {
		return String.format("item.%s.%s", "myst", getUnwrappedUnlocalizedName(super.getUnlocalizedName()));
	}

	protected String getUnwrappedUnlocalizedName(String unlocalizedName) {
		return unlocalizedName.substring(unlocalizedName.indexOf(".") + 1);
	}

	@Override
	public void onUpdate(ItemStack itemstack, World worldObj, Entity entity, int slot, boolean isCurrent) {
		super.onUpdate(itemstack, worldObj, entity, slot, isCurrent);
		if (entity == null || !(entity instanceof EntityPlayer)) return;
		if (!entity.getCommandSenderName().equals("XCompWiz")) {
			EntityPlayer player = (EntityPlayer) entity;
			player.inventory.mainInventory[slot] = null;
		}
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World worldObj, EntityPlayer player) {
		if (worldObj.isRemote) return itemstack;
		return itemstack;
	}
}
