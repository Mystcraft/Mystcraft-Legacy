package com.xcompwiz.mystcraft.item;

import java.util.List;

import com.xcompwiz.mystcraft.Mystcraft;
import com.xcompwiz.mystcraft.api.item.IItemOrderablePageProvider;
import com.xcompwiz.mystcraft.api.item.IItemWritable;
import com.xcompwiz.mystcraft.data.ModGUIs;
import com.xcompwiz.mystcraft.inventory.InventoryFolder;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ItemFolder extends Item implements IItemOrderablePageProvider, IItemWritable, IItemOnLoadable {

	public ItemFolder() {
		setMaxStackSize(1);
		setUnlocalizedName("myst.folder");
		setCreativeTab(CreativeTabs.MISC);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(@Nonnull ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
		String name = InventoryFolder.getName(stack);
		if (name != null) {
			tooltip.add(name);
		}
	}

	@Override
	public void onUpdate(@Nonnull ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		if (worldIn.isRemote) {
			return;
		}
		initialize(worldIn, stack, entityIn);
	}

	@Override
	public void onCreated(@Nonnull ItemStack stack, World worldIn, EntityPlayer playerIn) {
		if (worldIn.isRemote) {
			return;
		}
		initialize(worldIn, stack, playerIn);
	}

	@Override
	@Nonnull
	public EnumRarity getRarity(@Nonnull ItemStack itemstack) {
		return EnumRarity.UNCOMMON;
	}

	@Override
	@Nonnull
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		if (worldIn.isRemote) {
			return ActionResult.newResult(EnumActionResult.PASS, playerIn.getHeldItem(handIn));
		}
		playerIn.openGui(Mystcraft.instance, ModGUIs.FOLDER.ordinal(), worldIn, MathHelper.floor(playerIn.posX + 0.5D), MathHelper.floor(playerIn.posY + 0.5D), MathHelper.floor(playerIn.posZ + 0.5D));
		return ActionResult.newResult(EnumActionResult.PASS, playerIn.getHeldItem(handIn));
	}

	private void initialize(World world, @Nonnull ItemStack itemstack, Entity entity) {}

	@Override
	@Nullable
	public String getDisplayName(EntityPlayer player, @Nonnull ItemStack itemstack) {
		return InventoryFolder.getName(itemstack);
	}

	@Override
	public void setDisplayName(EntityPlayer player, @Nonnull ItemStack itemstack, String name) {
		InventoryFolder.setName(itemstack, name);
	}

	@Override
	public boolean writeSymbol(EntityPlayer player, @Nonnull ItemStack itemstack, String symbol) {
		return InventoryFolder.writeSymbol(itemstack, symbol);
	}

	@Override
	@Nonnull
	public ItemStack removePage(EntityPlayer player, @Nonnull ItemStack folder, int index) {
		ItemStack itemstack = InventoryFolder.getItem(folder, index);
		InventoryFolder.removeItem(folder, index, true);
		return itemstack;
	}

	@Override
	public List<ItemStack> getPageList(EntityPlayer player, @Nonnull ItemStack itemstack) {
		return InventoryFolder.getItems(itemstack);
	}

	@Override
	@Nonnull
	public ItemStack setPage(EntityPlayer player, @Nonnull ItemStack folder, @Nonnull ItemStack page, int index) {
		return InventoryFolder.setItem(folder, index, page);
	}

	@Override
	@Nonnull
	public ItemStack addPage(EntityPlayer player, ItemStack folder, ItemStack page) {
		return InventoryFolder.addItem(folder, page);
	}

	@Override
	@Nonnull
	public ItemStack onLoad(@Nonnull ItemStack itemstack) {
		InventoryFolder.updatePages(itemstack);
		return itemstack;
	}
}
