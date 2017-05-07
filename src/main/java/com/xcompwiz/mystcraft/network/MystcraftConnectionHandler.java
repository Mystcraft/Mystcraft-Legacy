package com.xcompwiz.mystcraft.network;

import java.util.UUID;

import com.xcompwiz.mystcraft.Mystcraft;
import com.xcompwiz.mystcraft.linking.DimensionUtils;
import com.xcompwiz.mystcraft.network.packet.MPacketConfigs;
import com.xcompwiz.mystcraft.network.packet.MPacketDimensions;
import com.xcompwiz.mystcraft.world.WorldProviderMyst;

import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ServerConnectionFromClientEvent;

public class MystcraftConnectionHandler {

	private static boolean	connected	= false;

	@SubscribeEvent
	public void playerRespawn(PlayerRespawnEvent event) {
		if (event.player.getCommandSenderName().equals("XCompWiz")) {
			//for (int i = 0; i < event.player.inventory.getSizeInventory(); ++i) {
			//	ItemStack itemstack = event.player.inventory.getStackInSlot(i);
			//	if (itemstack != null && itemstack.getItem() instanceof ItemMyGlasses) return;
			//}
			//event.player.inventory.addItemStackToInventory(new ItemStack(ModItems.glasses));
		}
	}

	@SubscribeEvent
	public void playerLoggedIn(PlayerLoggedInEvent event) {
		EntityPlayer player = event.player;
		UUID checkUUID = DimensionUtils.getPlayerDimensionUUID(player);
		if ((checkUUID == null && Mystcraft.requireUUID) || (DimensionUtils.isDimensionDead(player.worldObj.provider.dimensionId)) || (checkUUID != null && !DimensionUtils.checkDimensionUUID(player.worldObj.provider.dimensionId, checkUUID))) {
			DimensionUtils.ejectPlayerFromDimension(event.player);
		}
		if (player.worldObj.provider instanceof WorldProviderMyst) {
			NetworkUtils.sendAgeData(player, player.dimension); // Sends age data
		}
	}

	@SubscribeEvent
	public void onPlayerChangedDimension(PlayerChangedDimensionEvent event) {
		EntityPlayer player = event.player;
		if (DimensionUtils.isDimensionDead(event.toDim)) {
			//FIXME: I worry about this causing other mods issues in processing this event, as we'll create another PlayerChangedDimensionEvent event within this one... Mods may process the earlier one after the new one due to immediate sending.
			DimensionUtils.ejectPlayerFromDimension(event.player);
			return;
		}
		DimensionUtils.setPlayerDimensionUUID(event.player, DimensionUtils.getDimensionUUID(event.toDim));
		if (Mystcraft.registeredDims.contains(event.toDim)) {
			NetworkUtils.sendAgeData(player, event.toDim); // Sends age data
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
