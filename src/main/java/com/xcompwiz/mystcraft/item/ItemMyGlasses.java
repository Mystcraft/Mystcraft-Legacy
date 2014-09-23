package com.xcompwiz.mystcraft.item;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

import com.xcompwiz.mystcraft.Mystcraft;
import com.xcompwiz.mystcraft.data.Assets;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemMyGlasses extends ItemArmor {

	public ItemMyGlasses() {
		super(ArmorMaterial.GOLD, 1, 0);
		this.setUnlocalizedName("glasses");
		setHasSubtypes(false);
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
		return String.format("item.%s.%s", "mystcraft", getUnwrappedUnlocalizedName(super.getUnlocalizedName()));
	}

	protected String getUnwrappedUnlocalizedName(String unlocalizedName) {
		return unlocalizedName.substring(unlocalizedName.indexOf(".") + 1);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World worldObj, EntityPlayer player) {
		if (worldObj.isRemote) return itemstack;
		Mystcraft.instabilitybonusEnabled = !Mystcraft.instabilitybonusEnabled;
		player.addChatMessage(new ChatComponentText(String.format("Toggled State: " + Mystcraft.instabilitybonusEnabled)));
		return itemstack;
	}
}
