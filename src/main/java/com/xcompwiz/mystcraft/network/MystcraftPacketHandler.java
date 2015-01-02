package com.xcompwiz.mystcraft.network;

import io.netty.buffer.ByteBuf;

import java.util.HashMap;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;

import com.xcompwiz.mystcraft.logging.LoggerUtils;
import com.xcompwiz.mystcraft.network.packet.PacketHandler;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLEventChannel;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientCustomPacketEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ServerCustomPacketEvent;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class MystcraftPacketHandler {

	public static final String										CHANNEL			= "mystcraft";
	public static FMLEventChannel									bus;

	private static HashMap<Byte, PacketHandler>						packethandlers	= new HashMap<Byte, PacketHandler>();
	private static HashMap<Class<? extends PacketHandler>, Byte>	idmap			= new HashMap<Class<? extends PacketHandler>, Byte>();

	public static void registerPacketHandler(PacketHandler handler, byte id) {
		if (packethandlers.get(id) != null) { throw new RuntimeException("Multiple id registrations for packet type on " + CHANNEL + " channel"); }
		packethandlers.put(id, handler);
		idmap.put(handler.getClass(), id);
	}

	public static byte getId(PacketHandler handler) {
		return getId(handler.getClass());
	}

	public static byte getId(Class<? extends PacketHandler> handlerclass) {
		if (!idmap.containsKey(handlerclass)) throw new RuntimeException("Attempted to get id for unregistered network message handler.");
		return idmap.get(handlerclass);
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
			if (packet == null || packet.payload() == null) { throw new RuntimeException("Empty packet sent to " + CHANNEL + " channel"); }
			ByteBuf data = packet.payload();
			byte type = data.readByte();

			try {
				PacketHandler handler = packethandlers.get(type);
				if (handler == null) { throw new RuntimeException("Unrecognized packet sent to " + CHANNEL + " channel"); }
				handler.handle(data, player);
			} catch (Exception e) {
				LoggerUtils.warn("PacketHandler: Failed to handle packet type " + type);
				LoggerUtils.warn(e.toString());
				e.printStackTrace();
			}
		} catch (Exception e) {
			LoggerUtils.warn("PacketHandler: Failed to read packet");
			LoggerUtils.warn(e.toString());
			e.printStackTrace();
		}
	}
}
