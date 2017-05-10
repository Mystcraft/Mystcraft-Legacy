package com.xcompwiz.mystcraft.network;

import java.util.Iterator;

import com.xcompwiz.mystcraft.Mystcraft;
import com.xcompwiz.mystcraft.network.packet.MPacketAgeData;
import com.xcompwiz.mystcraft.world.agedata.AgeData;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IChatComponent;

public final class NetworkUtils {

	public static void sendAgeData(EntityPlayer player, int ageUID) {
		if (!(player instanceof EntityPlayerMP)) return;
		if (!Mystcraft.registeredDims.contains(ageUID)) return;
		if (AgeData.getAge(ageUID, false) == null) return;
		((EntityPlayerMP) player).playerNetServerHandler.sendPacket(MPacketAgeData.getDataPacket(ageUID));
	}

	public static void sendMessageToAdmins(IChatComponent chatcomponent) {
		Iterator iterator = MinecraftServer.getServer().getPlayerList().playerEntityList.iterator();
		while (iterator.hasNext()) {
			EntityPlayer entityplayer = (EntityPlayer) iterator.next();
			if (MinecraftServer.getServer().getPlayerList().func_152596_g(entityplayer.getGameProfile())) {
				entityplayer.addChatMessage(chatcomponent);
			}
		}

		MinecraftServer.getServer().addChatMessage(chatcomponent);
	}

	public static void sendMessageToPlayersInWorld(IChatComponent chatcomponent, int dimensionid) {
		Iterator iterator = MinecraftServer.getServer().getPlayerList().playerEntityList.iterator();
		while (iterator.hasNext()) {
			EntityPlayer entityplayer = (EntityPlayer) iterator.next();
			if (entityplayer.dimension == dimensionid) {
				entityplayer.addChatMessage(chatcomponent);
			}
		}

		MinecraftServer.getServer().addChatMessage(chatcomponent);
	}

	public static void sendMessageToPlayers(IChatComponent chatcomponent) {
		Iterator iterator = MinecraftServer.getServer().getPlayerList().playerEntityList.iterator();
		while (iterator.hasNext()) {
			((EntityPlayer) iterator.next()).addChatMessage(chatcomponent);
		}

		MinecraftServer.getServer().addChatMessage(chatcomponent);
	}
}
