package com.xcompwiz.mystcraft.item;

import io.netty.buffer.ByteBuf;

import java.util.Collection;
import java.util.List;
import java.util.Random;

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
import com.xcompwiz.mystcraft.data.ModItems;
import com.xcompwiz.mystcraft.inventory.ContainerNotebook;
import com.xcompwiz.mystcraft.inventory.InventoryNotebook;
import com.xcompwiz.mystcraft.network.NetworkUtils;
import com.xcompwiz.mystcraft.page.IItemPageCollection;
import com.xcompwiz.mystcraft.page.Page;
import com.xcompwiz.mystcraft.symbol.IAgeSymbol;
import com.xcompwiz.mystcraft.symbol.SymbolManager;
import com.xcompwiz.mystcraft.treasure.WeightProviderSymbolItem;
import com.xcompwiz.mystcraft.utility.WeightedItemSelector;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemNotebook extends Item implements IItemPageCollection, IItemWritable {

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
			//FIXME: NetworkUtils.displayGui(entityplayer, world, GuiID, itemstack);
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
	public boolean writeSymbol(EntityPlayer player, ItemStack itemstack, String symbol) {
		return InventoryNotebook.writeSymbol(itemstack, symbol);
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
	public ItemStack addPage(EntityPlayer player, ItemStack notebook, ItemStack page) {
		return InventoryNotebook.addItem(notebook, page);
	}

	@Override
	public List<ItemStack> getPages(EntityPlayer player, ItemStack itemstack) {
		return InventoryNotebook.getItems(itemstack);
	}

	//XXX: Generalize to allow for alternate rank sets (any rank, >=2, etc) 
	public static ItemStack generateBooster(Random rand, int verycommon, int common, int uncommon, int rare) {
		ItemStack notebook = new ItemStack(ModItems.notebook, 1, 0);
	
		Collection<IAgeSymbol> symbols_vc = SymbolManager.getSymbolsByRank(0);
		Collection<IAgeSymbol> symbols_c = SymbolManager.getSymbolsByRank(1);
		Collection<IAgeSymbol> symbols_uc = SymbolManager.getSymbolsByRank(2);
		Collection<IAgeSymbol> symbols_r = SymbolManager.getSymbolsByRank(3, null);
	
		for (int i = 0; i < verycommon; ++i) {
			IAgeSymbol symbol = WeightedItemSelector.getRandomItem(rand, symbols_vc, WeightProviderSymbolItem.instance);
			InventoryNotebook.addItem(notebook, Page.createSymbolPage(symbol.identifier()));
		}
		for (int i = 0; i < common; ++i) {
			IAgeSymbol symbol = WeightedItemSelector.getRandomItem(rand, symbols_c, WeightProviderSymbolItem.instance);
			InventoryNotebook.addItem(notebook, Page.createSymbolPage(symbol.identifier()));
		}
		for (int i = 0; i < uncommon; ++i) {
			IAgeSymbol symbol = WeightedItemSelector.getRandomItem(rand, symbols_uc, WeightProviderSymbolItem.instance);
			InventoryNotebook.addItem(notebook, Page.createSymbolPage(symbol.identifier()));
		}
		for (int i = 0; i < rare; ++i) {
			IAgeSymbol symbol = WeightedItemSelector.getRandomItem(rand, symbols_r, WeightProviderSymbolItem.instance);
			InventoryNotebook.addItem(notebook, Page.createSymbolPage(symbol.identifier()));
		}
		return notebook;
	}
}
