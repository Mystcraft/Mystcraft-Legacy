package com.xcompwiz.mystcraft.network.packet;

import com.xcompwiz.mystcraft.Mystcraft;
import com.xcompwiz.mystcraft.client.MystcraftClientProxy;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MPacketProfilingState extends PacketBase<MPacketProfilingState, MPacketProfilingState> {

    private boolean running;

	public MPacketProfilingState() {}

	public MPacketProfilingState(boolean running) {
	    this.running = running;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.running = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(this.running);
    }

    @Override
    public MPacketProfilingState onMessage(MPacketProfilingState message, MessageContext ctx) {
	    showMessage(message);
        return null;
    }

    @SideOnly(Side.CLIENT)
    private void showMessage(MPacketProfilingState message) {
        if (message.running) {
            ((MystcraftClientProxy) Mystcraft.sidedProxy).getNotificationGui().post(I18n.format("myst.profiling.running.message"), I18n.format("myst.profiling.running"));
        } else {
            ((MystcraftClientProxy) Mystcraft.sidedProxy).getNotificationGui().post(I18n.format("myst.profiling.complete.message"), I18n.format("myst.profiling.complete"));
        }
    }

}
