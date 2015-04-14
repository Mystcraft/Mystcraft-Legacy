package com.xcompwiz.mystcraft.item;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
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
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import com.xcompwiz.mystcraft.api.item.IItemOrderablePageProvider;
import com.xcompwiz.mystcraft.api.item.IItemPageCollection;
import com.xcompwiz.mystcraft.api.item.IItemRenameable;
import com.xcompwiz.mystcraft.client.gui.GuiHandlerManager;
import com.xcompwiz.mystcraft.client.gui.GuiInventoryFolder;
import com.xcompwiz.mystcraft.data.ModItems;
import com.xcompwiz.mystcraft.inventory.ContainerFolder;
import com.xcompwiz.mystcraft.nbt.NBTUtils;
import com.xcompwiz.mystcraft.network.NetworkUtils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemPortfolio extends Item implements IItemPageCollection, IItemRenameable {

	public static class GuiHandlerPortfolio extends GuiHandlerManager.GuiHandler {
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

	private static final int	GuiID	= GuiHandlerManager.registerGuiNetHandler(new GuiHandlerPortfolio());

	public ItemPortfolio() {
		setMaxStackSize(1);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister register) {
		this.itemIcon = register.registerIcon("mystcraft:portfolio");
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
		String name = this.getDisplayName(entityplayer, itemstack);
		if (name != null) {
			list.add(name);
		}
	}

	@Override
	public void onUpdate(ItemStack itemstack, World world, Entity entity, int i, boolean flag) {
		if (world.isRemote) { return; }
	}

	@Override
	public void onCreated(ItemStack itemstack, World world, EntityPlayer entityplayer) {
		if (world.isRemote) { return; }
	}

	@Override
	public EnumRarity getRarity(ItemStack itemstack) {
		return EnumRarity.uncommon;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer) {
		if (!world.isRemote) {
			NetworkUtils.displayGui(entityplayer, world, GuiID, itemstack);
		}
		return itemstack;
	}

	@Override
	public String getDisplayName(EntityPlayer player, ItemStack itemstack) {
		if (itemstack == null) return null;
		if (itemstack.stackTagCompound == null) itemstack.stackTagCompound = new NBTTagCompound();
		if (!itemstack.stackTagCompound.hasKey("Name")) return null;
		return itemstack.stackTagCompound.getString("Name");
	}

	@Override
	public void setDisplayName(EntityPlayer player, ItemStack itemstack, String name) {
		if (itemstack == null) return;
		if (itemstack.stackTagCompound == null) itemstack.stackTagCompound = new NBTTagCompound();
		if (name == null || name.equals("")) {
			itemstack.stackTagCompound.removeTag("Name");
		} else {
			itemstack.stackTagCompound.setString("Name", name);
		}
	}

	public static boolean isItemValid(ItemStack itemstack) {
		if (itemstack == null) return false;
		if (itemstack.getItem() == ModItems.page) return true;
		return false;
	}

	@Override
	public ItemStack remove(EntityPlayer player, ItemStack itemstack, ItemStack page) {
		if (itemstack == null) return null;
		if (page == null) return null;
		if (itemstack.stackTagCompound == null) itemstack.stackTagCompound = new NBTTagCompound();
		Collection<NBTTagCompound> compounds = NBTUtils.readTagCompoundCollection(itemstack.stackTagCompound.getTagList("Collection", Constants.NBT.TAG_COMPOUND), new LinkedList<NBTTagCompound>());
		NBTTagCompound nbt = new NBTTagCompound();
		int count = page.stackSize;
		page.stackSize = 1;
		page.writeToNBT(nbt);
		page.stackSize = 0;
		while (page.stackSize < count && compounds.remove(nbt)) {
			++page.stackSize;
		}
		if (page.stackSize == 0) return null;
		itemstack.stackTagCompound.setTag("Collection", NBTUtils.writeTagCompoundCollection(new NBTTagList(), compounds));
		return page;
	}

	@Override
	public ItemStack addPage(EntityPlayer player, ItemStack itemstack, ItemStack page) {
		if (itemstack == null) return page;
		if (page == null) return page;
		if (page.getItem() instanceof IItemPageCollection) {
			if (page.stackSize != 1) return page;
			IItemPageCollection otheritem = (IItemPageCollection) page.getItem();
			List<ItemStack> pages = otheritem.getItems(player, page);
			for (ItemStack p : pages) {
				ItemStack out = this.addPage(player, itemstack, otheritem.remove(player, page, p));
				if (out != null) otheritem.addPage(player, page, out);
			}
			return page;
		}
		if (page.getItem() instanceof IItemOrderablePageProvider) {
			if (page.stackSize != 1) return page;
			IItemOrderablePageProvider otheritem = (IItemOrderablePageProvider) page.getItem();
			List<ItemStack> pages = otheritem.getPageList(player, page);
			for (int i = 0; i < pages.size(); ++i) {
				ItemStack out = this.addPage(player, itemstack, otheritem.removePage(player, page, i));
				if (out != null) otheritem.addPage(player, page, out);
			}
			return page;
		}
		if (!isItemValid(page)) return page;
		if (itemstack.stackTagCompound == null) itemstack.stackTagCompound = new NBTTagCompound();
		Collection<NBTTagCompound> compounds = NBTUtils.readTagCompoundCollection(itemstack.stackTagCompound.getTagList("Collection", Constants.NBT.TAG_COMPOUND), new LinkedList<NBTTagCompound>());
		NBTTagCompound nbt = new NBTTagCompound();
		int count = page.stackSize;
		page.stackSize = 1;
		for (int i = 0; i < count; ++i) {
			page.writeToNBT(nbt);
			compounds.add(nbt);
		}
		itemstack.stackTagCompound.setTag("Collection", NBTUtils.writeTagCompoundCollection(new NBTTagList(), compounds));
		return null;
	}

	@Override
	public List<ItemStack> getItems(EntityPlayer player, ItemStack itemstack) {
		if (itemstack == null) return null;
		if (itemstack.stackTagCompound == null) itemstack.stackTagCompound = new NBTTagCompound();
		Collection<NBTTagCompound> compounds = NBTUtils.readTagCompoundCollection(itemstack.stackTagCompound.getTagList("Collection", Constants.NBT.TAG_COMPOUND), new LinkedList<NBTTagCompound>());
		List<ItemStack> items = new ArrayList<ItemStack>();
		for (NBTTagCompound nbt : compounds) {
			ItemStack page = ItemStack.loadItemStackFromNBT(nbt);
			items.add(page);
		}
		return items;
	}
}
