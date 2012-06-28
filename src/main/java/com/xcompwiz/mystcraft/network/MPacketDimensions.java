package com.xcompwiz.mystcraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.ArrayList;
import java.util.Collection;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.DimensionManager;

import com.xcompwiz.mystcraft.Mystcraft;

import cpw.mods.fml.common.network.internal.FMLProxyPacket;

public class MPacketDimensions extends MPacket {

	private static byte	packetId	= (byte) 10;

	@Override
	public byte getPacketType() {
		return packetId;
	}

	@Override
	public void handle(ByteBuf data, EntityPlayer player) {
		registerDimensions(data);
	}

	private static void registerDimensions(ByteBuf data) {
		if (Mystcraft.registeredDims == null) Mystcraft.registeredDims = new ArrayList<Integer>();
		int length = data.readInt();
		for (int i = 0; i < length; ++i) {
			int dimId = data.readInt();
			if (!Mystcraft.registeredDims.contains(dimId)) {
				Mystcraft.registeredDims.add(dimId);
				DimensionManager.registerDimension(dimId, Mystcraft.providerId);
			}
		}
	}

	public static FMLProxyPacket createPacket(Integer dim) {
		ArrayList<Integer> set = new ArrayList<Integer>();
		set.add(dim);
		return createPacket(set);
	}

	public static FMLProxyPacket createPacket(Collection<Integer> set) {
		ByteBuf data = Unpooled.buffer();

		data.writeByte(packetId);
		data.writeInt(set.size());
		for (Integer dimId : set)
			data.writeInt(dimId);

		return buildPacket(data);
	}

}
