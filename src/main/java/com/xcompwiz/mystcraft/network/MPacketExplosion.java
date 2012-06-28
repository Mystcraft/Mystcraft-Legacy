package com.xcompwiz.mystcraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.ChunkPosition;

import com.xcompwiz.mystcraft.explosion.ExplosionAdvanced;
import com.xcompwiz.mystcraft.explosion.effects.ExplosionEffect;

import cpw.mods.fml.common.network.internal.FMLProxyPacket;

public class MPacketExplosion extends MPacket {

	private static final byte	packetId	= (byte) 100;

	@Override
	public void handle(ByteBuf data, EntityPlayer player) {
		int count;

		double posX = data.readDouble();
		double posY = data.readDouble();
		double posZ = data.readDouble();
		float size = data.readFloat();
		int x = (int) posX;
		int y = (int) posY;
		int z = (int) posZ;

		count = data.readInt();
		List<ExplosionEffect> effects = new ArrayList<ExplosionEffect>();
		for (int i = 0; i < count; ++i) {
			ExplosionEffect effect = ExplosionAdvanced.getEffectById(data.readByte());
			if (effect != null) effects.add(effect);
		}

		count = data.readInt();
		List<ChunkPosition> blocks = new ArrayList<ChunkPosition>();
		for (int i = 0; i < count; ++i) {
			blocks.add(new ChunkPosition(data.readByte() + x, data.readByte() + y, data.readByte() + z));
		}
		ExplosionAdvanced explosion = new ExplosionAdvanced(player.worldObj, (Entity) null, posX, posY, posZ, size);
		explosion.blocks = blocks;
		explosion.effects = effects;
		explosion.doExplosionB(true);
	}

	public static FMLProxyPacket createPacket(EntityPlayer player, ExplosionAdvanced explosion) {
		ByteBuf data = Unpooled.buffer();

		data.writeByte(packetId);
		data.writeDouble(explosion.explosionX);
		data.writeDouble(explosion.explosionY);
		data.writeDouble(explosion.explosionZ);
		data.writeFloat(explosion.explosionSize);
		data.writeInt(explosion.effects.size());
		for (ExplosionEffect effect : explosion.effects) {
			data.writeByte(ExplosionAdvanced.getEffectId(effect));
		}
		int x = (int) explosion.explosionX;
		int y = (int) explosion.explosionY;
		int z = (int) explosion.explosionZ;
		data.writeInt(explosion.blocks.size());
		for (ChunkPosition coords : explosion.blocks) {
			int var7 = coords.chunkPosX - x;
			int var8 = coords.chunkPosY - y;
			int var9 = coords.chunkPosZ - z;
			data.writeByte(var7);
			data.writeByte(var8);
			data.writeByte(var9);
		}

		return buildPacket(data);
	}

	@Override
	public byte getPacketType() {
		return packetId;
	}

}
