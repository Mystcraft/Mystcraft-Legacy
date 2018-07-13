package com.xcompwiz.mystcraft.inventory;

import com.xcompwiz.mystcraft.Mystcraft;
import com.xcompwiz.mystcraft.block.BlockWritingDesk;
import com.xcompwiz.mystcraft.client.gui.GuiBook;
import com.xcompwiz.mystcraft.client.gui.GuiBookBinder;
import com.xcompwiz.mystcraft.client.gui.GuiInkMixer;
import com.xcompwiz.mystcraft.client.gui.GuiInventoryFolder;
import com.xcompwiz.mystcraft.client.gui.GuiLinkModifier;
import com.xcompwiz.mystcraft.client.gui.GuiVillagerShop;
import com.xcompwiz.mystcraft.client.gui.GuiWritingDesk;
import com.xcompwiz.mystcraft.data.ModGUIs;
import com.xcompwiz.mystcraft.entity.EntityLinkbook;
import com.xcompwiz.mystcraft.item.ItemFolder;
import com.xcompwiz.mystcraft.item.ItemLinking;
import com.xcompwiz.mystcraft.item.ItemPortfolio;
import com.xcompwiz.mystcraft.tileentity.TileEntityBookBinder;
import com.xcompwiz.mystcraft.tileentity.TileEntityBookRotateable;
import com.xcompwiz.mystcraft.tileentity.TileEntityDesk;
import com.xcompwiz.mystcraft.tileentity.TileEntityInkMixer;
import com.xcompwiz.mystcraft.tileentity.TileEntityLinkModifier;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {

	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		// TEs
		if (id == ModGUIs.BOOK_BINDER.ordinal()) {
			TileEntityBookBinder tileentity = (TileEntityBookBinder) world.getTileEntity(new BlockPos(x, y, z));
			return new ContainerBookBinder(player.inventory, tileentity);
		}
		if (id == ModGUIs.BOOK_DISPLAY.ordinal()) {
			TileEntityBookRotateable tileentity = (TileEntityBookRotateable) player.world.getTileEntity(new BlockPos(x, y, z));
			return new ContainerBook(player.inventory, tileentity);
		}
		if (id == ModGUIs.INK_MIXER.ordinal()) {
			TileEntityInkMixer tileentity = (TileEntityInkMixer) world.getTileEntity(new BlockPos(x, y, z));
			return new ContainerInkMixer(player.inventory, tileentity);
		}
		if (id == ModGUIs.LINK_MODIFIER.ordinal()) {
			TileEntityLinkModifier tileentity = (TileEntityLinkModifier) world.getTileEntity(new BlockPos(x, y, z));
			return new ContainerLinkModifier(player.inventory, tileentity);
		}
		if (id == ModGUIs.WRITING_DESK.ordinal()) {
			TileEntityDesk tileentity = BlockWritingDesk.getTileEntity(world, new BlockPos(x, y, z));
			return new ContainerWritingDesk(player.inventory, tileentity);
		}

		// Items
		if (id == ModGUIs.BOOK.ordinal()) {
			int slot = player.inventory.currentItem;
			ItemStack current = player.inventory.getCurrentItem();
			if (!current.isEmpty() && current.getItem() instanceof ItemLinking) {
				return new ContainerBook(player.inventory, slot);
			}
		}
		if (id == ModGUIs.FOLDER.ordinal()) {
			int slot = player.inventory.currentItem;
			ItemStack current = player.inventory.getCurrentItem();
			if (!current.isEmpty() && current.getItem() instanceof ItemFolder) {
				return new ContainerFolder(player.inventory, slot);
			}
		}
		if (id == ModGUIs.PORTFOLIO.ordinal()) {
			int slot = player.inventory.currentItem;
			ItemStack current = player.inventory.getCurrentItem();
			if (!current.isEmpty() && current.getItem() instanceof ItemPortfolio) {
				return new ContainerFolder(player.inventory, slot);
			}
		}

		// Entities
		if (id == ModGUIs.BOOK_ENTITY.ordinal()) {
			Entity entity = Mystcraft.sidedProxy.getEntityByID(player.world, x);
			if (entity != null && entity instanceof EntityLinkbook) {
				return new ContainerBook(player.inventory, (EntityLinkbook) entity);
			}
		}
		if (id == ModGUIs.VILLAGER.ordinal()) {
			Entity entity = Mystcraft.sidedProxy.getEntityByID(player.world, x);
			if (entity != null && entity instanceof EntityVillager) {
				return new ContainerVillagerShop(player, (EntityVillager) entity);
			}
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		// TEs
		if (id == ModGUIs.BOOK_BINDER.ordinal()) {
			TileEntityBookBinder tileentity = (TileEntityBookBinder) player.world.getTileEntity(new BlockPos(x, y, z));
			return new GuiBookBinder(player.inventory, tileentity);
		}
		if (id == ModGUIs.BOOK_DISPLAY.ordinal()) {
			TileEntityBookRotateable tileentity = (TileEntityBookRotateable) player.world.getTileEntity(new BlockPos(x, y, z));
			return new GuiBook(player.inventory, tileentity);
		}
		if (id == ModGUIs.INK_MIXER.ordinal()) {
			TileEntityInkMixer tileentity = (TileEntityInkMixer) world.getTileEntity(new BlockPos(x, y, z));
			return new GuiInkMixer(player.inventory, tileentity);
		}
		if (id == ModGUIs.LINK_MODIFIER.ordinal()) {
			TileEntityLinkModifier tileentity = (TileEntityLinkModifier) world.getTileEntity(new BlockPos(x, y, z));
			return new GuiLinkModifier(player.inventory, tileentity);
		}
		if (id == ModGUIs.WRITING_DESK.ordinal()) {
			TileEntityDesk tileentity = BlockWritingDesk.getTileEntity(world, new BlockPos(x, y, z));
			return new GuiWritingDesk(player.inventory, tileentity);
		}

		// Items
		if (id == ModGUIs.BOOK.ordinal()) {
			int slot = player.inventory.currentItem;
			ItemStack current = player.inventory.getCurrentItem();
			if (!current.isEmpty() && current.getItem() instanceof ItemLinking) {
				return new GuiBook(player.inventory, slot);
			}
		}
		if (id == ModGUIs.FOLDER.ordinal()) {
			int slot = player.inventory.currentItem;
			ItemStack current = player.inventory.getCurrentItem();
			if (!current.isEmpty() && current.getItem() instanceof ItemFolder) {
				return new GuiInventoryFolder(player.inventory, slot);
			}
		}
		if (id == ModGUIs.PORTFOLIO.ordinal()) {
			int slot = player.inventory.currentItem;
			ItemStack current = player.inventory.getCurrentItem();
			if (!current.isEmpty() && current.getItem() instanceof ItemPortfolio) {
				return new GuiInventoryFolder(player.inventory, slot);
			}
		}

		// Entities
		if (id == ModGUIs.BOOK_ENTITY.ordinal()) {
			Entity entity = Mystcraft.sidedProxy.getEntityByID(player.world, x);
			if (entity != null && entity instanceof EntityLinkbook) {
				return new GuiBook(player.inventory, (EntityLinkbook) entity);
			}
		}
		if (id == ModGUIs.VILLAGER.ordinal()) {
			Entity entity = Mystcraft.sidedProxy.getEntityByID(player.world, x);
			if (entity != null && entity instanceof EntityVillager) {
				return new GuiVillagerShop(player, (EntityVillager) entity);
			}
		}
		return null;
	}

}
