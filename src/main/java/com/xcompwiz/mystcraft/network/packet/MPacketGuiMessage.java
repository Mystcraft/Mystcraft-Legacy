package com.xcompwiz.mystcraft.network.packet;

import com.xcompwiz.mystcraft.logging.LoggerUtils;
import com.xcompwiz.mystcraft.network.IGuiMessageHandler;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IThreadListener;
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
		if (ctx.side.isClient()) {
			thePlayer = clientPlayer();
		} else {
			thePlayer = ctx.getServerHandler().player;
		}

		IThreadListener itl;
		if (ctx.side.isClient()) {
			itl = clientListener();
		} else {
			itl = thePlayer.world.getMinecraftServer();
		}

		// We should process all of the container checks, etc later, so we can be sure they are set (avoid race conditions)
		itl.addScheduledTask(() -> {
			if (thePlayer.openContainer == null || thePlayer.openContainer.windowId != message.windowId) {
				LoggerUtils.info("%b %s == %d", thePlayer.openContainer == null, (thePlayer.openContainer == null ? "null" : "" + thePlayer.openContainer.windowId), message.windowId);
				return;
			}
			if (!thePlayer.openContainer.getCanCraft(thePlayer)) { //!thePlayer.openContainer.playerList.contains(player);
				return;
			}
			if (thePlayer.openContainer instanceof IGuiMessageHandler) {
				((IGuiMessageHandler) thePlayer.openContainer).processMessage(thePlayer, message.tag);
			}
		});
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
