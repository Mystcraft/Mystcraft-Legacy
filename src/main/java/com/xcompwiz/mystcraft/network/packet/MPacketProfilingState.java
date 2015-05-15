package com.xcompwiz.mystcraft.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.StatCollector;

import com.xcompwiz.mystcraft.Mystcraft;
import com.xcompwiz.mystcraft.client.MystcraftClientProxy;

import cpw.mods.fml.common.network.internal.FMLProxyPacket;

public class MPacketProfilingState extends PacketBase {

	@Override
	public void handle(ByteBuf data, EntityPlayer player) {
		boolean running = data.readBoolean();
		if (running) {
			((MystcraftClientProxy) Mystcraft.sidedProxy).getNotificationGui().post(StatCollector.translateToLocal("myst.profiling.running.message"), StatCollector.translateToLocal("myst.profiling.running"));
		} else {
			((MystcraftClientProxy) Mystcraft.sidedProxy).getNotificationGui().post(StatCollector.translateToLocal("myst.profiling.complete.message"), StatCollector.translateToLocal("myst.profiling.complete"));
		}
	}

	public static FMLProxyPacket createPacket(boolean running) {
		ByteBuf data = PacketBase.createDataBuffer((Class<? extends PacketBase>) new Object() {}.getClass().getEnclosingClass());

		data.writeBoolean(running);

		return buildPacket(data);
	}

}
