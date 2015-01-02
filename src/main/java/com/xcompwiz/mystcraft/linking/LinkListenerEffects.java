package com.xcompwiz.mystcraft.linking;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.network.Packet;
import net.minecraft.server.MinecraftServer;

import com.xcompwiz.mystcraft.api.event.LinkEvent.LinkEventEnd;
import com.xcompwiz.mystcraft.api.event.LinkEvent.LinkEventStart;
import com.xcompwiz.mystcraft.api.linking.ILinkInfo;
import com.xcompwiz.mystcraft.data.Sounds;
import com.xcompwiz.mystcraft.entity.EntityLinkbook;
import com.xcompwiz.mystcraft.network.packet.MPacketParticles;
import com.xcompwiz.mystcraft.oldapi.internal.ILinkPropertyAPI;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class LinkListenerEffects {

	private static void spawnParticles(Entity entity) {
		Packet pkt = MPacketParticles.createPacket(entity, "link");
		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
		if (server != null) server.getConfigurationManager().sendPacketToAllPlayersInDimension(pkt, entity.worldObj.provider.dimensionId);
	}

	private static void playSound(Entity entity, ILinkInfo info) {
		if (entity instanceof EntityItem || entity instanceof EntityLinkbook) {
			entity.worldObj.playSoundAtEntity(entity, Sounds.POP, 0.8F, entity.worldObj.rand.nextFloat() * 0.2F + 0.9F);
		} else if (info.getFlag(ILinkPropertyAPI.FLAG_DISARM)) {
			entity.worldObj.playSoundAtEntity(entity, Sounds.DISARM, 0.8F, entity.worldObj.rand.nextFloat() * 0.2F + 0.9F);
		} else if (info.getProperty(ILinkPropertyAPI.PROP_SOUND) != null) {
			entity.worldObj.playSoundAtEntity(entity, info.getProperty(ILinkPropertyAPI.PROP_SOUND), 0.8F, entity.worldObj.rand.nextFloat() * 0.2F + 0.9F);
		} else if (info.getFlag(ILinkPropertyAPI.FLAG_FOLLOWING)) {
			entity.worldObj.playSoundAtEntity(entity, Sounds.FOLLOWING, 0.8F, entity.worldObj.rand.nextFloat() * 0.2F + 0.9F);
		} else if (info.getFlag(ILinkPropertyAPI.FLAG_INTRA_LINKING)) {
			entity.worldObj.playSoundAtEntity(entity, Sounds.INTRA_AGE, 0.8F, entity.worldObj.rand.nextFloat() * 0.2F + 0.9F);
		} else {
			entity.worldObj.playSoundAtEntity(entity, Sounds.LINK, 0.8F, entity.worldObj.rand.nextFloat() * 0.2F + 0.9F);
		}
	}

	@SubscribeEvent
	public void onLinkStart(LinkEventStart event) {
		spawnParticles(event.entity);
		playSound(event.entity, event.info);
	}

	@SubscribeEvent
	public void onLinkEnd(LinkEventEnd event) {
		spawnParticles(event.entity);
		playSound(event.entity, event.info);
	}
}
