package com.xcompwiz.mystcraft.network;

import com.xcompwiz.mystcraft.Mystcraft;
import com.xcompwiz.mystcraft.network.packet.MPacketAgeData;
import com.xcompwiz.mystcraft.world.agedata.AgeData;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.common.FMLCommonHandler;

public final class NetworkUtils {

	public static void sendAgeData(EntityPlayer player, int ageUID) {
		if (!(player instanceof EntityPlayerMP))
			return;
		if (!Mystcraft.registeredDims.contains(ageUID))
			return;
		if (AgeData.getAge(ageUID, false) == null)
			return;
		MystcraftPacketHandler.CHANNEL.sendTo(new MPacketAgeData(ageUID), (EntityPlayerMP) player);
	}

	public static void sendMessageToAdmins(ITextComponent chatcomponent) {
		MinecraftServer serv = FMLCommonHandler.instance().getMinecraftServerInstance();
		if (serv != null) {
			for (EntityPlayerMP mp : serv.getPlayerList().getPlayers()) {
				if (serv.getPlayerList().getOppedPlayers().getPermissionLevel(mp.getGameProfile()) >= serv.getOpPermissionLevel()) {
					mp.sendMessage(chatcomponent);
				}
			}
			serv.sendMessage(chatcomponent);
		}
	}

	public static void sendMessageToPlayersInWorld(ITextComponent chatcomponent, int dimensionid) {
		MinecraftServer serv = FMLCommonHandler.instance().getMinecraftServerInstance();
		if (serv != null) {
			for (EntityPlayerMP mp : serv.getPlayerList().getPlayers()) {
				if (mp.world.provider.getDimension() == dimensionid) {
					mp.sendMessage(chatcomponent);
				}
			}
			serv.sendMessage(chatcomponent);
		}
	}

	public static void sendMessageToPlayers(ITextComponent chatcomponent) {
		MinecraftServer serv = FMLCommonHandler.instance().getMinecraftServerInstance();
		if (serv != null) {
			for (EntityPlayerMP mp : serv.getPlayerList().getPlayers()) {
				mp.sendMessage(chatcomponent);
			}
			serv.sendMessage(chatcomponent);
		}
	}
}
