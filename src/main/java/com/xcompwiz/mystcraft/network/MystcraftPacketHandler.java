package com.xcompwiz.mystcraft.network;

import io.netty.buffer.ByteBuf;

import java.util.HashMap;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;

import com.xcompwiz.mystcraft.logging.LoggerUtils;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLEventChannel;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientCustomPacketEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ServerCustomPacketEvent;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class MystcraftPacketHandler {

	public static final String				CHANNEL			= "mystcraft";
	public static FMLEventChannel			bus;

	private static HashMap<Byte, MPacket>	packethandlers	= new HashMap<Byte, MPacket>();

	public static void registerPacketHandler(MPacket mPacket) {
		if (packethandlers.get(mPacket.getPacketType()) != null) { throw new RuntimeException("Multiple id registrations for packet type on Mystcraft channel"); }
		packethandlers.put(mPacket.getPacketType(), mPacket);
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onPacketData(ClientCustomPacketEvent event) {
		FMLProxyPacket pkt = event.packet;

		onPacketData(event.manager, pkt, Minecraft.getMinecraft().thePlayer);
	}

	@SubscribeEvent
	public void onPacketData(ServerCustomPacketEvent event) {
		FMLProxyPacket pkt = event.packet;

		onPacketData(event.manager, pkt, ((NetHandlerPlayServer) event.handler).playerEntity);
	}

	public void onPacketData(NetworkManager manager, FMLProxyPacket packet, EntityPlayer player) {
		try {
			if (packet == null || packet.payload() == null) { throw new RuntimeException("Empty packet sent to Mystcraft channel"); }
			ByteBuf data = packet.payload();
			byte type = data.readByte();

			try {
				MPacket handler = packethandlers.get(type);
				if (handler == null) { throw new RuntimeException("Unrecognized packet sent to Mystcraft channel"); }
				handler.handle(data, player);
			} catch (Exception e) {
				LoggerUtils.warn("PacketHandler: Failed to handle packet type " + type);
				LoggerUtils.warn(e.getMessage());
				e.printStackTrace();
			}
		} catch (Exception e) {
			LoggerUtils.warn("PacketHandler: Failed to read packet");
			LoggerUtils.warn(e.getMessage());
			e.printStackTrace();
		}
	}
}
