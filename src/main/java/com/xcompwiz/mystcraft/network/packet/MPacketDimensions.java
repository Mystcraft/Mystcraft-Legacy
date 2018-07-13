package com.xcompwiz.mystcraft.network.packet;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import com.xcompwiz.mystcraft.Mystcraft;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MPacketDimensions extends PacketBase<MPacketDimensions, MPacketDimensions> {

	private List<Integer> dimIds = new LinkedList<>();

	public MPacketDimensions() {}

	public MPacketDimensions(Integer... ids) {
		this.dimIds = Arrays.asList(ids);
	}

	public MPacketDimensions(Collection<Integer> ids) {
		dimIds.addAll(ids);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		int length = buf.readInt();
		for (int i = 0; i < length; i++) {
			this.dimIds.add(buf.readInt());
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(dimIds.size());
		for (int id : dimIds) {
			buf.writeInt(id);
		}
	}

	@Override
	public MPacketDimensions onMessage(MPacketDimensions message, MessageContext ctx) {
		if (Mystcraft.registeredDims == null) {
			Mystcraft.registeredDims = new HashSet<>();
		}
		for (int id : message.dimIds) {
			if (!Mystcraft.registeredDims.contains(id)) {
				Mystcraft.registeredDims.add(id);
				DimensionManager.registerDimension(id, Mystcraft.dimensionType);
			}
		}
		return null;
	}

}
