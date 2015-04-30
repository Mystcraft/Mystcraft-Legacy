package com.xcompwiz.mystcraft.item;

import io.netty.buffer.ByteBuf;

import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import com.xcompwiz.mystcraft.api.item.IItemOrderablePageProvider;
import com.xcompwiz.mystcraft.api.item.IItemWritable;
import com.xcompwiz.mystcraft.client.gui.GuiHandlerManager;
import com.xcompwiz.mystcraft.client.gui.GuiInventoryFolder;
import com.xcompwiz.mystcraft.inventory.ContainerFolder;
import com.xcompwiz.mystcraft.inventory.InventoryFolder;
import com.xcompwiz.mystcraft.network.NetworkUtils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemFolder extends Item implements IItemOrderablePageProvider, IItemWritable {

	public static class GuiHandlerFolder extends GuiHandlerManager.GuiHandler {
		@Override
		public Container getContainer(EntityPlayerMP player, World worldObj, ItemStack itemstack, int slot) {
			return new ContainerFolder(player.inventory, slot);
		}

		@Override
		@SideOnly(Side.CLIENT)
		public GuiScreen getGuiScreen(EntityPlayer player, ByteBuf data) {
			byte slot = data.readByte();
			ItemStack itemstack = player.inventory.getStackInSlot(slot);
			if (itemstack != null) { return new GuiInventoryFolder(player.inventory, slot); }
			return null;
		}
	}

	private static final int	GuiID	= GuiHandlerManager.registerGuiNetHandler(new GuiHandlerFolder());

	@SideOnly(Side.CLIENT)
	protected IIcon				filledIcon;

	public ItemFolder() {
		setMaxStackSize(1);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister register) {
		this.itemIcon = register.registerIcon("mystcraft:folder");
		this.filledIcon = register.registerIcon("mystcraft:folder_filled");
	}

	/**
	 * Returns the icon index of the stack given as argument.
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconIndex(ItemStack itemstack) {
		return InventoryFolder.getLargestSlotId(itemstack) == -1 ? this.itemIcon : this.filledIcon;
	}

	/**
	 * If this function returns true (or the item is damageable), the ItemStack's NBT tag will be sent to the client.
	 */
	@Override
	public boolean getShareTag() {
		return true;
	}

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
		return EnumRarity.uncommon;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer) {
		if (!world.isRemote) {
			InventoryFolder.updatePages(itemstack);
			NetworkUtils.displayGui(entityplayer, world, GuiID, itemstack);
		}
		return itemstack;
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
}
