package com.xcompwiz.mystcraft.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;

import com.xcompwiz.mystcraft.api.util.Color;
import com.xcompwiz.mystcraft.entity.EntityLightningBoltAdv;

import cpw.mods.fml.common.network.internal.FMLProxyPacket;

public class MPacketSpawnLightningBolt extends PacketBase {

	@Override
	public void handle(ByteBuf data, EntityPlayer player) {
		int entityId = data.readInt();
		double posX = data.readDouble();
		double posY = data.readDouble();
		double posZ = data.readDouble();
		Color color = null;
		if (data.readBoolean()) {
			color = new Color(data.readFloat(), data.readFloat(), data.readFloat());
		}
		EntityLightningBoltAdv entity = new EntityLightningBoltAdv(player.worldObj, posX, posY, posZ);
		entity.setEntityId(entityId);
		if (color != null) entity.setColor(color);
		player.worldObj.addWeatherEffect(entity);
	}

	public static FMLProxyPacket createPacket(EntityLightningBoltAdv entity) {
		ByteBuf data = PacketBase.createDataBuffer((Class<? extends PacketBase>) new Object() {}.getClass().getEnclosingClass());

		try {
			data.writeInt(entity.getEntityId());
			data.writeDouble(entity.posX);
			data.writeDouble(entity.posY);
			data.writeDouble(entity.posZ);
			Color color = entity.getColor();
			if (color == null) {
				data.writeBoolean(false);
			} else {
				data.writeBoolean(true);
				data.writeFloat(color.r);
				data.writeFloat(color.g);
				data.writeFloat(color.b);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return buildPacket(data);
	}
}
