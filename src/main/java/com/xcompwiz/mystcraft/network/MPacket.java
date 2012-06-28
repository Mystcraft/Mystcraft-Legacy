package com.xcompwiz.mystcraft.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;

public abstract class MPacket {

	protected static Vec3 readCoordinates(ByteBuf data) {
		Vec3 coords = Vec3.createVectorHelper(data.readDouble(), data.readDouble(), data.readDouble());
		return coords;
	}

	public abstract void handle(ByteBuf data, EntityPlayer player);

	public abstract byte getPacketType();

	protected static FMLProxyPacket buildPacket(ByteBuf payload) {
		return new FMLProxyPacket(payload, MystcraftPacketHandler.CHANNEL);
	}
}
