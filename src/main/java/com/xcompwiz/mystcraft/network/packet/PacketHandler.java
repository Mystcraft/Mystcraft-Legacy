package com.xcompwiz.mystcraft.network.packet;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;

import com.xcompwiz.mystcraft.network.MystcraftPacketHandler;

import cpw.mods.fml.common.network.internal.FMLProxyPacket;

public abstract class PacketHandler {

	protected static Vec3 readCoordinates(ByteBuf data) {
		Vec3 coords = Vec3.createVectorHelper(data.readDouble(), data.readDouble(), data.readDouble());
		return coords;
	}

	public abstract void handle(ByteBuf data, EntityPlayer player);

	public static ByteBuf createDataBuffer(Class<? extends PacketHandler> handlerclass) {
		ByteBuf data = Unpooled.buffer();
		data.writeByte(MystcraftPacketHandler.getId(handlerclass));
		return data;
	}

	protected static FMLProxyPacket buildPacket(ByteBuf payload) {
		return new FMLProxyPacket(payload, MystcraftPacketHandler.CHANNEL);
	}
}
