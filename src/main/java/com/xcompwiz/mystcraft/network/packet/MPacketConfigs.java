package com.xcompwiz.mystcraft.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;

import com.xcompwiz.mystcraft.Mystcraft;

import cpw.mods.fml.common.network.internal.FMLProxyPacket;

public class MPacketConfigs extends PacketHandler {

	@Override
	public void handle(ByteBuf data, EntityPlayer player) {
		Mystcraft.serverLabels = data.readBoolean();
	}

	public static FMLProxyPacket createPacket() {
		ByteBuf data = PacketHandler.createDataBuffer(MPacketConfigs.class);

		//TODO: Generalize?
		data.writeBoolean(Mystcraft.renderlabels);

		return buildPacket(data);
	}

}
