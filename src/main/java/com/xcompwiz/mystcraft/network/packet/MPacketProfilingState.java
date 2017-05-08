package com.xcompwiz.mystcraft.network.packet;

import com.xcompwiz.mystcraft.Mystcraft;
import com.xcompwiz.mystcraft.client.MystcraftClientProxy;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;

public class MPacketProfilingState extends PacketBase {

	@Override
	public void handle(ByteBuf data, EntityPlayer player) {
		boolean running = data.readBoolean();
		if (running) {
			((MystcraftClientProxy) Mystcraft.sidedProxy).getNotificationGui().post(I18n.format("myst.profiling.running.message"), I18n.format("myst.profiling.running"));
		} else {
			((MystcraftClientProxy) Mystcraft.sidedProxy).getNotificationGui().post(I18n.format("myst.profiling.complete.message"), I18n.format("myst.profiling.complete"));
		}
	}

	public static FMLProxyPacket createPacket(boolean running) {
		ByteBuf data = PacketBase.createDataBuffer((Class<? extends PacketBase>) new Object() {}.getClass().getEnclosingClass());

		data.writeBoolean(running);

		return buildPacket(data);
	}

}
