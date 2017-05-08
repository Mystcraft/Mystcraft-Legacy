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

public class ItemFolder extends Item implements IItemOrderablePageProvider, IItemWritable, IItemOnLoadable {

	//@SideOnly(Side.CLIENT)
	//protected IIcon				filledIcon;

	public ItemFolder() {
		setMaxStackSize(1);
		setUnlocalizedName("myst.folder");
		setCreativeTab(CreativeTabs.MISC);
	}
//
	//@SideOnly(Side.CLIENT)
	//@Override
	//public void registerIcons(IIconRegister register) {
	//	this.itemIcon = register.registerIcon("mystcraft:folder");
	//	this.filledIcon = register.registerIcon("mystcraft:folder_filled");
	//}
//
	///**
	// * Returns the icon index of the stack given as argument.
	// */
	//@Override
	//@SideOnly(Side.CLIENT)
	//public IIcon getIconIndex(ItemStack itemstack) {
	//	return InventoryFolder.getLargestSlotId(itemstack) == -1 ? this.itemIcon : this.filledIcon;
	//}

	@Override
	public void addInformation(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean advancedTooltip) {
		String name = InventoryFolder.getName(itemstack);
		if (name != null) {
			list.add(name);
		}
	}

	@Override
	public void onUpdate(ItemStack itemstack, World world, Entity entity, int i, boolean flag) {
		if (world.isRemote) { return; }
		initialize(world, itemstack, entity);
	}

	@Override
	public void onCreated(ItemStack itemstack, World world, EntityPlayer entityplayer) {
		if (world.isRemote) { return; }
		initialize(world, itemstack, entityplayer);
	}

	@Override
	public EnumRarity getRarity(ItemStack itemstack) {
		return EnumRarity.UNCOMMON;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		if (worldIn.isRemote) return ActionResult.newResult(EnumActionResult.PASS, playerIn.getHeldItem(handIn));
		playerIn.openGui(Mystcraft.instance, ModGUIs.FOLDER.ordinal(), worldIn, MathHelper.floor(playerIn.posX + 0.5D), MathHelper.floor(playerIn.posY + 0.5D), MathHelper.floor(playerIn.posZ + 0.5D));
		return super.onItemRightClick(worldIn, playerIn, handIn);
	}

	private void initialize(World world, ItemStack itemstack, Entity entity) {}

	@Override
	public String getDisplayName(EntityPlayer player, ItemStack itemstack) {
		return InventoryFolder.getName(itemstack);
	}

	@Override
	public void setDisplayName(EntityPlayer player, ItemStack itemstack, String name) {
		InventoryFolder.setName(itemstack, name);
	}

	@Override
	public boolean writeSymbol(EntityPlayer player, ItemStack itemstack, String symbol) {
		return InventoryFolder.writeSymbol(itemstack, symbol);
	}

	@Override
	public ItemStack removePage(EntityPlayer player, ItemStack folder, int index) {
		ItemStack itemstack = InventoryFolder.getItem(folder, index);
		InventoryFolder.removeItem(folder, index);
		return itemstack;
	}

	@Override
	public List<ItemStack> getPageList(EntityPlayer player, ItemStack itemstack) {
		return InventoryFolder.getItems(itemstack);
	}

	@Override
	public ItemStack setPage(EntityPlayer player, ItemStack folder, ItemStack page, int index) {
		return InventoryFolder.setItem(folder, index, page);
	}

	@Override
	public ItemStack addPage(EntityPlayer player, ItemStack folder, ItemStack page) {
		return InventoryFolder.addItem(folder, page);
	}

	@Override
	public ItemStack onLoad(ItemStack itemstack) {
		InventoryFolder.updatePages(itemstack);
		return itemstack;
	}
}
