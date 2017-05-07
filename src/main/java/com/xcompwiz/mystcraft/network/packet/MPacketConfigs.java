package com.xcompwiz.mystcraft.network.packet;

import com.xcompwiz.mystcraft.Mystcraft;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;

public class MPacketConfigs extends PacketBase {

	@Override
	public void handle(ByteBuf data, EntityPlayer player) {
		Mystcraft.serverLabels = data.readBoolean();
	}

	public static FMLProxyPacket createPacket() {
		ByteBuf data = PacketBase.createDataBuffer((Class<? extends PacketBase>) new Object() {}.getClass().getEnclosingClass());

		//TODO: Generalize?
		data.writeBoolean(Mystcraft.renderlabels);

		return buildPacket(data);
	}

}
