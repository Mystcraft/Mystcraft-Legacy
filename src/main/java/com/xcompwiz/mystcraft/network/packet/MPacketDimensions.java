package com.xcompwiz.mystcraft.network.packet;

import java.util.Collection;
import java.util.HashSet;

import com.xcompwiz.mystcraft.Mystcraft;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;

public class MPacketDimensions extends PacketBase {

	@Override
	public void handle(ByteBuf data, EntityPlayer player) {
		registerDimensions(data);
	}

	private static void registerDimensions(ByteBuf data) {
		if (Mystcraft.registeredDims == null) Mystcraft.registeredDims = new HashSet<Integer>();
		int length = data.readInt();
		for (int i = 0; i < length; ++i) {
			int dimId = data.readInt();
			if (!Mystcraft.registeredDims.contains(dimId)) {
				Mystcraft.registeredDims.add(dimId);
				DimensionManager.registerDimension(dimId, Mystcraft.dimensionType);
			}
		}
	}

	public static FMLProxyPacket createPacket(Integer dim) {
		HashSet<Integer> set = new HashSet<Integer>();
		set.add(dim);
		return createPacket(set);
	}

	public static FMLProxyPacket createPacket(Collection<Integer> set) {
		ByteBuf data = PacketBase.createDataBuffer((Class<? extends PacketBase>) new Object() {}.getClass().getEnclosingClass());

		data.writeInt(set.size());
		for (Integer dimId : set)
			data.writeInt(dimId);

		return buildPacket(data);
	}

}
