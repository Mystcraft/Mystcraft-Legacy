package com.xcompwiz.mystcraft.network.packet;

import com.xcompwiz.mystcraft.Mystcraft;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MPacketConfigs extends PacketBase<MPacketConfigs, MPacketConfigs> {

	private boolean serverLabels;

	public MPacketConfigs() {}

	public static MPacketConfigs createPacket() {
		MPacketConfigs pkt = new MPacketConfigs();
		pkt.serverLabels = Mystcraft.serverLabels;
		return pkt;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.serverLabels = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeBoolean(this.serverLabels);
	}

	@Override
	public MPacketConfigs onMessage(MPacketConfigs message, MessageContext ctx) {
		Mystcraft.serverLabels = message.serverLabels;
		return null;
	}
}
