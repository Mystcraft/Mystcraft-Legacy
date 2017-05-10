package com.xcompwiz.mystcraft.linking;

import com.xcompwiz.mystcraft.api.event.LinkEvent.LinkEventEnd;
import com.xcompwiz.mystcraft.api.event.LinkEvent.LinkEventStart;
import com.xcompwiz.mystcraft.api.hook.LinkPropertyAPI;
import com.xcompwiz.mystcraft.api.linking.ILinkInfo;
import com.xcompwiz.mystcraft.data.Sounds;
import com.xcompwiz.mystcraft.entity.EntityLinkbook;
import com.xcompwiz.mystcraft.network.MystcraftPacketHandler;
import com.xcompwiz.mystcraft.network.packet.MPacketParticles;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class LinkListenerEffects {

	private static void spawnParticles(Entity entity) {
		MystcraftPacketHandler.CHANNEL.sendToDimension(new MPacketParticles(entity.posX, entity.posY, entity.posZ, "link"), entity.world.provider.getDimension());
	}

	private static void playSound(Entity entity, ILinkInfo info) {
		if (entity instanceof EntityItem || entity instanceof EntityLinkbook) {
			entity.world.playSound(null, entity.posX, entity.posY, entity.posZ, Sounds.SOUND_LINK_POP, SoundCategory.PLAYERS, 0.8F, entity.world.rand.nextFloat() * 0.2F + 0.9F);
		} else if (info.getFlag(LinkPropertyAPI.FLAG_DISARM)) {
			entity.world.playSound(null, entity.posX, entity.posY, entity.posZ, Sounds.SOUND_LINK_DISARM, SoundCategory.PLAYERS, 0.8F, entity.world.rand.nextFloat() * 0.2F + 0.9F);
		} else if (info.getProperty(LinkPropertyAPI.PROP_SOUND) != null) {
			SoundEvent ev = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(info.getProperty(LinkPropertyAPI.PROP_SOUND)));
			if(ev != null) {
				entity.world.playSound(null, entity.posX, entity.posY, entity.posZ, ev, SoundCategory.PLAYERS, 0.8F, entity.world.rand.nextFloat() * 0.2F + 0.9F);
			}
		} else if (info.getFlag(LinkPropertyAPI.FLAG_FOLLOWING)) {
			entity.world.playSound(null, entity.posX, entity.posY, entity.posZ, Sounds.SOUND_LINK_FOLLOWING, SoundCategory.PLAYERS, 0.8F, entity.world.rand.nextFloat() * 0.2F + 0.9F);
		} else if (info.getFlag(LinkPropertyAPI.FLAG_INTRA_LINKING)) {
			entity.world.playSound(null, entity.posX, entity.posY, entity.posZ, Sounds.SOUND_LINK_INTRA_LINK, SoundCategory.PLAYERS, 0.8F, entity.world.rand.nextFloat() * 0.2F + 0.9F);
		} else {
			entity.world.playSound(null, entity.posX, entity.posY, entity.posZ, Sounds.SOUND_LINK_LINK, SoundCategory.PLAYERS, 0.8F, entity.world.rand.nextFloat() * 0.2F + 0.9F);
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
