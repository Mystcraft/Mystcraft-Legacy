package com.xcompwiz.mystcraft.network;

import java.util.Iterator;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;

import com.xcompwiz.mystcraft.Mystcraft;
import com.xcompwiz.mystcraft.client.gui.GuiHandlerManager;
import com.xcompwiz.mystcraft.inventory.InventoryUtils;
import com.xcompwiz.mystcraft.logging.LoggerUtils;
import com.xcompwiz.mystcraft.network.packet.MPacketAgeData;
import com.xcompwiz.mystcraft.network.packet.MPacketOpenWindow;
import com.xcompwiz.mystcraft.world.agedata.AgeData;

public final class NetworkUtils {

	public static void displayGui(EntityPlayer entityplayer, World worldObj, int guiID, int i, int j, int k) {
		if (!(entityplayer instanceof EntityPlayerMP)) return;
		EntityPlayerMP player = (EntityPlayerMP) entityplayer;
		GuiHandlerManager.GuiHandler handler = GuiHandlerManager.getGuiNetHandler(guiID);
		if (handler == null) {
			LoggerUtils.warn(String.format("Unrecognized gui type id %s", guiID));
			return;
		}
		TileEntity tileentity = handler.getTileEntity(player, worldObj, i, j, k);
		if (tileentity == null) {
			LoggerUtils.warn(String.format("GUI Handler %s did not return a tile entity.", guiID));
			return;
		}
		player.openContainer = handler.getContainer(player, worldObj, tileentity, i, j, k);
		if (player.openContainer == null) {
			LoggerUtils.warn(String.format("GUI Handler %s did not produce a container.", guiID));
			return;
		}

		int currentWindowId = getNextWindowId(player);
		MystcraftPacketHandler.bus.sendTo(MPacketOpenWindow.createPacket(currentWindowId, guiID, tileentity), player);
		player.openContainer.windowId = currentWindowId;
		player.openContainer.addCraftingToCrafters(player);
	}

	public static void displayGui(EntityPlayer entityplayer, World worldObj, int guiID, ItemStack itemstack) {
		if (!(entityplayer instanceof EntityPlayerMP)) return;
		EntityPlayerMP player = (EntityPlayerMP) entityplayer;

		GuiHandlerManager.GuiHandler handler = GuiHandlerManager.getGuiNetHandler(guiID);
		if (handler == null) {
			LoggerUtils.warn(String.format("Unrecognized gui type id %s", guiID));
			return;
		}

		int slot = InventoryUtils.findInInventory(player.inventory, itemstack);
		if (slot < 0) {
			LoggerUtils.warn(String.format("Attempted to open GUI for item not in inventory (%s)", guiID));
			return;
		}
		player.openContainer = handler.getContainer(player, worldObj, itemstack, slot);
		if (player.openContainer == null) {
			LoggerUtils.warn(String.format("GUI Handler %s did not produce a container.", guiID));
			return;
		}

		int currentWindowId = getNextWindowId(player);
		MystcraftPacketHandler.bus.sendTo(MPacketOpenWindow.createPacket(currentWindowId, guiID, (byte) slot), player);
		player.openContainer.windowId = currentWindowId;
		player.openContainer.addCraftingToCrafters(player);
	}

	public static void displayGui(EntityPlayer entityplayer, World worldObj, int guiID, Entity entity) {
		if (!(entityplayer instanceof EntityPlayerMP)) return;
		EntityPlayerMP player = (EntityPlayerMP) entityplayer;

		GuiHandlerManager.GuiHandler handler = GuiHandlerManager.getGuiNetHandler(guiID);
		if (handler == null) {
			LoggerUtils.warn(String.format("Unrecognized gui type id %s", guiID));
			return;
		}
		player.openContainer = handler.getContainer(player, worldObj, entity);
		if (player.openContainer == null) {
			LoggerUtils.warn(String.format("GUI Handler %s did not produce a container.", guiID));
			return;
		}

		int currentWindowId = getNextWindowId(player);
		MystcraftPacketHandler.bus.sendTo(MPacketOpenWindow.createPacket(currentWindowId, guiID, entity), player);
		player.openContainer.windowId = currentWindowId;
		player.openContainer.addCraftingToCrafters(player);
	}

	public static void sendAgeData(World worldObj, EntityPlayer player, int ageUID) {
		if (!(player instanceof EntityPlayerMP)) return;
		if (!Mystcraft.registeredDims.contains(ageUID)) return;
		if (AgeData.getAge(ageUID, false) == null) return;
		((EntityPlayerMP) player).playerNetServerHandler.sendPacket(MPacketAgeData.getDataPacket(ageUID));
	}

	private static int getNextWindowId(EntityPlayerMP player) {
		player.getNextWindowId();
		return player.currentWindowId;
	}

	public static void sendMessageToAdmins(IChatComponent chatcomponent) {
		Iterator iterator = MinecraftServer.getServer().getConfigurationManager().playerEntityList.iterator();
		while (iterator.hasNext()) {
			EntityPlayer entityplayer = (EntityPlayer) iterator.next();
			if (MinecraftServer.getServer().getConfigurationManager().func_152596_g(entityplayer.getGameProfile())) {
				entityplayer.addChatMessage(chatcomponent);
			}
		}

		MinecraftServer.getServer().addChatMessage(chatcomponent);
	}

	public static void sendMessageToPlayersInWorld(IChatComponent chatcomponent, int dimensionid) {
		Iterator iterator = MinecraftServer.getServer().getConfigurationManager().playerEntityList.iterator();
		while (iterator.hasNext()) {
			EntityPlayer entityplayer = (EntityPlayer) iterator.next();
			if (entityplayer.dimension == dimensionid) {
				entityplayer.addChatMessage(chatcomponent);
			}
		}

		MinecraftServer.getServer().addChatMessage(chatcomponent);
	}

	public static void sendMessageToPlayers(IChatComponent chatcomponent) {
		Iterator iterator = MinecraftServer.getServer().getConfigurationManager().playerEntityList.iterator();
		while (iterator.hasNext()) {
			((EntityPlayer) iterator.next()).addChatMessage(chatcomponent);
		}

		MinecraftServer.getServer().addChatMessage(chatcomponent);
	}
}
