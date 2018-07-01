package com.xcompwiz.mystcraft.network.packet;

import com.xcompwiz.mystcraft.Mystcraft;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MPacketParticles extends PacketBase<MPacketParticles, MPacketParticles> {

	private double x, y, z;
	private String particle;

	public MPacketParticles() {}

	//Hellfire> DO NOT USE THIS FOR VANILLA PARTICLES!
	public MPacketParticles(double x, double y, double z, String particle) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.particle = particle;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.x = buf.readDouble();
		this.y = buf.readDouble();
		this.z = buf.readDouble();
		this.particle = readString(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeDouble(this.x);
		buf.writeDouble(this.y);
		buf.writeDouble(this.z);
		writeString(buf, this.particle);
	}

	@Override
	public MPacketParticles onMessage(MPacketParticles message, MessageContext ctx) {
		playParticles(message);
		return null;
	}

	@SideOnly(Side.CLIENT)
	private void playParticles(MPacketParticles p) {
		World world = Minecraft.getMinecraft().world;
		for (int particles = 0; particles < 50; ++particles) {
			float f = world.rand.nextFloat() - world.rand.nextFloat();
			float f1 = world.rand.nextFloat() * 2;
			float f2 = world.rand.nextFloat() - world.rand.nextFloat();
			Mystcraft.sidedProxy.spawnParticle(particle, p.x + f, p.y + f1, p.z + f2, 0, 0, 0);
		}
	}

}
