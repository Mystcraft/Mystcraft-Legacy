package com.xcompwiz.mystcraft.network;

import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;

import com.xcompwiz.mystcraft.Mystcraft;
import com.xcompwiz.mystcraft.data.ModItems;
import com.xcompwiz.mystcraft.item.ItemMyGlasses;
import com.xcompwiz.mystcraft.world.WorldProviderMyst;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ServerConnectionFromClientEvent;

public class MystcraftConnectionHandler {

	private static boolean	connected	= false;

	@SubscribeEvent
	public void playerRespawn(PlayerRespawnEvent event) {
		if (event.player.getCommandSenderName().equals("XCompWiz")) {
			for (int i = 0; i < event.player.inventory.getSizeInventory(); ++i) {
				ItemStack itemstack = event.player.inventory.getStackInSlot(i);
				if (itemstack != null && itemstack.getItem() instanceof ItemMyGlasses) return;
			}
			event.player.inventory.addItemStackToInventory(new ItemStack(ModItems.glasses));
		}
	}

	@SubscribeEvent
	public void playerLoggedIn(PlayerLoggedInEvent event) {
		EntityPlayer player = event.player;
		if (player.worldObj.provider instanceof WorldProviderMyst) {
			NetworkUtils.sendAgeData(player.worldObj, player, player.dimension); // Sends age data
		}
	}

	@SubscribeEvent
	public void connectionOpened(ClientConnectedToServerEvent event) {
		connected = true;
		Mystcraft.clientStorage = ((NetHandlerPlayClient) event.handler).mapStorageOrigin;
	}

	@SubscribeEvent
	public void connectionOpened(ServerConnectionFromClientEvent event) {
		event.manager.scheduleOutboundPacket(MPacketDimensions.createPacket(Mystcraft.registeredDims));
		event.manager.scheduleOutboundPacket(MPacketConfigs.createPacket());
	}

	@SubscribeEvent
	public void connectionClosed(ClientDisconnectionFromServerEvent event) {
		if (connected) {
			connected = false;
			Mystcraft.clientStorage = null;
			Mystcraft.serverLabels = Mystcraft.renderlabels;
			MinecraftServer mcServer = MinecraftServer.getServer();
			if (mcServer == null || mcServer.isServerStopped()) {
				Mystcraft.unregisterDimensions();
			}
		}
	}
}
