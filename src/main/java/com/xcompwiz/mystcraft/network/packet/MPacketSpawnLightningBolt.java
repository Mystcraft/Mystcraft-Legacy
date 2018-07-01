package com.xcompwiz.mystcraft.network.packet;

import com.xcompwiz.mystcraft.api.util.Color;
import com.xcompwiz.mystcraft.entity.EntityLightningBoltAdv;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MPacketSpawnLightningBolt extends PacketBase<MPacketSpawnLightningBolt, MPacketSpawnLightningBolt> {

	private int entityId;
	private double x, y, z;
	private boolean colored;
	private float r, g, b;

	public MPacketSpawnLightningBolt() {}

	public MPacketSpawnLightningBolt(EntityLightningBoltAdv adv) {
		this.entityId = adv.getEntityId();
		this.x = adv.posX;
		this.y = adv.posY;
		this.z = adv.posZ;
		Color c = adv.getColor();
		if (c != null) {
			colored = true;
			r = c.r;
			g = c.g;
			b = c.b;
		} else {
			colored = false;
		}
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.entityId = buf.readInt();
		this.x = buf.readDouble();
		this.y = buf.readDouble();
		this.z = buf.readDouble();
		this.colored = buf.readBoolean();
		if (this.colored) {
			this.r = buf.readFloat();
			this.g = buf.readFloat();
			this.b = buf.readFloat();
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(this.entityId);
		buf.writeDouble(this.x);
		buf.writeDouble(this.y);
		buf.writeDouble(this.z);
		buf.writeBoolean(this.colored);
		if (this.colored) {
			buf.writeFloat(this.r);
			buf.writeFloat(this.g);
			buf.writeFloat(this.b);
		}
	}

	@Override
	public MPacketSpawnLightningBolt onMessage(MPacketSpawnLightningBolt message, MessageContext ctx) {
		playLightningBolt(message);
		return null;
	}

	@SideOnly(Side.CLIENT)
	private void playLightningBolt(MPacketSpawnLightningBolt p) {
		EntityLightningBoltAdv entity = new EntityLightningBoltAdv(Minecraft.getMinecraft().world, p.x, p.y, p.z, false);
		entity.setEntityId(entityId);
		if (p.colored) {
			entity.setColor(new Color(p.r, p.g, p.b));
		}
		Minecraft.getMinecraft().world.addWeatherEffect(entity);
	}

}
