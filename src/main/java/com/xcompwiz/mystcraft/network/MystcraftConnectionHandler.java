package com.xcompwiz.mystcraft.network;

import java.util.UUID;

import com.xcompwiz.mystcraft.Mystcraft;
import com.xcompwiz.mystcraft.linking.DimensionUtils;
import com.xcompwiz.mystcraft.network.packet.MPacketConfigs;
import com.xcompwiz.mystcraft.network.packet.MPacketDimensions;
import com.xcompwiz.mystcraft.world.WorldProviderMyst;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.storage.SaveDataMemoryStorage;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ServerConnectionFromClientEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MystcraftConnectionHandler {

	private static boolean connected = false;

	@SubscribeEvent
	public void playerRespawn(PlayerRespawnEvent event) {
		if (event.player.getName().equals("XCompWiz")) {
			//for (int i = 0; i < event.player.inventory.getSizeInventory(); ++i) {
			//	ItemStack itemstack = event.player.inventory.getStackInSlot(i);
			//	if (itemstack != null && itemstack.getBook() instanceof ItemMyGlasses) return;
			//}
			//event.player.inventory.addItemStackToInventory(new ItemStack(ModItems.glasses));
		}
	}

	@SubscribeEvent
	public void playerLoggedIn(PlayerLoggedInEvent event) {
		EntityPlayer player = event.player;
		UUID checkUUID = DimensionUtils.getPlayerDimensionUUID(player);
		if ((checkUUID == null && Mystcraft.requireUUID) || (DimensionUtils.isDimensionDead(player.world.provider.getDimension())) || (checkUUID != null && !DimensionUtils.checkDimensionUUID(player.world.provider.getDimension(), checkUUID))) {
			DimensionUtils.scheduleEjectPlayerFromDimension(event.player);
		} else if (player.world.provider instanceof WorldProviderMyst) {
			NetworkUtils.sendAgeData(player, player.dimension); // Sends age data
		}
	}

	@SubscribeEvent
	public void onPlayerChangedDimension(PlayerChangedDimensionEvent event) {
		EntityPlayer player = event.player;
		if (DimensionUtils.isDimensionDead(event.toDim)) {
			DimensionUtils.scheduleEjectPlayerFromDimension(event.player);
			return;
		}
		DimensionUtils.setPlayerDimensionUUID(event.player, DimensionUtils.getDimensionUUID(event.toDim));
		if (Mystcraft.registeredDims.contains(event.toDim)) {
			NetworkUtils.sendAgeData(player, event.toDim); // Sends age data
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void connectionOpened(ClientConnectedToServerEvent event) {
		connected = true;
		Mystcraft.clientStorage = new SaveDataMemoryStorage();
	}

	@SubscribeEvent
	public void onPlJoin(ServerConnectionFromClientEvent event) {
		NetHandlerPlayServer handler = (NetHandlerPlayServer) event.getHandler();
		EntityPlayerMP player = handler.player;
		NetHandlerPlayServer temp = player.connection;
		player.connection = handler; //XXX: This is a bit of a hack to ensure we have something to send through
		MystcraftPacketHandler.CHANNEL.sendTo(new MPacketDimensions(Mystcraft.registeredDims), player);
		MystcraftPacketHandler.CHANNEL.sendTo(MPacketConfigs.createPacket(), player);
		player.connection = temp;
	}

	@SubscribeEvent
	public void connectionClosed(ClientDisconnectionFromServerEvent event) {
		if (connected) {
			connected = false;
			Mystcraft.clientStorage = null;
			Mystcraft.serverLabels = Mystcraft.renderlabels;
			MinecraftServer mcServer = FMLCommonHandler.instance().getMinecraftServerInstance();
			if (mcServer == null || mcServer.isServerStopped()) {
				Mystcraft.unregisterDimensions();
			}
		}
	}
}
