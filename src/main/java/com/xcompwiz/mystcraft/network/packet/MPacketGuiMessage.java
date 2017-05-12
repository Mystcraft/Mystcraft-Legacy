package com.xcompwiz.mystcraft.network.packet;

import com.xcompwiz.mystcraft.network.IGuiMessageHandler;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MPacketGuiMessage extends PacketBase<MPacketGuiMessage, MPacketGuiMessage> {

	private NBTTagCompound tag;
	private int windowId;

	public MPacketGuiMessage() {}

	public MPacketGuiMessage(int windowId, NBTTagCompound tag) {
		this.windowId = windowId;
		this.tag = tag;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.windowId = buf.readInt();
		this.tag = readTag(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(this.windowId);
		writeTag(buf, this.tag);
	}

	@Override
	public MPacketGuiMessage onMessage(MPacketGuiMessage message, MessageContext ctx) {
		EntityPlayer thePlayer;
		if(ctx.side.isClient()) {
			thePlayer = clientPlayer();
		} else {
			thePlayer = ctx.getServerHandler().playerEntity;
		}
		if(thePlayer.openContainer == null || thePlayer.openContainer.windowId != message.windowId) {
			return null;
		}
		if(!thePlayer.openContainer.getCanCraft(thePlayer)) { //!thePlayer.openContainer.playerList.contains(player); - the fck is wrong with this implementation... whatever.
			return null;
		}
		if(thePlayer.openContainer instanceof IGuiMessageHandler) {
			IThreadListener itl;
			if(ctx.side.isClient()) {
				itl = clientListener();
			} else {
				itl = thePlayer.world.getMinecraftServer();
			}
			itl.addScheduledTask(() -> ((IGuiMessageHandler) thePlayer.openContainer).processMessage(thePlayer, message.tag));
		}
		return null;
	}

	@SideOnly(Side.CLIENT)
	private EntityPlayer clientPlayer() {
		return Minecraft.getMinecraft().player;
	}

	@SideOnly(Side.CLIENT)
	private IThreadListener clientListener() {
		return Minecraft.getMinecraft();
	}

}
