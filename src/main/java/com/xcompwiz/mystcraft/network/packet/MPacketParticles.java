package com.xcompwiz.mystcraft.network.packet;

import com.xcompwiz.mystcraft.Mystcraft;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;

public class MPacketParticles extends PacketBase {

	@Override
	public void handle(ByteBuf data, EntityPlayer player) {
		spawnParticles(player.worldObj, data);
	}

	private static void spawnParticles(World worldObj, ByteBuf data) {
		Vec3 coords = readCoordinates(data);
		String particle = ByteBufUtils.readUTF8String(data);
		spawnParticles(worldObj, coords, particle);
	}

	//XXX: (Helper) Should this be made into a general helper? 
	private static void spawnParticles(World worldObj, Vec3 coords, String particle) {
		for (int particles = 0; particles < 50; ++particles) {
			float f = worldObj.rand.nextFloat() - worldObj.rand.nextFloat();
			float f1 = worldObj.rand.nextFloat() * 2;
			float f2 = worldObj.rand.nextFloat() - worldObj.rand.nextFloat();
			Mystcraft.sidedProxy.spawnParticle(particle, coords.xCoord + f, coords.yCoord + f1, coords.zCoord + f2, 0, 0, 0);
		}
	}

	public static FMLProxyPacket createPacket(Entity entity, String particle) {
		ByteBuf data = PacketBase.createDataBuffer((Class<? extends PacketBase>) new Object() {}.getClass().getEnclosingClass());

		data.writeDouble(entity.posX);
		data.writeDouble(entity.posY);
		data.writeDouble(entity.posZ);
		ByteBufUtils.writeUTF8String(data, particle);

		return buildPacket(data);
	}
}
