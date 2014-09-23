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
import net.minecraft.world.World;

import com.xcompwiz.mystcraft.client.gui.GuiHandlerManager;
import com.xcompwiz.mystcraft.client.gui.GuiInventoryNotebook;
import com.xcompwiz.mystcraft.inventory.ContainerNotebook;
import com.xcompwiz.mystcraft.inventory.InventoryNotebook;
import com.xcompwiz.mystcraft.network.NetworkUtils;
import com.xcompwiz.mystcraft.oldapi.PositionableItem;
import com.xcompwiz.mystcraft.page.IItemPageProvider;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemNotebook extends Item implements IItemPageProvider, IItemWritable {

	public static class GuiHandlerNotebook extends GuiHandlerManager.GuiHandler {
		@Override
		public Container getContainer(EntityPlayerMP player, World worldObj, ItemStack itemstack, int slot) {
			return new ContainerNotebook(player.inventory, slot);
		}

		@Override
		@SideOnly(Side.CLIENT)
		public GuiScreen getGuiScreen(EntityPlayer player, ByteBuf data) {
			byte slot = data.readByte();
			ItemStack itemstack = player.inventory.getStackInSlot(slot);
			if (itemstack != null) { return new GuiInventoryNotebook(player.inventory, slot); }
			return null;
		}
	}

	private static final int	GuiID	= GuiHandlerManager.registerGuiNetHandler(new GuiHandlerNotebook());

	public ItemNotebook() {
		setMaxStackSize(1);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister register) {
		this.itemIcon = register.registerIcon("mystcraft:notebook");
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
		String name = InventoryNotebook.getName(itemstack);
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
			InventoryNotebook.updatePages(itemstack);
			NetworkUtils.displayGui(entityplayer, world, GuiID, itemstack);
		}
		return itemstack;
	}

	private void initialize(World world, ItemStack itemstack, Entity entity) {}

	@Override
	public String getDisplayName(EntityPlayer player, ItemStack itemstack) {
		return InventoryNotebook.getName(itemstack);
	}

	@Override
	public void setDisplayName(EntityPlayer player, ItemStack itemstack, String name) {
		InventoryNotebook.setName(itemstack, name);
	}

	@Override
	public boolean writeSymbol(EntityPlayer player, ItemStack itemstack, String symbol, ItemStack paper_feeder) {
		return InventoryNotebook.writeSymbol(itemstack, symbol, paper_feeder);
	}

	@Override
	public ItemStack removePage(EntityPlayer player, ItemStack notebook, int index) {
		ItemStack itemstack = InventoryNotebook.getItem(notebook, index);
		InventoryNotebook.removeItem(notebook, index);
		return itemstack;
	}

	@Override
	public List<ItemStack> getPageList(EntityPlayer player, ItemStack itemstack) {
		return InventoryNotebook.getItems(itemstack);
	}

	@Override
	public List<PositionableItem> getPagesForSurface(EntityPlayer player, ItemStack notebook) {
		return InventoryNotebook.getPositionableItems(notebook);
	}

	@Override
	public ItemStack addPage(EntityPlayer player, ItemStack notebook, ItemStack page) {
		return InventoryNotebook.addItem(notebook, page);
	}

	@Override
	public ItemStack addPage(ItemStack notebook, ItemStack page, float x, float y) {
		return InventoryNotebook.addItemAt(notebook, page, x, y);
	}

	@Override
	public void sort(ItemStack itemstack, SortType type, short width) {
		InventoryNotebook.sort(itemstack, type, width);
	}
}
