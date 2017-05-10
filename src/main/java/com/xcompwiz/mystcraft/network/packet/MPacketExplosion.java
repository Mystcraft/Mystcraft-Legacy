package com.xcompwiz.mystcraft.network.packet;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.xcompwiz.mystcraft.explosion.ExplosionAdvanced;
import com.xcompwiz.mystcraft.explosion.effects.ExplosionEffect;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MPacketExplosion extends PacketBase<MPacketExplosion, MPacketExplosion> {

	private double x, y, z;
	private float size;
	private List<Byte> effectIds = new LinkedList<>();
	private List<BlockPos> positions = new LinkedList<>();

	public MPacketExplosion() {}

	public MPacketExplosion(ExplosionAdvanced exp) {
		this.x = exp.explosionX;
		this.y = exp.explosionY;
		this.z = exp.explosionZ;
		this.size = exp.explosionSize;
		for (ExplosionEffect eff : exp.effects) {
			effectIds.add(ExplosionAdvanced.getEffectId(eff));
		}
		BlockPos offset = new BlockPos(x, y, z);
		for (BlockPos pos : exp.blocks) {
			positions.add(pos.subtract(offset));
		}
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.x = buf.readDouble();
		this.y = buf.readDouble();
		this.z = buf.readDouble();
		this.size = buf.readFloat();

		int s = buf.readInt();
		for (int i = 0; i < s; i++) {
			effectIds.add(buf.readByte());
		}
		s = buf.readInt();
		for (int i = 0; i < s; i++) {
			positions.add(readPos(buf));
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeDouble(x);
		buf.writeDouble(y);
		buf.writeDouble(z);
		buf.writeFloat(size);
		buf.writeInt(effectIds.size());
		for (Byte b : effectIds) {
			buf.writeByte(b);
		}
		BlockPos offset = new BlockPos(x, y, z);
		buf.writeInt(positions.size());
		for (BlockPos pos : positions) {
			writePos(buf, pos.add(offset));
		}
	}

	@Override
	public MPacketExplosion onMessage(MPacketExplosion message, MessageContext ctx) {
		playExplosion(message);
		return null;
	}

	@SideOnly(Side.CLIENT)
	private void playExplosion(MPacketExplosion message) {
		List<ExplosionEffect> effects = new ArrayList<>();
		for (Byte b : message.effectIds) {
			ExplosionEffect effect = ExplosionAdvanced.getEffectById(b);
			if (effect != null) {
				effects.add(effect);
			}
		}
		ExplosionAdvanced adv = new ExplosionAdvanced(Minecraft.getMinecraft().world, null, message.x, message.y, message.z, message.size);
		adv.blocks = message.positions;
		adv.effects = effects;
		adv.doExplosionB(true);
	}

}
