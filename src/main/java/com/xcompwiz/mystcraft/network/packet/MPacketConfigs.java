package com.xcompwiz.mystcraft.network.packet;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.EntityPlayer;

import com.xcompwiz.mystcraft.Mystcraft;

import cpw.mods.fml.common.network.internal.FMLProxyPacket;

public class MPacketConfigs extends MPacket {

	private static byte	packetId	= (byte) 25;

	@Override
	public byte getPacketType() {
		return packetId;
	}

	@Override
	public void handle(ByteBuf data, EntityPlayer player) {
		Mystcraft.serverLabels = data.readBoolean();
	}

	public static FMLProxyPacket createPacket() {
		ByteBuf data = Unpooled.buffer();

		data.writeByte(packetId);
		data.writeBoolean(Mystcraft.renderlabels);

		return buildPacket(data);
	}

}
